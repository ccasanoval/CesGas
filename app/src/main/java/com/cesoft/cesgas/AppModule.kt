package com.cesoft.cesgas

import android.content.Context
import com.cesoft.cesgas.ui.Util
import com.cesoft.data.Repository
import com.cesoft.data.prefs.PrefDataSource
import com.cesoft.data.remote.RemoteDataSource
import com.cesoft.domain.repository.RepositoryContract
import com.cesoft.domain.usecase.GetStatesUC
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApp(@ApplicationContext appContext: Context) = appContext

    @Singleton
    @Provides
    fun provideUtil(@ApplicationContext appContext: Context) = Util(appContext)

//
//    @Singleton
//    @Provides
//    fun provideGetStatesUC(
//        repository: RepositoryContract
//    ): GetStatesUC {
//        return GetStatesUC(repository)
//    }

}