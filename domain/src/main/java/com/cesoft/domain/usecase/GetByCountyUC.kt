package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetByCountyUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int, product: Int? = null) =
        if(product != null) repository.getByCounty(id, product)
        else repository.getByCounty(id)
}
