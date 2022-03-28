package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.helper.showLog
import com.example.myapplication.helper.showToast
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.viewmodel.MainFragmentViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel by activityViewModels<MainFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
            setContent {
                AppUi()
            }
        }
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiUpdates.collectLatest { resModel ->
                    when (resModel) {
                        is ResponseModel.Error -> {
                            showLog("observeData: Error")
                        }
                        is ResponseModel.Idle -> {
                            showLog("observeData: Idle")
                        }
                        is ResponseModel.Loading -> {
                            progressBar.show()
                            showLog("observeData: Loading")
                        }
                        is ResponseModel.Success -> {
                            progressBar.dismiss()
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
                            showLog("observeData: Success")
                        }
                    }
                }
            }
        }
    }

    private val progressBar: KProgressHUD by lazy {
        KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Downloading data")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
    }

    @Preview
    @Composable
    fun AppUi() {
        val selectedOptionText = remember { mutableStateOf(""+resources.getStringArray(R.array.countrynames)[0]) }

        MdcTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var countryName by remember { mutableStateOf("jacky,smith,tommy,robbins,jon,jonny,timmy") }
                    OutlinedTextField(
                        value = countryName,
                        onValueChange = { countryName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    addSpinnerView(selectedOptionText)
                    Button(onClick = {
                        viewModel.viewModelScope.launch {
                        if (!TextUtils.isEmpty(selectedOptionText.value) and !(TextUtils.isEmpty(selectedOptionText.value))) {
                            if (selectedOptionText.value != null) {
                                viewModel.searchAge(SendResponseModel().apply {
                                    userName = countryName
                                    viewModel.currUserName = userName
                                    country = selectedOptionText.value
                                })
                            } else {
                                requireContext().showToast("Select Country")
                            }
                        } else {
                            requireContext().showToast("Enter Name")
                        }
                    } }, modifier = Modifier.padding(10.dp).width(100.dp)) {
                        Text(text = "Search")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun addSpinnerView(selCountryCode: MutableState<String>) {
        val spinnerModel = object {                                                     //2
            val countryNames = stringArrayResource(R.array.countrynames)
            val countryCodes = stringArrayResource(R.array.countrycodes)
        }
        var selectedOptionText by remember {
            selCountryCode.value=spinnerModel.countryCodes.get(0)
            mutableStateOf(spinnerModel.countryNames.get(0))
        }
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
        ) {
            TextField(
                readOnly = true,
                value = selectedOptionText,
                onValueChange = { },
                label = { Text("Country") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                modifier = Modifier
                    .fillMaxWidth(),
                onDismissRequest = {
                    expanded = false
                }
            ) {
                spinnerModel.countryNames.forEachIndexed { index,name ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOptionText = name
                            selCountryCode.value = spinnerModel.countryCodes[index]
                            expanded = false
                        }
                    ) {
                        Text(text = name)
                    }
                }
            }
        }
    }
}


