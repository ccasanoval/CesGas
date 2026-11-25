package com.cesoft.cesgas.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.cesgas.ui.map.MapScreen
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetCountiesUC
import com.cesoft.domain.usecase.GetFavoritesUC
import com.cesoft.domain.usecase.GetFilterUC
import com.cesoft.domain.usecase.GetProvincesUC
import com.cesoft.domain.usecase.GetStatesUC
import com.cesoft.domain.usecase.SetFavoritesUC
import com.cesoft.domain.usecase.SetFilterUC
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/** Everything the screen shows once loaded */
private data class HomeData(
    val stations: List<Station> = listOf(),
    val filter: Filter = Filter.Empty,
    val masters: Masters = Masters.Empty,
    val favorites: Favorites = Favorites.Empty,
    val error: Throwable? = null,
)

/** A filter change waiting to be saved before the reload */
private data class PendingSave(val filter: Filter, val favorites: Favorites)

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val getFilter: GetFilterUC,
    private val setFilter: SetFilterUC,
    private val getFavorites: GetFavoritesUC,
    private val setFavorites: SetFavoritesUC,
    private val filterStations: FilterStationsUC,
    private val getStates: GetStatesUC,
    private val getProvinces: GetProvincesUC,
    private val getCounties: GetCountiesUC,
) : Presenter<HomeState> {

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): HomePresenter
    }

    @Composable
    override fun present(): HomeState {
        // Retained: survives configuration changes, so rotating the device doesn't call the API again
        var isLoading by rememberRetained { mutableStateOf(true) }
        var data by rememberRetained { mutableStateOf(HomeData()) }
        // Filter change not saved yet. Retained too: if a rotation cancels the save, the reload repeats it
        var pendingSave by rememberRetained { mutableStateOf<PendingSave?>(null) }

        // Setting isLoading = true is what triggers a (re)load
        if(isLoading) {
            LaunchedEffect(Unit) {
                var loaded: HomeData
                do {
                    // Save first, so the reload reads the new filter
                    val save = pendingSave
                    if(save != null) {
                        setFavorites(save.favorites)
                        setFilter(save.filter)
                    }
                    loaded = withContext(Dispatchers.IO) { fetch() }
                    // Another change arrived meanwhile: save it and load again
                    val isUpToDate = pendingSave === save
                    if(isUpToDate) pendingSave = null
                } while(!isUpToDate)
                data = loaded
                isLoading = false
            }
        }

        // Applied to the data right away, so a change made before the reload builds on the previous one
        val changeFilter: ((Favorites) -> Favorites, (Filter) -> Filter) -> Unit = { updateFavorites, transform ->
            data = data.copy(favorites = updateFavorites(data.favorites), filter = transform(data.filter))
            pendingSave = PendingSave(data.filter, data.favorites)
            isLoading = true
        }

        val eventSink: (HomeIntent) -> Unit = { event ->
            when (event) {
                is HomeIntent.Close -> navigator.pop()
                is HomeIntent.Load -> isLoading = true
                // The station travels in the screen: nothing to write to disk before navigating
                is HomeIntent.GoMap -> navigator.goTo(MapScreen(event.station))
                is HomeIntent.ChangeProduct -> changeFilter(
                    { it.copy(products = it.products.updatedWith(event.filters) { id -> ProductType.entries.getOrNull(id) }) },
                    { it.copy(productType = event.filters.getSelectedProductType()) }
                )
                is HomeIntent.ChangeAddressState -> changeFilter(
                    { it.copy(states = it.states.updatedWith(event.filters) { id -> id }) },
                    { it.copy(state = event.filters.getSelectedId(), province = null, county = null) }
                )
                is HomeIntent.ChangeAddressProvince -> changeFilter(
                    { it.copy(provinces = it.provinces.updatedWith(event.filters) { id -> id }) },
                    { it.copy(province = event.filters.getSelectedId(), county = null) }
                )
                is HomeIntent.ChangeAddressCounty -> changeFilter(
                    { it.copy(counties = it.counties.updatedWith(event.filters) { id -> id }) },
                    { it.copy(county = event.filters.getSelectedId()) }
                )
                is HomeIntent.ChangeAddressZipCode -> changeFilter(
                    { it },
                    { it.copy(zipCode = event.zipCode) }
                )
            }
        }

        return if(isLoading) {
            HomeState.Loading(onEvent = eventSink)
        } else {
            HomeState.Success(
                stations = data.stations,
                filter = data.filter,
                masters = data.masters,
                favorites = data.favorites,
                error = data.error,
                onEvent = eventSink
            )
        }
    }

    private suspend fun fetch(): HomeData = coroutineScope {
        val favorites = async { getFavorites().getOrNull() ?: Favorites.Empty }
        fetchStations().copy(favorites = favorites.await())
    }

    /** The requests don't depend on each other: they all start at once, only the saved filter is read first */
    private suspend fun fetchStations(): HomeData = coroutineScope {
        val filter = getFilter().getOrNull() ?: Filter()
        val state = filter.state
        val province = filter.province
        val statesRequest = async { getStates() }
        val provincesRequest = state?.let { async { getProvinces(it) } }
        val countiesRequest = if(state != null && province != null) async { getCounties(province) } else null
        val stationsRequest = if(state != null && filter.productType != null) async { filterStations(filter) } else null

        val states = statesRequest.await().getOrElse { error ->
            // Nothing can be shown without the states: don't wait for the rest
            coroutineContext.cancelChildren()
            return@coroutineScope HomeData(filter = filter, error = error)
        }
        val masters = Masters(
            products = PRODUCTS,
            states = states,
            provinces = provincesRequest?.await()?.getOrNull() ?: listOf(),
            counties = countiesRequest?.await()?.getOrNull() ?: listOf(),
        )
        when {
            stationsRequest != null -> stationsRequest.await().fold(
                onSuccess = { stations ->
                    val error = if(stations.isEmpty()) AppError.NotFound() else null
                    HomeData(stations = stations, filter = filter, masters = masters, error = error)
                },
                onFailure = { HomeData(filter = filter, masters = masters, error = it) }
            )
            state == null -> HomeData(filter = filter, masters = masters, error = AppError.NoStateSelected())
            else -> HomeData(filter = filter, masters = masters, error = AppError.NoProductSelected())
        }
    }

    /**
     * The options only show part of the favorites (e.g. the provinces of the selected state):
     * keep the ones not shown and take the starred state of the shown ones
     */
    private fun <T> Set<T>.updatedWith(options: FilterOptions, toItem: (Int) -> T?): Set<T> {
        val shown = options.fields.mapNotNull { toItem(it.id) }.toSet()
        val starred = options.fields.filter { it.favorite }.mapNotNull { toItem(it.id) }.toSet()
        return this - shown + starred
    }

    /** Product filter options use the ProductType ordinal as id */
    private fun FilterOptions.getSelectedProductType() =
        getSelectedId()?.let { ProductType.entries.getOrNull(it) }

    companion object {
        val PRODUCTS = listOf(
            ProductType.G95, ProductType.G98, ProductType.GOA, ProductType.GOAP, ProductType.GLP
        )
    }
}
