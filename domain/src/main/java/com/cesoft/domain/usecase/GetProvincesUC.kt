package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetProvincesUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int) = repository.getProvinces(id)
}
