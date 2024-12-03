package com.cesoft.domain.repository

import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station


interface RepositoryContract {
    // PREFS
    suspend fun getFilter(): Result<Filter>
    suspend fun setFilter(filter: Filter): Result<Unit>

    // REMOTE MASTERS
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getStates(): Result<List<AddressState>>
    suspend fun getProvinces(id: Int): Result<List<AddressProvince>>
    suspend fun getCounties(id: Int): Result<List<AddressCounty>>

    // REMOTE STATIONS
    suspend fun getByState(id: Int): Result<List<Station>>
    suspend fun getByState(id: Int, productType: ProductType): Result<List<Station>>
    suspend fun getByProvince(id: Int): Result<List<Station>>
    suspend fun getByProvince(id: Int, productType: ProductType): Result<List<Station>>
    suspend fun getByCounty(id: Int): Result<List<Station>>
    suspend fun getByCounty(id: Int, productType: ProductType): Result<List<Station>>
}