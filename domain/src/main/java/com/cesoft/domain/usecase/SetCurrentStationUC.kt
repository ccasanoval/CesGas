package com.cesoft.domain.usecase

import com.cesoft.domain.entity.Station
import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class SetCurrentStationUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(station: Station) = repository.setCurrentStation(station)
}
