package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.api.AgifyService
import com.example.myapplication.repository.roomdb.AppLocalDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NetworkModule @Inject constructor(@ApplicationContext var context: Context) {

    fun sourceOfTruthNetworkDB(): AgifyService {
        return AgifyService.create()
    }

    fun sourceOfTruthLocalDB(): AppLocalDatabase {
        return AppLocalDatabase.getInstance(context)
    }
}