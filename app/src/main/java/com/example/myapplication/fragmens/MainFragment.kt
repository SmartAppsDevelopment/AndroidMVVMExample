package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.helper.showLog
import com.example.myapplication.helper.showToast
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.viewmodel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    private val viewModel by activityViewModels<MainFragmentViewModel>()

    private var currentIndex:Int = -1
    private var currentCountry = "US"

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = resources.getStringArray(R.array.countrynames)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

            binding.autoCompletetxt.setAdapter(adapter)
            binding.autoCompletetxt.setOnItemClickListener { _, _, i, _ ->
                showLog( "onViewCreated: ")
                currentIndex = i
            }
            binding.btnsearch.setOnClickListener {
                viewModel.viewModelScope.launch {
                    if (!TextUtils.isEmpty(binding.tveText.text.toString())) {
                        if (validateCountry() != null) {
                            viewModel.searchAge(SendResponseModel().apply {
                                userName = binding.tveText.text.toString()
                                viewModel.currUserName = userName
                                country = currentCountry
                            })
                        } else {
                            requireContext().showToast("Select Country")

                        }
                    } else {
                        requireContext().showToast("Enter Name")
                    }
                }
            }

        observeData()
        binding.tveText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.btnsearch.callOnClick()
                return@OnEditorActionListener true
            }
            false
        })
        binding.autoCompletetxt.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.btnsearch.callOnClick()
                return@OnEditorActionListener true
            }
            false
        })
        binding.maincontaner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.autoCompletetxt.isFocused) {
                    val outRect = Rect()
                    binding.autoCompletetxt.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        binding.autoCompletetxt.clearFocus()
                        /// requireContext().showToast("outside click ")
                    }
                }
            }
            false
        }
    }

    private fun validateCountry(): String? {
        return try {
            currentCountry = resources.getStringArray(R.array.countrycodes)[currentIndex]
            currentCountry
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiUpdates.collectLatest {resModel->
                    when (resModel) {
                        is ResponseModel.Error -> {
                            showLog( "observeData: Error")
                        }
                        is ResponseModel.Idle -> {
                            showLog( "observeData: Idle")
                        }
                        is ResponseModel.Loading -> {
                            showProgressDialog()
                            showLog( "observeData: Loading")
                        }
                        is ResponseModel.Success -> {
                            hideProgress()
                            val dir = MainFragmentDirections.actionMainFragmentToResultFragment()
                            showLog(resModel.data!!.size.toString())
                            if (resModel.data.isNotEmpty()) {
                                val list = resModel.data.map {
                                    it!!
                                }.toTypedArray()
                                showLog(list.size.toString())
                                dir.transfereddata = list
                                findNavController().navigate(dir)
                                viewModel.markIdleState()
                            } else {
                                requireContext().showToast("No Data Found ")
                            }
                            showLog( "observeData: Success")
                        }
                    }

                }
            }

        }
    }

}