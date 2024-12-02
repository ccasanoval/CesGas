package com.cesoft.domain.repository

import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Settings
import com.cesoft.domain.entity.Station


interface RepositoryContract {
    // PREFS
    suspend fun readSettings(): Result<Settings>

    // REMOTE MASTERS
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getStates(): Result<List<AddressState>>
    suspend fun getProvinces(id: Int): Result<List<AddressProvince>>
    suspend fun getCounties(id: Int): Result<List<AddressCounty>>

    // REMOTE STATIONS
    suspend fun getByState(id: Int): Result<List<Station>>
    suspend fun getByState(id: Int, product: Int): Result<List<Station>>
    suspend fun getByProvince(id: Int): Result<List<Station>>
    suspend fun getByProvince(id: Int, product: Int): Result<List<Station>>
    suspend fun getByCounty(id: Int): Result<List<Station>>
    suspend fun getByCounty(id: Int, product: Int): Result<List<Station>>
}