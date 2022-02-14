package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.pojos.UserData
import com.example.myapplication.repository.DataRepo

class EditFragmentViewModel(private var dataRepo: DataRepo) : ViewModel() {
    private val TAG = "EditFragmentViewModel"

    fun saveUriToDb(userData: UserData, abspath: String) {
        userData.userImageRef=abspath
         dataRepo.networkModule.sourceOfTruthLocalDB().userNameDao().updateUser(userData)
    }


}