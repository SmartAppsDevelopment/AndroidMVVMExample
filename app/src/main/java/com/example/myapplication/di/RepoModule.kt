package com.example.myapplication.di

import com.example.myapplication.repository.DataRepo
import com.example.myapplication.repository.DataRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object RepoModule {
    @ViewModelScoped
    @Provides
    fun getDataRepoImpl(dataSources: DataSources): DataRepo {
        return DataRepoImpl(dataSources)
    }
}

