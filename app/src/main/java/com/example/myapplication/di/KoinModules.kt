package com.example.myapplication.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.api.AgifyService
import com.example.myapplication.repository.DataRepoImpl
import com.example.myapplication.repository.roomdb.AppLocalDatabase
import com.example.myapplication.viewmodel.EditFragmentViewModel
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.example.myapplication.viewmodel.MainFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class HiltModule {

    // single instance of HelloRepository
//    @Provides
//    @Singleton
//    fun getDataRepoImpl(@ApplicationContext appContext: Context): DataRepoImpl {
//        return DataRepoImpl(NetworkModule(appContext))
//    }

//    single
//    { DataRepoImpl(NetworkModule(androidContext())) }
//    viewModel
//    { (safestateHandle:SavedStateHandle)->MainFragmentViewModel(get(), safestateHandle) }
//    viewModel
//    { HistoryFragmentViewModel(get()) }
//    viewModel
//    { EditFragmentViewModel(get()) }
//    // Simple Presenter Factory
//    factory { MySimplePresenter(get()) }
}

//val appModule = module {
//
//    // single instance of HelloRepository
//    single { DataRepoImpl(NetworkModule(androidContext())) }
//    viewModel { (safestateHandle:SavedStateHandle)->MainFragmentViewModel(get(),safestateHandle) }
//    viewModel { HistoryFragmentViewModel(get()) }
//    viewModel { EditFragmentViewModel(get()) }
////    // Simple Presenter Factory
////    factory { MySimplePresenter(get()) }
//}
//@InstallIn(ViewModelComponent::class) // Scope our dependencies
//@Module
//abstract class ProfileModule {
//
//    // To be read as — When someone asks for DataRepository, create a DataRepoImpl and return it.
//    @Binds
//    abstract fun getProfileSource(repo: DataRepoImpl): DataRepoImpl
//}

class NetworkModule @Inject constructor(@ApplicationContext var context: Context) {

    fun sourceOfTruthNetworkDB(): AgifyService {
        return AgifyService.create()
    }

    fun sourceOfTruthLocalDB(): AppLocalDatabase {
        return AppLocalDatabase.getInstance(context)
    }
}