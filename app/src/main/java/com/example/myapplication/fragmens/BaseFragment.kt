package com.example.myapplication.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.kaopiz.kprogresshud.KProgressHUD

open class BaseFragment<T : ViewDataBinding>(var layoutid: Int) : Fragment() {
    lateinit var binding: T
    val isBindingInit by lazy { this::binding.isInitialized }
    var progressDialog: KProgressHUD? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<T>(inflater, layoutid, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context ?: return
    }

    fun showProgressDialog() {
        progressDialog = KProgressHUD.create(requireContext())
        progressDialog?.let {
            it.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                //   .setDetailsLabel("Downloading data")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        }

    }

    fun hideProgress() {
        progressDialog?.dismiss()
    }
}