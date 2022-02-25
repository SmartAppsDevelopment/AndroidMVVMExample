package com.example.ui.base

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
   ////     Navigation.setViewNavController(view, findNavController())

    }

    fun showProgressDialog(msg: String = "Please Wait") {
        progressDialog = progressDialog ?: KProgressHUD.create(requireActivity())
        progressDialog?.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)?.setLabel(msg)
            ?.setCancellable(true)?.setAnimationSpeed(2)?.setDimAmount(0.5f)?.show()

    }

    fun hideProgress() {
        progressDialog?.dismiss()
    }

//    fun findNavController()=
//        Navigator(requireContext())


}
//class Navigator @Inject constructor(private val navController: NavController, app : Context) : NavController(app){
//    override fun navigate(destination : NavDirections){
//        navController.navigateSafe(destination)
//    }
//}

//class Navigator(context: Context) : NavController(context) {
//    override fun navigate(resId: Int) {
////        super.navigate(resId)
//        myNavigate(resId)
//    }
//
//    override fun navigate(directions: NavDirections) {
//        myNavigate(directions.actionId)
//        //  super.navigate(directions)
//    }
//
//    fun myNavigate(resID:Int){
//        val resId = resID
//        val destinationId = currentDestination?.getAction(resId)?.destinationId ?: 0
//        currentDestination?.let { node ->
//            val currentNode = when (node) {
//                is NavGraph -> node
//                else -> node.parent
//            }
//            if (destinationId != 0) {
//                currentNode?.findNode(destinationId)?.let { navigate(resId) }
//            }
//        }
//    }
//}