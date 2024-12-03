package com.cesoft.domain.usecase

import com.cesoft.domain.entity.Filter
import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class SetFilterUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(filter: Filter) = repository.setFilter(filter)
}
