package com.cesoft.domain.usecase

import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.repository.RepositoryContract
import javax.inject.Inject

class SetFavoritesUC @Inject constructor(
    private val repository: RepositoryContract
) {
    suspend operator fun invoke(favorites: Favorites) = repository.setFavorites(favorites)
}
