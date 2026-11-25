package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class ClearCacheUC @Inject constructor(
    private val repository: RepositoryContract
) {
    operator fun invoke() = repository.clearCache()
}
