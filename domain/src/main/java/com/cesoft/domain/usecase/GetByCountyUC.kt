package com.cesoft.domain.usecase

import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetByCountyUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int, productType: ProductType? = null) =
        if(productType != null) repository.getByCounty(id, productType)
        else repository.getByCounty(id)
}