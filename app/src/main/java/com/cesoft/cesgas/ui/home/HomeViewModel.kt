package com.cesoft.cesgas.ui.home

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesgas.Page
import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeSideEffect
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.cesgas.ui.home.mvi.HomeTransform
import com.cesoft.cesgas.ui.home.mvi.Masters
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCountiesUC
import com.cesoft.domain.usecase.GetFilterUC
import com.cesoft.domain.usecase.GetProvincesUC
import com.cesoft.domain.usecase.GetStatesUC
import com.cesoft.domain.usecase.SetCurrentStationUC
import com.cesoft.domain.usecase.SetFilterUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFilter: GetFilterUC,
    private val setFilter: SetFilterUC,
    private val filterStations: FilterStationsUC,
    private val setCurrentStation: SetCurrentStationUC,
    //private val getFavorites: GetFavoritesUC,
    //private val setFavorites: SetFavoritesUC,
    //private val getProducts: GetProductsUC,

    private val getStates: GetStatesUC,
    private val getProvinces: GetProvincesUC,
    private val getCounties: GetCountiesUC,

    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC
    
): ViewModel(), MviHost<HomeIntent, State<HomeState, HomeSideEffect>> {

    private var products = listOf<ProductType>()
    private var states = listOf<AddressState>()
    private var provinces = listOf<AddressProvince>()
    private var counties = listOf<AddressCounty>()
    private var filter = Filter(productType = ProductType.G95, state = 28)//TODO: Prefs---------------------

    private val reducer: Reducer<HomeIntent, State<HomeState, HomeSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = HomeState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: HomeIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: HomeIntent) =
        when(intent) {
            HomeIntent.Close -> executeClose()
            HomeIntent.Load -> executeLoad()
            is HomeIntent.ChangeProduct -> executeChangeProduct(intent.filters)
            is HomeIntent.ChangeAddressState -> executeChangeState(intent.filters)
            is HomeIntent.ChangeAddressProvince -> executeChangeProvince(intent.filters)
            is HomeIntent.ChangeAddressCounty -> executeChangeCounty(intent.filters)
            is HomeIntent.ChangeAddressZipCode -> executeChangeZipCode(intent.zipCode)
            is HomeIntent.GoMap -> executeMap(intent.station)
        }

    private fun executeClose() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }

    private fun executeChangeProduct(filters: FilterOptions) = flow {
        emit(HomeTransform.Wait)
        val i = filters.getSelectedId() ?: 0
        val productType = ProductType.entries[i]
        filter = filter.copy(productType = productType)
        setFilter(filter)
        emit(fetch())
    }
    private fun executeChangeState(options: FilterOptions) = flow {
        val idState = options.getSelectedId()
        filter = filter.copy(state = idState, province = null, county = null)
        setFilter(filter)
        emit(fetch())
    }
    private fun executeChangeProvince(options: FilterOptions) = flow {
        val idProvince = options.getSelectedId()
        filter = filter.copy(province = idProvince, county = null)
        setFilter(filter)
        emit(fetch())
    }
    private fun executeChangeCounty(options: FilterOptions) = flow {
        val idCounty = options.getSelectedId()
        filter = filter.copy(county = idCounty)
        setFilter(filter)
        emit(fetch())
    }
    private fun executeChangeZipCode(zipCode: String) = flow {
        filter = filter.copy(zipCode = zipCode)
        setFilter(filter)
        emit(fetch())
    }

    private fun executeLoad() = flow {
        filter = getFilter().getOrNull() ?: Filter()
        val state = fetch()
        //android.util.Log.e(TAG, "executeLoad------------------- prod ${state.masters.products.size}")
        //android.util.Log.e(TAG, "executeLoad------------------- stat ${state.masters.states.size}")
        //android.util.Log.e(TAG, "executeLoad------------------- prov ${state.masters.provinces.size}")
        //android.util.Log.e(TAG, "executeLoad------------------- coun ${state.masters.counties.size}")
        emit(state)
    }

    private suspend fun fetch(): HomeTransform.GoInit {
        //products = getProducts().getOrNull() ?: listOf()
        products = listOf(
            ProductType.G95, ProductType.G98, ProductType.GOA, ProductType.GOAP, ProductType.GLP
        )
        states = getStates().getOrNull() ?: listOf()

        val county = filter.county
        val province = filter.province
        val state = filter.state
        val productType = filter.productType
        val zipCode = filter.zipCode

        android.util.Log.e("AAA", "refresh------- FILTER PRODUC ------ $productType")
        android.util.Log.e("AAA", "refresh------- FILTER STATE ------ $state")
        android.util.Log.e("AAA", "refresh------- FILTER PROVIN ------ $province")
        android.util.Log.e("AAA", "refresh------- FILTER COUNTY ------ $county")
        android.util.Log.e("AAA", "refresh------- FILTER ZIP CODE ------ $zipCode")

        if(county != null && province != null && state != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
            counties = getCounties(province).getOrNull() ?: listOf()
        }
        else if(province != null && state != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
            counties = getCounties(province).getOrNull() ?: listOf()
        }
        else if(state != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
        }
        else {
            return HomeTransform.GoInit(
                masters = Masters.Empty.copy(products = products, states = states),
                error = AppError.NoStateSelected
            )
        }
        val masters = Masters.Empty.copy(
            products = products,
            states = states,
            provinces = provinces,
            counties = counties
        )

        val res = filterStations(filter)
        val stations = res.filter { s ->
            if(zipCode.isNotBlank()) { s.zipCode == zipCode } else true
        }
        if(stations.isNotEmpty()) {
            //stations.forEach { android.util.Log.e("SM", "SM---------- ${it.location}") }
            return HomeTransform.GoInit(
                stations = stations,
                filter = filter,
                masters = masters,
                error = null
            )
        }
        else {
            //val e: AppError = res.exceptionOrNull()
                //?.let { AppError.DataBaseError(it) } ?: run { AppError.NotFound }
            val e = AppError.NotFound
            Log.e(TAG, "refresh:e: $e")
            return HomeTransform.GoInit(error = e)
        }
    }

    private fun executeMap(station: Station?) = flow {
        station?.let {
            setCurrentStation(station)
            emit(HomeTransform.AddSideEffect(HomeSideEffect.GoMap))
        } ?: run {
            setCurrentStation(Station.Empty)
            emit(HomeTransform.AddSideEffect(HomeSideEffect.GoMap))
            android.util.Log.e("VM", "executeMap-------------------- FILTER")
            //TODO: Go map with filter
        }
    }

    fun consumeSideEffect(
        sideEffect: HomeSideEffect,
        navController: NavController,
        context: Context
    ) {
        android.util.Log.e("AA", "consumeSideEffect-------- $sideEffect")
        when(sideEffect) {
            HomeSideEffect.Start -> {
                navController.navigate(Page.Home.route)
            }
            HomeSideEffect.GoMap -> {
                navController.navigate(Page.Map.route)
            }
            HomeSideEffect.Close -> {
                (context as Activity).finish()
            }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}