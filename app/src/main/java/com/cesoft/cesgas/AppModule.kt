package com.cesoft.cesgas

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Singleton
//    @Provides
//    fun provideApp(@ApplicationContext appContext: Context) = appContext
//
//    @Singleton
//    @Provides
//    fun provideUtil(@ApplicationContext appContext: Context) = Util(appContext)

//
//    @Singleton
//    @Provides
//    fun provideGetStatesUC(
//        repository: RepositoryContract
//    ): GetStatesUC {
//        return GetStatesUC(repository)
//    }

}