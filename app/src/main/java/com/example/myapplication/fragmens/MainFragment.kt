package com.example.myapplication.fragmens

import android.app.ProgressDialog
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.helper.showToast
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.viewmodel.MainFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MainFragment : Fragment() {
    private val TAG = "MainFragment"
    lateinit var binding: FragmentMainBinding
    val viewmodel by sharedViewModel<MainFragmentViewModel>()
    val isBindingInit = this::binding.isInitialized
    var progress: ProgressDialog? = null
    var currentIndex = -1
    var currentCountry = "US"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_main, container, false)
        progress = ProgressDialog(context)
        progress?.setMessage("Loading...")
        binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater,
            R.layout.fragment_main,
            null,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //// binding.tveText.setText(viewmodel.currUserName)
        val items = resources.getStringArray(R.array.countrynames)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        if (true) {
            binding.autoCompletetxt.setAdapter(adapter)
            binding.autoCompletetxt.setOnItemClickListener { adapterView, view, i, l ->
                Log.e(TAG, "onViewCreated: ")

                currentIndex = i
            }
            binding.btnsearch.setOnClickListener {
                viewmodel.viewModelScope.launch {
                    if (!TextUtils.isEmpty(binding.tveText.text.toString())) {
                        if (validateCountry() != null) {
                            val dfdf = viewmodel.searchAge(SendResponseModel().apply {
                                userName = binding.tveText.text.toString()
                                viewmodel.currUserName = userName
                                country = currentCountry
                                /// viewmodel.currentIndex
                            })
                        } else {
                            requireContext().showToast("Select Country")

                        }
                    } else {
                        requireContext().showToast("Enter Name")
                    }

                    Log.e("TAG", "onViewCreated: ")
                }
            }
        }
        observeData()
        binding.tveText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.btnsearch.callOnClick()
                return@OnEditorActionListener true
            }
            false
        })
        binding.autoCompletetxt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.btnsearch.callOnClick()
                return@OnEditorActionListener true
            }
            false
        })

        binding.maincontaner.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.autoCompletetxt.isFocused()) {
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
        try {
            currentCountry = resources.getStringArray(R.array.countrycodes)[currentIndex]
            return currentCountry
        } catch (e: Exception) {
            return null
        }
    }

    fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewmodel.uiUpdates.collectLatest {
                    when (it) {
                        is ResponseModel.Error -> {
                            Log.e(TAG, "observeData: Error")
                        }
                        is ResponseModel.Idle -> {
                            Log.e(TAG, "observeData: Idle")
                        }
                        is ResponseModel.Loading -> {
                            showDialog()
                            Log.e(TAG, "observeData: Loading")
                        }
                        is ResponseModel.Success -> {
                            dismissDialog()
                            val dir = MainFragmentDirections.actionMainFragmentToResultFragment()
                            if (it.data!!.size > 0) {
                                val list = it.data!!.map {
                                    it!!
                                }.toTypedArray()
                                dir.transfereddata = list
                                findNavController().navigate(dir)
                                viewmodel.markIdleStsate()
                            } else {
                                requireContext().showToast("No Data Found ")
                            }
                            Log.e(TAG, "observeData: Success")
                        }
                    }

                }
            }

        }
    }

    fun showDialog() {
        viewmodel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                progress?.show()
            }
        }
    }

    fun dismissDialog() {
        viewmodel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                progress?.hide()
            }
        }
    }

}