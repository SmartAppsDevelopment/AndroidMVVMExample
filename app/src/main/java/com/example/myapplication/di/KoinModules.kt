package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.api.AgifyService
import com.example.myapplication.repository.roomdb.AppLocalDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class DataSources @Inject constructor(@ApplicationContext var context: Context,
                                      private val service: AgifyService,
                                      private val localDb: AppLocalDatabase) {
    fun sourceOfTruthNetworkDB(): AgifyService {
        return service
    }

    fun sourceOfTruthLocalDB(): AppLocalDatabase {
        return localDb
    }
}