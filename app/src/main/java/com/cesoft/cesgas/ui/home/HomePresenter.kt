package com.cesoft.cesgas.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.cesgas.ui.map.MapScreen
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetCountiesUC
import com.cesoft.domain.usecase.GetFilterUC
import com.cesoft.domain.usecase.GetProvincesUC
import com.cesoft.domain.usecase.GetStatesUC
import com.cesoft.domain.usecase.SetCurrentStationUC
import com.cesoft.domain.usecase.SetFilterUC
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Everything the screen shows once loaded */
private data class HomeData(
    val stations: List<Station> = listOf(),
    val filter: Filter = Filter.Empty,
    val masters: Masters = Masters.Empty,
    val error: Throwable? = null,
)

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val getFilter: GetFilterUC,
    private val setFilter: SetFilterUC,
    private val filterStations: FilterStationsUC,
    private val setCurrentStation: SetCurrentStationUC,
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
        val coroutineScope = rememberCoroutineScope()
        // Retained: survives configuration changes, so rotating the device doesn't call the API again
        var isLoading by rememberRetained { mutableStateOf(true) }
        var data by rememberRetained { mutableStateOf(HomeData()) }

        // Setting isLoading = true is what triggers a (re)load
        if(isLoading) {
            LaunchedEffect(Unit) {
                data = withContext(Dispatchers.IO) { fetch() }
                isLoading = false
            }
        }

        // Persist the new filter first, so the reload reads it
        val changeFilter: ((Filter) -> Filter) -> Unit = { transform ->
            coroutineScope.launch {
                setFilter(transform(data.filter))
                isLoading = true
            }
        }

        val eventSink: (HomeIntent) -> Unit = { event ->
            when (event) {
                is HomeIntent.Close -> navigator.pop()
                is HomeIntent.Load -> isLoading = true
                is HomeIntent.GoMap -> coroutineScope.launch {
                    setCurrentStation(event.station ?: Station.Empty)
                    navigator.goTo(MapScreen)
                }
                is HomeIntent.ChangeProduct -> changeFilter {
                    it.copy(productType = event.filters.getSelectedProductType())
                }
                is HomeIntent.ChangeAddressState -> changeFilter {
                    it.copy(state = event.filters.getSelectedId(), province = null, county = null)
                }
                is HomeIntent.ChangeAddressProvince -> changeFilter {
                    it.copy(province = event.filters.getSelectedId(), county = null)
                }
                is HomeIntent.ChangeAddressCounty -> changeFilter {
                    it.copy(county = event.filters.getSelectedId())
                }
                is HomeIntent.ChangeAddressZipCode -> changeFilter {
                    it.copy(zipCode = event.zipCode)
                }
            }
        }

        return if(isLoading) {
            HomeState.Loading(onEvent = eventSink)
        } else {
            HomeState.Success(
                stations = data.stations,
                filter = data.filter,
                masters = data.masters,
                error = data.error,
                onEvent = eventSink
            )
        }
    }

    private suspend fun fetch(): HomeData {
        val filter = getFilter().getOrNull() ?: Filter()
        val states = getStates().getOrElse { return HomeData(filter = filter, error = it) }
        var masters = Masters(products = PRODUCTS, states = states, provinces = listOf(), counties = listOf())

        val state = filter.state
            ?: return HomeData(filter = filter, masters = masters, error = AppError.NoStateSelected())
        masters = masters.copy(
            provinces = getProvinces(state).getOrNull() ?: listOf(),
            counties = filter.province?.let { getCounties(it).getOrNull() } ?: listOf(),
        )
        if(filter.productType == null) {
            return HomeData(filter = filter, masters = masters, error = AppError.NoProductSelected())
        }

        return filterStations(filter).fold(
            onSuccess = { stations ->
                val error = if(stations.isEmpty()) AppError.NotFound() else null
                HomeData(stations = stations, filter = filter, masters = masters, error = error)
            },
            onFailure = { HomeData(filter = filter, masters = masters, error = it) }
        )
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
