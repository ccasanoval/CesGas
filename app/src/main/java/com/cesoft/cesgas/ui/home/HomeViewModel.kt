package com.cesoft.cesgas.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
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
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCountiesUC
import com.cesoft.domain.usecase.GetFilterUC
import com.cesoft.domain.usecase.GetProductsUC
import com.cesoft.domain.usecase.GetProvincesUC
import com.cesoft.domain.usecase.GetStatesUC
import com.cesoft.domain.usecase.SetFilterUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFilter: GetFilterUC,
    private val setFilter: SetFilterUC,
    //TODO
    //private val getFavorites: GetFavoritesUC,
    //private val setFavorites: SetFavoritesUC,

    private val getProducts: GetProductsUC,
    private val getStates: GetStatesUC,
    private val getProvinces: GetProvincesUC,
    private val getCounties: GetCountiesUC,

    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC,

): ViewModel(), MviHost<HomeIntent, State<HomeState, HomeSideEffect>> {
    private var products = listOf<ProductType>()
    private var states = listOf<AddressState>()
    private var provinces = listOf<AddressProvince>()
    private var counties = listOf<AddressCounty>()
    private var filter = Filter(productType = ProductType.G95, state = 28)//TODO: Prefs

    private val reducer = Reducer(
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
            is HomeIntent.Map -> executeMap(intent.idStation)
        }

    private fun executeClose() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }

    private fun executeChangeProvince(filters: FilterOptions) = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }
    private fun executeChangeCounty(filters: FilterOptions) = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }
    private fun executeChangeProduct(filters: FilterOptions) = flow {
        val productType = filters.getSelectedId()
//TODO
        emit(
            HomeTransform.GoInit(
                stations = listOf(),
                filter = filter,
                masters = Masters.Empty,
                error = null
            )
        )
    }

    private fun executeChangeState(options: FilterOptions) = flow {
        //TODO: Save favorite states
        //TODO: Select current state
//android.util.Log.e("AAA", "executeChangeState--000----------------- ${filter}")
        options.getSelectedId()?.let { idState ->
            val provinces = getProvinces(idState).getOrNull() ?: listOf()
            val masters = Masters.Empty.copy(states = states, products = products, provinces = provinces)
            filter = filter.copy(state = idState, province = null, county = null)
            val res = getByState(idState, filter.productType)
            if(res.isSuccess) {
                emit(HomeTransform.GoInit(
                    stations = res.getOrNull() ?: listOf(),
                    filter = filter,
                    masters = masters,
                    error = null
                ))
            }
            else {
                emit(HomeTransform.GoInit(
                    stations = listOf(),
                    filter = filter,
                    masters = masters,
                    error = res.exceptionOrNull() ?: AppError.UnknownError
                ))
            }
        } ?: run {
            val masters = Masters.Empty.copy(states = states, products = products)
            emit(
                HomeTransform.GoInit(
                    stations = listOf(),
                    filter = filter,
                    masters = masters,
                    error = null
                )
            )
        }
    }

    private fun executeLoad() = flow {
        val state = refresh()
        android.util.Log.e(TAG, "executeLoad------------------- prod ${state.masters.products.size}")
        android.util.Log.e(TAG, "executeLoad------------------- stat ${state.masters.states.size}")
        android.util.Log.e(TAG, "executeLoad------------------- prov ${state.masters.provinces.size}")
        android.util.Log.e(TAG, "executeLoad------------------- coun ${state.masters.counties.size}")
        emit(state)
    }

    private suspend fun refresh(): HomeTransform.GoInit {
        //products = getProducts().getOrNull() ?: listOf()
        products = listOf(
            ProductType.G95, ProductType.G98, ProductType.GOA, ProductType.GOAP, ProductType.GLP
        )
        states = getStates().getOrNull() ?: listOf()

        filter = getFilter().getOrNull() ?: Filter()
        val county = filter.county
        val province = filter.province
        val state = filter.state
        val productType = filter.productType
        val res = if(county != null && province != null && state != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
            counties = getCounties(province).getOrNull() ?: listOf()
            getByCounty(county, productType)
        }
        else if(province != null && state != null) {
            provinces = getProvinces(state).getOrNull() ?: listOf()
            getByProvince(province, productType)
        }
        else if(state != null) {
            getByState(state, productType)
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
        val stations = res.getOrNull()
        if(stations != null) {
            return HomeTransform.GoInit(
                stations = stations,
                filter = filter,
                masters = masters,
                error = null
            )
        }
        else {
            val e: AppError = res.exceptionOrNull()
                ?.let { AppError.DataBaseError(it) } ?: run { AppError.NotFound }
            Log.e(TAG, "refresh:e: $e")
            return HomeTransform.GoInit(error = e)
        }
    }

    private fun executeMap(idStation: Int) = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Start))//TODO: Map page...
    }

    fun consumeSideEffect(
        sideEffect: HomeSideEffect,
        navController: NavController,
        context: Context,
    ) {
        when(sideEffect) {
            HomeSideEffect.Start -> {
                navController.navigate(Page.Home.route)
            }
            else -> navController.navigate(Page.Home.route)
//            HomeSideEffect.GoSettings -> {
//                navController.navigate(Page.Settings.route)
//                //Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
//            }
//            HomeSideEffect.GoTracks -> {
//                navController.navigate(Page.Tracks.route)
//            }
//            HomeSideEffect.GoMap -> {
//                navController.navigate(Page.Map.route)
//            }
//            HomeSideEffect.Close -> (context as Activity).finish()
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}