/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.repository.roomdb

import androidx.room.*
import com.example.myapplication.pojos.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserNameDao {
    @Query("SELECT * FROM userdata")
    fun getUserNameList(): Flow<List<UserData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertUserName(dataModel: UserData): Long

    @Delete
     fun deleteUserName(datamodel: UserData)

    @Update
    fun updateUser(dataModel: UserData)

    @Query("SELECT * FROM userdata WHERE  name like :username AND countryId=:coutnryID")
    fun getUserByID(username: String,coutnryID:String): List<UserData>
}
