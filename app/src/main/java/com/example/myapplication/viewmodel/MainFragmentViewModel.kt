package com.example.myapplication.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepo
import com.example.myapplication.repository.DataRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private var dataRepoImpl: DataRepo,
    private var savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Keep the key as a constant
    companion object {
        private const val USER_NAME = "USERNAMES"
    }

    var currUserName: String = ""
        set(value) {
            savedStateHandle.set(USER_NAME, value)
            field = value
        }
        get() = savedStateHandle.get<String>(USER_NAME) ?: ""

    val uiUpdates =
        MutableStateFlow<ResponseModel<List<UserData?>>>(ResponseModel.Idle("Ideal State"))
    private var currentQueryValue: SendResponseModel? = null


    suspend fun searchAge(queryString: SendResponseModel) {
        currentQueryValue = queryString
        uiUpdates.emit(ResponseModel.Loading())
        dataRepoImpl.getSearchResultStream(queryString).collect {
            viewModelScope.launch {
                uiUpdates.emit(ResponseModel.Success(it))
            }
        }
    }

    fun markIdleState() {
        uiUpdates.value = ResponseModel.Idle("Ideal")
    }


}