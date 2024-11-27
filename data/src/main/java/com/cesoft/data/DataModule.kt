package com.cesoft.data

import android.content.Context
import com.cesoft.data.prefs.PrefDataSource
import com.cesoft.data.remote.RemoteDataSource
import com.cesoft.domain.repository.RepositoryContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providePrefDataSource(
        @ApplicationContext appContext: Context
    ): PrefDataSource {
        return PrefDataSource(appContext)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        @ApplicationContext appContext: Context,
    ): RemoteDataSource {
        return RemoteDataSource(appContext)
    }

    @Singleton
    @Provides
    fun provideRepository(
        prefDataSource: PrefDataSource,
        remoteDataSource: RemoteDataSource
    ): RepositoryContract {
        return Repository(prefDataSource, remoteDataSource)
    }

//    @Singleton
//    @Provides
//    fun providePrefsRepository(
//        @ApplicationContext appContext: Context,
//    ): PrefsRepositoryContract {
//        return PrefsRepository(appContext)
//    }
}
