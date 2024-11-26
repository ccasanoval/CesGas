package com.cesoft.cesgas

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApp(@ApplicationContext appContext: Context) = appContext

//    @OptIn(DelicateCoroutinesApi::class)
//    @Singleton
//    @Provides
//    fun provideSharedLocationManager(
//        @ApplicationContext appContext: Context
//    ): SharedLocationManager {
//        val scope = (appContext as App).applicationScope
//        return SharedLocationManager(appContext, scope)
//    }
}