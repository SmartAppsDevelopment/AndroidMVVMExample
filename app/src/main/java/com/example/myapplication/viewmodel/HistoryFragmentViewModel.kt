package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(private var dataRepoImpl: DataRepoImpl) : ViewModel() {
    private val TAG = "MainFragmentViewModel"

    val uiUpdates =
        MutableStateFlow<ResponseModel<List<UserData?>>>(ResponseModel.Idle("Idel State"))
    private var currentQueryValue: SendResponseModel? = null


    suspend fun getDataFromLocalDb() {
        uiUpdates.emit(ResponseModel.Loading())
        dataRepoImpl.getallDataFromLocalDb().collect {
            uiUpdates.emit(ResponseModel.Success(it))
        }
    }
    fun delUser(userData: UserData){
        dataRepoImpl.delDataFromRoom(userData)
    }

    fun markIdleStsate() {
        uiUpdates.value = ResponseModel.Idle("Idel")
    }


}