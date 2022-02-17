package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepo
import com.example.myapplication.repository.DataRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditFragmentViewModel @Inject constructor(private var dataRepoImpl: DataRepo) : ViewModel() {

    fun saveUriToDb(userData: UserData, absPath: String) {
        userData.userImageRef=absPath
         dataRepoImpl.updateUser(userData)
    }


}