package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetByProvinceUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int, product: Int? = null) =
        if(product != null) repository.getByProvince(id, product)
        else repository.getByProvince(id)
}
