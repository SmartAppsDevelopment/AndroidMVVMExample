package com.example.myapplication.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.api.AgifyService
import com.example.myapplication.repository.DataRepoImpl
import com.example.myapplication.repository.roomdb.AppLocalDatabase
import com.example.myapplication.viewmodel.EditFragmentViewModel
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.example.myapplication.viewmodel.MainFragmentViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // single instance of HelloRepository
    single { DataRepoImpl(NetworkModule(androidContext())) }
    viewModel { (safestateHandle:SavedStateHandle)->MainFragmentViewModel(get(),safestateHandle) }
    viewModel { HistoryFragmentViewModel(get()) }
    viewModel { EditFragmentViewModel(get()) }
//    // Simple Presenter Factory
//    factory { MySimplePresenter(get()) }
}

class NetworkModule(var context: Context) {

    fun sourceOfTruthNetworkDB(): AgifyService {
        return AgifyService.create()
    }

    fun sourceOfTruthLocalDB(): AppLocalDatabase {
        return AppLocalDatabase.getInstance(context)
    }
}