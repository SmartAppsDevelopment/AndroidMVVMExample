package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.myapplication.R
import com.example.myapplication.adapters.HistoryFragmentAdapter
import com.example.myapplication.databinding.FragmentHistoryBinding
import com.example.myapplication.helper.*
import com.example.myapplication.pojos.UserData
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.example.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history),
        (UserData) -> Unit {
    private val viewModel by viewModels<HistoryFragmentViewModel>()
    private var adapter = HistoryFragmentAdapter().apply {
        delUserCallback = this@HistoryFragment
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.rv.adapter = adapter
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiUpdates.collectLatest {
                    when (it) {
                        is ResponseModel.Error -> {
                            showLog("Error")
                        }
                        is ResponseModel.Idle -> {
                            showLog("Ideal")
                        }
                        is ResponseModel.Loading -> {
                            showDialog()
                            showLog("Loading")
                        }
                        is ResponseModel.Success -> {
                            dismissDialog()
                            if ((it.data.isNullOrEmpty()) and (it.data!!.isEmpty())) {
                                requireContext().showToast("No Data Found")
                                adapter.submitList(null)

                            } else {
                                adapter.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }
        viewModel.viewModelScope.launch {
            viewModel.getDataFromLocalDb()
        }
    }

    private fun showDialog() {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                showProgressDialog()
            }
        }
    }

    private fun dismissDialog() {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                hideProgress()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.historymenu, menu)
        //////  super.onCreateOptionsMenu(menu, inflater)
    }

    override  fun onOptionsItemSelected(item: MenuItem): Boolean {
        showProgressDialog("Creating Pdf")
        viewModel.viewModelScope.launch {
            delay(1000)
            PdfManager.createPDF(adapter.currentList, requireContext())
        }.invokeOnCompletion {
            hideProgress()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun invoke(userData: UserData) {
        viewModel.delUser(userData)
    }
}