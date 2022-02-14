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

class HistoryFragmentViewModel(private var dataRepo: DataRepo) : ViewModel() {
    private val TAG = "MainFragmentViewModel"

    val uiUpdates =
        MutableStateFlow<ResponseModel<List<UserData?>>>(ResponseModel.Idle("Idel State"))
    private var currentQueryValue: SendResponseModel? = null


    suspend fun getDataFromLocalDb() {
        uiUpdates.emit(ResponseModel.Loading())
        dataRepo.getallDataFromLocalDb().collect {
            uiUpdates.emit(ResponseModel.Success(it))
        }

    }
    fun delUser(userData: UserData){
        dataRepo.delDataFromRoom(userData)
    }

    fun markIdleStsate() {
        uiUpdates.value = ResponseModel.Idle("Idel")
    }


}