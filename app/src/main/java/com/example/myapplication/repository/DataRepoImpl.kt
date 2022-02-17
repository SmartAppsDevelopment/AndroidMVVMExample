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


class DataRepoImpl @Inject constructor(private var networkModel: NetworkModule) : DataRepo {

    private fun identifySourceOfDATA(): DataSourceType {
        /////////////////////////check for local db if exist then from local
        return DataSourceType.FROM_NETWORK
    }

    override fun getAllDataFromLocalDb(): Flow<List<UserData>> {
        return networkModel.sourceOfTruthLocalDB().userNameDao().getUserNameList()

    }

    override fun delDataFromRoom(body: UserData) {
        networkModel.sourceOfTruthLocalDB().userNameDao().deleteUserName(body)
    }

    override fun updateUser(dataModel: UserData) {
        networkModel.sourceOfTruthLocalDB().userNameDao().updateUser(dataModel)
    }

    override suspend fun getSearchResultStream(
        query: SendResponseModel
    ): Flow<List<UserData?>> {
        return when (identifySourceOfDATA()) {
            DataSourceType.FROM_NETWORK -> getData(query)
            DataSourceType.FROM_LOCALDB -> TODO()
        }
    }

    private fun isRequestForMultipleUser(query: SendResponseModel): Boolean {
        return query.userName.contains(",")
    }

    private suspend fun getData(
        query: SendResponseModel
    ): Flow<List<UserData?>> {
        return flow {
            if (isRequestForMultipleUser(query)) {
              emit(hitApiOrDbMultiUser(query))
            } else {
                emit(hitApiOrDbSingleUser(query))
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun hitApiOrDbMultiUser(query: SendResponseModel):List<UserData> {
        val listToSendBack = arrayListOf<UserData>()
        val pairRes = getExistingNotExistUserLists(query)
        listToSendBack.addAll(pairRes.first)
        return try {
            if (pairRes.second.isNotEmpty()) {
                val response = getDataFromNetworkMultiUser(query.country, pairRes.second)
                if (response != null) {
                    listToSendBack.addAll(response)
                }
            }
            sendDataToRoom(listToSendBack)
            (listToSendBack)
        } catch (e: Exception) {
            (listToSendBack)
        }
    }

    private suspend fun hitApiOrDbSingleUser(query: SendResponseModel):List<UserData> {
        val listFromLocalDb = doesUserExistInLocalDB(query)
        return listFromLocalDb.ifEmpty {
            try {
                val response = getDataFromNetworkSingleUser(query)
                sendDataToRoom(listOf(response!!))
                (listOf(response))
            } catch (e: Exception) {
                showLog("getDataFromNetWork: " + e.message)
                (emptyList())
            }
        }
    }

    private fun getExistingNotExistUserLists(query: SendResponseModel): Pair<List<UserData>, List<String>> {
        val listToSendBack = arrayListOf<UserData>()
        val listOfUserNameNotFound = arrayListOf<String>()
        query.userName.split(",").map {
            it.trim()
        }.toList().forEachIndexed { _, s ->
            val dec = doesUserExistInLocalDB(SendResponseModel(query.country, s))
            if (dec.isNotEmpty())
                listToSendBack.addAll(dec)
            else
                listOfUserNameNotFound.add(s)
        }
        return Pair(listToSendBack, listOfUserNameNotFound)
    }

    private suspend fun getDataFromNetworkSingleUser(query: SendResponseModel): UserData? {
        return networkModel.sourceOfTruthNetworkDB()
            .getSingleNameAge(query.country, query.userName).body()
    }

    private suspend fun getDataFromNetworkMultiUser(
        query: String,
        listOfUserName: List<String>
    ): List<UserData>? {
        return networkModel.sourceOfTruthNetworkDB()
            .getMultipleNamesAge(query, listOfUserName).body()
    }

    private fun sendDataToRoom(body: List<UserData>) {
        body.forEach {
            val longId = networkModel.sourceOfTruthLocalDB().userNameDao().insertUserName(it)
            showLog("sendDataToRoom: $longId")
        }
    }

    private fun doesUserExistInLocalDB(userData: SendResponseModel): List<UserData> {
        return networkModel.sourceOfTruthLocalDB()
            .userNameDao()
            .getUserByID(userData.userName, userData.country)
    }

}