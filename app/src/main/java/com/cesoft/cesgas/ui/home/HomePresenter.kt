package com.cesoft.cesgas.ui.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.cesgas.ui.home.HomeIntent
import com.cesoft.cesgas.ui.map.MapScreen
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
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
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomePresenter @Inject constructor(
    var getFilter: GetFilterUC,
    var setFilter: SetFilterUC,
    var filterStations: FilterStationsUC,
    var setCurrentStation: SetCurrentStationUC,
    var getStates: GetStatesUC,
    var getProvinces: GetProvincesUC,
    var getCounties: GetCountiesUC
) : Presenter<HomeState> {

    private var error: Throwable? = null
    private var stations = listOf<Station>()
    private var products = listOf<ProductType>()
    private var states = listOf<AddressState>()
    private var provinces = listOf<AddressProvince>()
    private var counties = listOf<AddressCounty>()
    private var filter = Filter(productType = ProductType.G95, state = 10, zipCode = "46520")//TODO: Prefs....

    var navigator: Navigator ?= null

    suspend fun fetch() {
        products = listOf(
            ProductType.G95, ProductType.G98, ProductType.GOA, ProductType.GOAP, ProductType.GLP
        )
        states = getStates().getOrNull() ?: listOf()
        filter = getFilter().getOrNull() ?: Filter()
        val county = filter.county
        val province = filter.province
        val state = filter.state
        val productType = filter.productType
        val zipCode = filter.zipCode

        Log.e(TAG, "fetch---------------------------- ")
        Log.e(TAG, "fetch------- FILTER PRODUC ------ $productType / ${products.size}")
        Log.e(TAG, "fetch------- FILTER STATE ------ $state / ${states.size}")
        Log.e(TAG, "fetch------- FILTER PROVIN ------ $province / ${provinces.size}")
        Log.e(TAG, "fetch------- FILTER COUNTY ------ $county / ${counties.size}")
        Log.e(TAG, "fetch------- FILTER ZIP CODE ------ $zipCode")

        error = null
        if(productType == null) {
            error = AppError.NoProductSelected()
            return
        }
        if(state == null) {
            error = AppError.NoStateSelected()
            return
        }

        if(county != null && province != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
            counties = getCounties(province).getOrNull() ?: listOf()
        }
        else if(province != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
            counties = getCounties(province).getOrNull() ?: listOf()
        }
        else {
            provinces = getProvinces(state).getOrNull() ?: listOf()
        }

        val res = filterStations(filter)
        stations = res.filter { s ->
            if(zipCode.isNotBlank()) { s.zipCode == zipCode } else true
        }
        Log.e(TAG, "fetch:stations:---------------- ${stations.size}")
        //TODO: Llega aqui pero no actualiza el estado...............................................................
        if(stations.isEmpty()) {
            error = AppError.NotFound()
            Log.e(TAG, "fetch:e:---------------- $error")
        }
    }

    private suspend fun executeChangeProduct(filters: FilterOptions) {
        val i = filters.getSelectedId() ?: 0
        val productType = ProductType.entries[i]
        filter = filter.copy(productType = productType)
        setFilter(filter)
    }
    private suspend fun executeChangeState(options: FilterOptions) {
        val idState = options.getSelectedId()
        filter = filter.copy(state = idState, province = null, county = null)
        setFilter(filter)
    }
    private suspend fun executeChangeProvince(options: FilterOptions) {
        val idProvince = options.getSelectedId()
        filter = filter.copy(province = idProvince, county = null)
        setFilter(filter)
    }
    private suspend fun executeChangeCounty(options: FilterOptions) {
        val idCounty = options.getSelectedId()
        filter = filter.copy(county = idCounty)
        setFilter(filter)
    }
    private suspend fun executeChangeZipCode(zipCode: String) {
        filter = filter.copy(zipCode = zipCode)
        setFilter(filter)
    }

    private suspend fun executeMap(station: Station?) {
        station?.let {
            setCurrentStation(station)
            navigator?.goTo(MapScreen)
        } ?: run {
            setCurrentStation(Station.Empty)
            navigator?.goTo(MapScreen)
        }
    }

    @Composable
    override fun present(): HomeState {
        android.util.Log.e(TAG, "present:------------------------------------------------")
        val coroutineScope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(true) }

        val eventSink: (HomeIntent) -> Unit = { event ->
            when (event) {
                is HomeIntent.Close -> {
                    navigator?.pop()
                }
                is HomeIntent.GoMap -> {
                    coroutineScope.launch(Dispatchers.IO) {
                        executeMap(event.station)
                    }
                }
                is HomeIntent.Load -> {
                    android.util.Log.e(TAG, "present------ is HomeIntent.Load ->")
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        fetch()
                        isLoading = false
                    }
                }
                is HomeIntent.ChangeProduct -> {
                    android.util.Log.e(TAG, "present------ is HomeIntent.ChangeProduct ->")
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        executeChangeProduct(event.filters)
                    }
                }
                is HomeIntent.ChangeAddressState -> {
                    android.util.Log.e(TAG, "present------ is HomeIntent.ChangeAddressState ->")
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        executeChangeState(event.filters)
                    }
                }
                is HomeIntent.ChangeAddressProvince -> {
                    android.util.Log.e(TAG, "present------ is HomeIntent.ChangeAddressProvince ->")
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        executeChangeProvince(event.filters)
                    }
                }
                is HomeIntent.ChangeAddressCounty -> {
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        executeChangeCounty(event.filters)
                    }
                }
                is HomeIntent.ChangeAddressZipCode -> {
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        executeChangeZipCode(event.zipCode)
                    }
                }
            }
        }

//        LaunchedEffect(Unit) {
//            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                coroutineScope.launch(Dispatchers.IO) {
//                    isLoading = true
//                    state = fetch()
//                }
//            }
//        }

        return when {
            isLoading -> {
                android.util.Log.e(TAG, "present:Loading-------------------------- ${stations.size} / ${states.size}")
                HomeState.Loading(onEvent = eventSink)
            }
            else -> {
                android.util.Log.e(TAG, "present:Success-------------------------- e=$error / stations=${stations.size} / states=${states.size}")
                HomeState.Success(
                    stations = stations,
                    filter = filter,
                    masters = Masters(
                        products = products,
                        states = states,
                        provinces = provinces,
                        counties = counties
                    ),
                    error = error,
                    onEvent = eventSink
                )
            }
        }
    }

    companion object {
        private const val TAG = "Presenter"
    }
}
