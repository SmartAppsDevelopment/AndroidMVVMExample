package com.example.myapplication.repository

import com.example.myapplication.pojos.SendResponseModel
import com.example.myapplication.pojos.UserData
import kotlinx.coroutines.flow.Flow

interface DataRepo {
    fun getAllDataFromLocalDb(): Flow<List<UserData>>

     suspend fun getSearchResultStream(
        query: SendResponseModel
    ): Flow<List<UserData?>>

    fun delDataFromRoom(body: UserData)

    fun updateUser(dataModel: UserData)

}