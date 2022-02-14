package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.di.NetworkModule
import com.example.myapplication.helper.DATA_SOURCE_TYPES
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.helper.flattenToList
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DataRepo(var networkModule: NetworkModule) {
    private val TAG = "DataRepo"
    private var dataflowModel =
        MutableStateFlow<ResponseModel<List<UserData>>>(ResponseModel.Idle("Source Idel"))

    fun identifySourceOfDATA(query: String): DATA_SOURCE_TYPES {
        /////////////////////////check for local db if exist then from local
        return DATA_SOURCE_TYPES.FROM_NETWORK
    }

    suspend fun getSearchResultStream(
        query: SendResponseModel
    ): Flow<List<UserData?>> {
        return when (identifySourceOfDATA(query.userName)) {
            DATA_SOURCE_TYPES.FROM_NETWORK -> getDataFromNetWork(query)
            DATA_SOURCE_TYPES.FROM_LOCALDB -> getDataFromLocalDb(query)
        }
    }

    private fun getDataFromLocalDb(query: SendResponseModel): Flow<List<UserData?>> {
        TODO()
    }

    fun getallDataFromLocalDb(): Flow<List<UserData>> {
        return networkModule.sourceOfTruthLocalDB().userNameDao().getUserNameList()
    }

    suspend fun getDataFromNetWork(
        query: SendResponseModel
    ): Flow<List<UserData?>> {

        return flow<List<UserData>> {
            if (query.userName.contains(",")) {
                ///////////////////////////////for multiple user
                val listToSendBack = arrayListOf<UserData>()
                val listOfUserNameNotFound = arrayListOf<String>()
                ///   val listOfData = arrayListOf<Pair<Boolean, String>>()
                query.userName.split(",").map {
                    it.trim()
                }.toList().forEachIndexed { index, s ->
                    val dec = doesUserExistInLocalDB(SendResponseModel(query.country, s))
                    if (dec.size > 0)
                        listToSendBack.addAll(dec)
                    else
                        listOfUserNameNotFound.add(s)
                }
                try {
                    if (listOfUserNameNotFound.size>0) {
                        val response = networkModule.sourceOfTruthNetworkDB()
                            .getMultipleNamesAge(query.country, listOfUserNameNotFound)
                        listToSendBack.addAll(response.body()!!)
                    }
                    sendDataToRoom(listToSendBack)
                    emit(listToSendBack)
                } catch (e: Exception) {
                    emit(listToSendBack)
                }
            } else {
                val listFromLocalDb = doesUserExistInLocalDB(query)
                if (listFromLocalDb.size > 0) {
                    ///////////////////////////////Send From Local Db
                    emit(listFromLocalDb)
                } else {
                    /////////////////////Request To remote server
                    try {
                        val response = networkModule.sourceOfTruthNetworkDB()
                            .getSingleNameAge(query.country, query.userName).body()
                        sendDataToRoom(listOf(response!!))
                        emit(listOf(response!!))
                    } catch (e: Exception) {
                        Log.e(TAG, "getDataFromNetWork: " + e.message)
                        emit(emptyList())
                    }
                }

            }
        }.flowOn(Dispatchers.IO)
    }

    private fun doesUserExistInLocalDB(userData: SendResponseModel): List<UserData> {
        val listOfData = networkModule.sourceOfTruthLocalDB()
            .userNameDao()
            .getUserByID(userData.userName, userData.country)
        return listOfData
    }

    private fun doesUserExistInLocalDBBol(userData: SendResponseModel): Boolean {
        val listOfData = networkModule.sourceOfTruthLocalDB()
            .userNameDao()
            .getUserByID(userData.userName, userData.country)
        return (listOfData.size > 0)
    }

    private fun sendDataToRoom(body: List<UserData>) {
        body.forEach {
            // if(it.age.isNullOrEmpty())
            val longid = networkModule.sourceOfTruthLocalDB().userNameDao().insertUserName(it)
            Log.e(TAG, "sendDataToRoom: " + longid)
        }
    }

    fun delDataFromRoom(body: UserData) {

        val longid = networkModule.sourceOfTruthLocalDB().userNameDao().deleteUserName(body)
        Log.e(TAG, "sendDataToRoom: " + longid)

    }


}