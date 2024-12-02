package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class GetCountiesUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(id: Int) = repository.getCounties(id)
}
