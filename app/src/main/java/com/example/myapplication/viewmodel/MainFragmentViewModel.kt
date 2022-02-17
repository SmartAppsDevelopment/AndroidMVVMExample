package com.example.myapplication.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainFragmentViewModel(
    private var dataRepoImpl: DataRepoImpl,
    private var savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val TAG = "MainFragmentViewModel"

    // Keep the key as a constant
    companion object {
        private val USER_NAME = "USERNAMELIST"
        private val CONTRY_INDEX = "country"
    }
var currUserName:String=""
    set(value) {
        savedStateHandle.set(USER_NAME,value)
        field=value
    }
    get()=savedStateHandle.get<String>(USER_NAME)?:""
    var currentIndex: Int = 0
        set(value) {
            savedStateHandle.set(CONTRY_INDEX,value)
            field =value
        }
        get() =savedStateHandle.get<Int>(CONTRY_INDEX)?:0

    val uiUpdates =
        MutableStateFlow<ResponseModel<List<UserData?>>>(ResponseModel.Idle("Idel State"))
    private var currentQueryValue: SendResponseModel? = null


    suspend fun searchAge(queryString: SendResponseModel) {
        currentQueryValue = queryString
        //  uiUpdates.value=ResponseModel.Loading()
        uiUpdates.emit(ResponseModel.Loading())
        dataRepoImpl.getSearchResultStream(queryString).collect {
            viewModelScope.launch {
                uiUpdates.emit(ResponseModel.Success(it))
            }
        }
    }

    fun markIdleStsate() {
        uiUpdates.value = ResponseModel.Idle("Idel")
    }

//    fun setCurrentIndex(index: Int) {
//        savedStateHandle.set(CONTRY_INDEX, index)
//    }

}