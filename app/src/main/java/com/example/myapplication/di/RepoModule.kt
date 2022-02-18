package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.repository.DataRepo
import com.example.myapplication.repository.DataRepoImpl
import com.example.myapplication.repository.roomdb.AppLocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object RepoModule {
    @ViewModelScoped
    @Provides
    fun getDataRepoImpl(dataSources: DataSources): DataRepo {
        return DataRepoImpl(dataSources)
    }

    @ViewModelScoped
    @Provides
    fun getLocalDataRepo(@ApplicationContext context: Context): AppLocalDatabase {
        return AppLocalDatabase.getInstance(context)
    }
}

