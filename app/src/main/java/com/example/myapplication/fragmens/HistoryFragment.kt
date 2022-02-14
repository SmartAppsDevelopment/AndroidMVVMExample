package com.example.myapplication.fragmens

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.adapters.HistoryFragmnetAdapter
import com.example.myapplication.adapters.ResultFragmnetAdapter
import com.example.myapplication.databinding.FragmentHistoryBinding
import com.example.myapplication.databinding.FragmentResultBinding
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.helper.showToast
import com.example.myapplication.pojos.UserData
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList


class HistoryFragment : Fragment(), (UserData) -> Unit {
    private val TAG = "ResultFragment"
    var progress: ProgressDialog? = null

    //    val args: ResultFragmentArgs by navArgs()
    lateinit var binding: FragmentHistoryBinding
    val viewmodel by viewModel<HistoryFragmentViewModel>()
    var resultFragmnetAdapter = HistoryFragmnetAdapter().apply {

        delUserCallback=this@HistoryFragment
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_main, container, false)
        binding = DataBindingUtil.inflate<FragmentHistoryBinding>(
            inflater,
            R.layout.fragment_history,
            null,
            false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.adapter = resultFragmnetAdapter
        progress = ProgressDialog(context)
        progress?.setMessage("Loading Data")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.uiUpdates.collectLatest {
                    when (it) {
                        is ResponseModel.Error -> {
                            Log.e(TAG, "eee")
                        }
                        is ResponseModel.Idle -> {
                            Log.e(TAG, "iiii")
                        }
                        is ResponseModel.Loading -> {
                            showDialog()
                            Log.e(TAG, "llll")
                        }
                        is ResponseModel.Success -> {
                            dismissDialog()
                            if ((it.data.isNullOrEmpty()) and (it.data!!.size<=0)) {
                                requireContext().showToast("No Data Found")
                                resultFragmnetAdapter.submitList(null)

                            } else
                                resultFragmnetAdapter.submitList(it.data)
                        }
                    }
                }
            }
        }
        viewmodel.viewModelScope.launch {
            viewmodel.getDataFromLocalDb()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.historymenu,menu)
      //////  super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    override fun invoke(userData: UserData) {
      viewmodel.delUser(userData)
    }

}