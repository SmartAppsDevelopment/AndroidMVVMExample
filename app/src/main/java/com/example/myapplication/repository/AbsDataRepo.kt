package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.di.NetworkModule
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import kotlinx.coroutines.flow.Flow

abstract class AbsDataRepo(var networkModule: NetworkModule) {
    private val TAG = "AbsDataRepo"
    fun doesUserExistInLocalDB(userData: SendResponseModel): List<UserData> {
        val listOfData = networkModule.sourceOfTruthLocalDB()
            .userNameDao()
            .getUserByID(userData.userName, userData.country)
        return listOfData
    }

    fun sendDataToRoom(body: List<UserData>) {
        body.forEach {
            // if(it.age.isNullOrEmpty())
            val longid = networkModule.sourceOfTruthLocalDB().userNameDao().insertUserName(it)
            Log.e(TAG, "sendDataToRoom: " + longid)
        }
    }

    fun getallDataFromLocalDb(): Flow<List<UserData>> {
        /////////////////////Get all data from db
        return networkModule.sourceOfTruthLocalDB().userNameDao().getUserNameList()
    }

    fun getDataFromLocalDb(query: SendResponseModel): Flow<List<UserData?>> {
        ////////////////////IMpl login from get data local db
        TODO()
    }


    fun doesthisRequestForMultipleUser(query: SendResponseModel): Boolean {
        return query.userName.contains(",")
    }

    fun delDataFromRoom(body: UserData) {

        val longid = networkModule.sourceOfTruthLocalDB().userNameDao().deleteUserName(body)
        Log.e(TAG, "sendDataToRoom: " + longid)

    }

    abstract suspend fun getSearchResultStream(
        query: SendResponseModel
    ): Flow<List<UserData?>>

    abstract suspend fun getDataFromNetWork(
        query: SendResponseModel
    ): Flow<List<UserData?>>
}