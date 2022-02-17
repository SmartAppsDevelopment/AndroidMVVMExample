package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditFragmentViewModel @Inject constructor(private var dataRepoImpl: DataRepoImpl) : ViewModel() {
    private val TAG = "EditFragmentViewModel"

    fun saveUriToDb(userData: UserData, abspath: String) {
        userData.userImageRef=abspath
         dataRepoImpl.networkModule.sourceOfTruthLocalDB().userNameDao().updateUser(userData)
    }


}