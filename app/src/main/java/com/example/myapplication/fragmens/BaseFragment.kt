package com.example.myapplication.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD

open class BaseFragment<T : ViewDataBinding>(private var layoutId: Int) : Fragment() {
    lateinit var binding: T
    val isBindingInit by lazy { this::binding.isInitialized }
    private var progressDialog: KProgressHUD? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context ?: return
    }

    fun showProgressDialog(msg:String="Please Wait") {
        progressDialog = progressDialog ?: KProgressHUD.create(requireActivity())
        progressDialog?.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)?.setLabel(msg)
            ?.setCancellable(true)?.setAnimationSpeed(2)?.setDimAmount(0.5f)?.show()

    }

    fun hideProgress() {
        progressDialog?.dismiss()
    }
}