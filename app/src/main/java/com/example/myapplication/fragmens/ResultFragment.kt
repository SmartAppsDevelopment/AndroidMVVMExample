package com.example.myapplication.fragmens

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.adapters.ResultFragmentAdapter
import com.example.myapplication.databinding.FragmentResultBinding
import com.example.myapplication.helper.showLog
import com.example.myapplication.pojos.UserData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(R.layout.fragment_result) {
    private val args: ResultFragmentArgs by navArgs()
    private var resultFragmentAdapter: ResultFragmentAdapter = ResultFragmentAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.adapter = resultFragmentAdapter
        if (!args.transfereddata.isNullOrEmpty()) {
            showLog( "onViewCreated: " + args.transfereddata)
            val arrayList= ArrayList<UserData>()
            with(resultFragmentAdapter) {
                args.transfereddata!!.toList().forEach {
                        arrayList.add(it)
                    }
                submitList(arrayList)
            }
        }
    }
}