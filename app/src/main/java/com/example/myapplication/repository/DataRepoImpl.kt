package com.example.myapplication.repository

import com.example.myapplication.di.NetworkModule
import com.example.myapplication.helper.DataSourceType
import com.example.myapplication.helper.showLog
import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class DataRepoImpl @Inject constructor(networkModel: NetworkModule):AbsDataRepo(networkModel) {

    fun identifySourceOfDATA(query: String): DataSourceType {
        /////////////////////////check for local db if exist then from local
        return DataSourceType.FROM_NETWORK
    }

    /**
     *
     */

    override suspend fun getSearchResultStream(
        query: SendResponseModel
    ): Flow<List<UserData?>> {
        return when (identifySourceOfDATA(query.userName)) {
            DataSourceType.FROM_NETWORK -> getDataFromNetWork(query)
            DataSourceType.FROM_LOCALDB -> getDataFromLocalDb(query)
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
                        showLog("getDataFromNetWork: " + e.message)
                        emit(emptyList())
                    }
                }

            }
        }.flowOn(Dispatchers.IO)
    }

}