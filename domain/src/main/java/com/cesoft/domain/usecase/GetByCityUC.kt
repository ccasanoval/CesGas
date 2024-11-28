package com.cesoft.domain.usecase

import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetByCityUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int, type: ProductType = ProductType.ALL) = repository.getByCity(id, type)
}
