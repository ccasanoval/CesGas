package com.cesoft.domain.repository

import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Settings
import com.cesoft.domain.entity.Station


interface RepositoryContract {
    // PREFS
    suspend fun readSettings(): Result<Settings>

    // REMOTE
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getByCity(id: Int, type: ProductType): Result<List<Station>>
}