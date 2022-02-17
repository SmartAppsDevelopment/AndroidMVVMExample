package com.example.myapplication.di

import com.example.myapplication.repository.DataRepo
import com.example.myapplication.repository.DataRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class HiltModule {
    @Provides
    fun getDataRepoImpl(networkModule: NetworkModule):DataRepo{
        return DataRepoImpl(networkModule)
    }
}