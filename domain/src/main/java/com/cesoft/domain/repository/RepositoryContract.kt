package com.cesoft.domain.repository

import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.Settings


interface RepositoryContract {
    // PREFS
    suspend fun readSettings(): Result<Settings>

    // REMOTE
    suspend fun getProducts(): Result<List<Product>>
}