package com.example.myapplication.fragmens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.adapters.ResultFragmnetAdapter
import com.example.myapplication.databinding.FragmentResultBinding
import com.example.myapplication.pojos.UserData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList


class ResultFragment : BaseFragment<FragmentResultBinding>(R.layout.fragment_result) {
    private val TAG = "ResultFragment"
    val args: ResultFragmentArgs by navArgs()
    var resultFragmnetAdapter: ResultFragmnetAdapter = ResultFragmnetAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.adapter = resultFragmnetAdapter
        if (!args.transfereddata.isNullOrEmpty()) {
            Log.e(TAG, "onViewCreated: " + args.transfereddata)
            val arrayList= ArrayList<UserData>()
             args.transfereddata!!.toList().forEach {
                arrayList.add(it)
            }
            resultFragmnetAdapter.submitList(arrayList)
        }

//        val items = resources.getStringArray(R.array.countrynames)
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
//        if(this::binding.isInitialized)
//        binding.autoCompletetxt.setAdapter(adapter)
    }
}