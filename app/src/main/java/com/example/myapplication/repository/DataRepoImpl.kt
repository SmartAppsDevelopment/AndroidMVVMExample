package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.di.NetworkModule
import com.example.myapplication.helper.DATA_SOURCE_TYPES
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class DataRepoImpl(networkModel: NetworkModule):AbsDataRepo(networkModel) {
    private val TAG = "DataRepo"

    fun identifySourceOfDATA(query: String): DATA_SOURCE_TYPES {
        /////////////////////////check for local db if exist then from local
        return DATA_SOURCE_TYPES.FROM_NETWORK
    }

    /**
     *
     */

    override suspend fun getSearchResultStream(
        query: SendResponseModel
    ): Flow<List<UserData?>> {
        return when (identifySourceOfDATA(query.userName)) {
            DATA_SOURCE_TYPES.FROM_NETWORK -> getDataFromNetWork(query)
            DATA_SOURCE_TYPES.FROM_LOCALDB -> getDataFromLocalDb(query)
        }
    }

    override suspend fun getDataFromNetWork(
        query: SendResponseModel
    ): Flow<List<UserData?>> {

        return flow {
            if (doesthisRequestForMultipleUser(query)) {
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

}