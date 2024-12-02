package com.cesoft.cesgas.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesgas.Page
import com.cesoft.cesgas.ui.home.mvi.Filter
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeSideEffect
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.cesgas.ui.home.mvi.HomeTransform
import com.cesoft.cesgas.ui.home.mvi.Masters
import com.cesoft.domain.AppError
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCountiesUC
import com.cesoft.domain.usecase.GetProductsUC
import com.cesoft.domain.usecase.GetProvincesUC
import com.cesoft.domain.usecase.GetStatesUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProducts: GetProductsUC,
    private val getStates: GetStatesUC,
    private val getProvinces: GetProvincesUC,
    private val getCounties: GetCountiesUC,
    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC,
): ViewModel(), MviHost<HomeIntent, State<HomeState, HomeSideEffect>> {

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
        }

    private fun executeClose() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }

    private fun executeLoad() = flow {
        android.util.Log.e("AAA", "----------- 000")
        //TODO: los muy anormales: id = "1" != "01"
        //TODO: Orden
        //val res = getByCounty(7183)
        //val res = getByProvince(13)
        val res = getByProvince(28)
        res.getOrNull()?.let {
            android.util.Log.e("AAA", "----------- ${it.size}")
            emit(HomeTransform.GoInit(
                stations = it,
                filter = Filter.Empty,
                masters = Masters.Empty,
                error = null
            ))
        } ?: run {
            val e: AppError = res.exceptionOrNull()
                ?.let { AppError.DataBaseError(it) } ?: run { AppError.NotFound }
            android.util.Log.e("AAA", "----------- $e")
            emit(HomeTransform.GoInit(error = e))
        }
    }

    private fun executeStart() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Start))
        emit(HomeTransform.GoLoading)
    }

    private fun executeMap() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Start))
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