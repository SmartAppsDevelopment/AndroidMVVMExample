package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainFragmentViewModel(private var dataRepo: DataRepo) : ViewModel() {
    private val TAG = "MainFragmentViewModel"

    val uiUpdates =
        MutableStateFlow<ResponseModel<List<UserData?>>>(ResponseModel.Idle("Idel State"))
    private var currentQueryValue: SendResponseModel? = null


    suspend fun searchAge(queryString: SendResponseModel) {
        currentQueryValue = queryString
        //   uiUpdates.
        //  uiUpdates.value=ResponseModel.Loading()
        uiUpdates.emit(ResponseModel.Loading())
        dataRepo.getSearchResultStream(queryString).collect{
            viewModelScope.launch {
                uiUpdates.emit(ResponseModel.Success(it))
            }
        }
//        dataRepo.getSearchResultStream(queryString).collect {
//            Log.e(TAG, "searchAge: "+it )
//            uiUpdates.emit(ResponseModel.Success(it))
//        }
    }
    fun markIdleStsate(){
        uiUpdates.value=ResponseModel.Idle("Idel")

    }


}