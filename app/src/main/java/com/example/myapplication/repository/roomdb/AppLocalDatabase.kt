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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.helper.DATABASE_NAME
import com.example.myapplication.pojos.UserData

/**
 * The Room database for this app
 */
@Database(entities = [UserData::class], version = 4, exportSchema = false)
abstract class AppLocalDatabase : RoomDatabase() {
    abstract fun userNameDao(): UserNameDao
    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppLocalDatabase? = null

        fun getInstance(context: Context): AppLocalDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppLocalDatabase {
            return Room.databaseBuilder(context, AppLocalDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(
                    object : RoomDatabase.Callback() {
                    }
                )
                .build()
        }
    }
}
