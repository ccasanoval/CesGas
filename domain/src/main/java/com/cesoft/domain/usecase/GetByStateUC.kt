package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetByStateUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int, product: Int? = null) =
        if(product != null) repository.getByState(id, product)
        else repository.getByState(id)
}
