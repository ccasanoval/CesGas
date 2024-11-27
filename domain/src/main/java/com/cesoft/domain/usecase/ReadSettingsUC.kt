package com.cesoft.domain.usecase

import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class ReadSettingsUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke() = repository.readSettings()
}
