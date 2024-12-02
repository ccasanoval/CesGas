package com.cesoft.data

import com.cesoft.data.prefs.PrefDataSource
import com.cesoft.data.remote.RemoteDataSource
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Settings
import com.cesoft.domain.entity.Station
import com.cesoft.domain.repository.RepositoryContract

class Repository(
    private val prefs: PrefDataSource,
    //private val local: LocalDataSource,
    private val remote: RemoteDataSource
): RepositoryContract {

    /// PREFS
    override suspend fun readSettings(): Result<Settings> {
        prefs.readSettings().let {
            return Result.success(it)
        } //?: run { return Result.failure(AppError.NotFound) }
    }

    /// REMOTE MASTERS
    override suspend fun getProducts(): Result<List<Product>> {
        val res = remote.getProducts()
        return if (res.isSuccess) {
            Result.success(res.getOrNull()?.map { it.toEntity() } ?: listOf())
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
    override suspend fun getStates(): Result<List<AddressState>> {
        val res = remote.getStates()
        return if (res.isSuccess) {
            Result.success(res.getOrNull()?.map { it.toEntity() } ?: listOf())
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
    override suspend fun getProvinces(id: Int): Result<List<AddressProvince>> {
        val res = remote.getProvinces(id)
        return if (res.isSuccess) {
            Result.success(res.getOrNull()?.map { it.toEntity() } ?: listOf())
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
    override suspend fun getCounties(id: Int): Result<List<AddressCounty>> {
        val res = remote.getCounties(id)
        return if (res.isSuccess) {
            Result.success(res.getOrNull()?.map { it.toEntity() } ?: listOf())
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }

    /// REMOTE STATIONS
    override suspend fun getByState(id: Int): Result<List<Station>> {
        val res = remote.getByState(id)
        return if (res.isSuccess) {
            val data = res.getOrNull()?.list?.map { it.toEntity() } ?: listOf()
//            val filtered = when(type) {
//                ProductType.ALL -> data
//                ProductType.G95 -> data.filter { it.prices.G95 != null }.sortedBy { it.prices.G95 }
//                ProductType.G98 -> data.filter { it.prices.G98 != null }.sortedBy { it.prices.G98 }
//                ProductType.GOA -> data.filter { it.prices.GOA != null }.sortedBy { it.prices.GOA }
//                ProductType.GOB -> data.filter { it.prices.GOB != null }.sortedBy { it.prices.GOB }
//                ProductType.GOC -> data.filter { it.prices.GOC != null }.sortedBy { it.prices.GOC }
//                ProductType.GOAP -> data.filter { it.prices.GOAP != null }.sortedBy { it.prices.GOAP }
//                ProductType.GLP -> data.filter { it.prices.GLP != null }.sortedBy { it.prices.GLP }
//                else -> data
//            }
            Result.success(data)
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
    override suspend fun getByState(id: Int, product: Int): Result<List<Station>> {
        val res = remote.getByState(id, product)
        return if (res.isSuccess) {
            val data = res.getOrNull()?.list?.map { it.toEntity() } ?: listOf()
            Result.success(data)
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }

    override suspend fun getByProvince(id: Int): Result<List<Station>> {
        val res = remote.getByProvince(id)
        return if (res.isSuccess) {
            val data = res.getOrNull()?.list?.map { it.toEntity() } ?: listOf()
            Result.success(data)
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
    override suspend fun getByProvince(id: Int, product: Int): Result<List<Station>> {
        val res = remote.getByProvince(id, product)
        return if (res.isSuccess) {
            val data = res.getOrNull()?.list?.map { it.toEntity() } ?: listOf()
            Result.success(data)
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }

    override suspend fun getByCounty(id: Int): Result<List<Station>> {
        val res = remote.getByCounty(id)
        return if (res.isSuccess) {
            val data = res.getOrNull()?.list?.map { it.toEntity() } ?: listOf()
            Result.success(data)
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }
    override suspend fun getByCounty(id: Int, product: Int): Result<List<Station>> {
        val res = remote.getByCounty(id, product)
        return if (res.isSuccess) {
            val data = res.getOrNull()?.list?.map { it.toEntity() } ?: listOf()
            Result.success(data)
        } else {
            Result.failure(res.exceptionOrNull() ?: AppError.UnknownError(""))
        }
    }

}