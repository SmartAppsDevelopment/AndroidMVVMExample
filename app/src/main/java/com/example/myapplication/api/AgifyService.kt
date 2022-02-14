/*
 * Copyright 2020 Google LLC
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

package com.example.myapplication.api


import com.example.myapplication.pojos.UserData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Used to connect to the Unsplash API to fetch photos
 */
interface AgifyService {

    //    @GET("search/photos")
//    suspend fun searchPhotos(
//        @Query("query") query: String,
//        @Query("page") page: Int,
//        @Query("per_page") perPage: Int,
//    ): UnsplashSearchResponse
///@Query("page") String page
    @GET("?")
   suspend fun getSingleNameAge(@Query("country_id") countryid: String,@Query("name") userName: String): Response<UserData>

    @GET("?")
   suspend fun getMultipleNamesAge(@Query("country_id") countryid: String,@Query("name[]") userNames: List<String>): Response<List<UserData>>

    companion object {
        private const val BASE_URL = "https://api.agify.io/"

        fun create(): AgifyService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AgifyService::class.java)
        }
    }
}
