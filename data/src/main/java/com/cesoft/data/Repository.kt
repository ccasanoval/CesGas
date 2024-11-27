package com.cesoft.data

import com.cesoft.data.prefs.PrefDataSource
import com.cesoft.data.remote.RemoteDataSource
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.Settings
import com.cesoft.domain.repository.RepositoryContract
import okhttp3.internal.connection.retryTlsHandshake

class Repository(
    private val prefs: PrefDataSource,
    //private val local: LocalDataSource,
    private val remote: RemoteDataSource
): RepositoryContract {
    override suspend fun readSettings(): Result<Settings> {
        prefs.readSettings().let {
            return Result.success(it)
        } //?: run { return Result.failure(AppError.NotFound) }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        val res = remote.getProducts()
        return if(res.isSuccess) {
            Result.success(res.getOrNull()?.map { it.toEntity() } ?: listOf())
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
}