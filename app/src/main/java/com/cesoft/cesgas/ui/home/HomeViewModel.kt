package com.cesoft.cesgas.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesgas.Page
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeSideEffect
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.cesgas.ui.home.mvi.HomeTransform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

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
        /*
        val currentTrack = readCurrentTrack().getOrNull() ?: TrackDto.Empty
        if(currentTrack.isCreated) {
            trackingServiceFac.start(currentTrack.minInterval, currentTrack.minDistance)
        }

        //https://stackoverflow.com/questions/78277363/collecting-flows-in-the-viewmodel
        val res = readCurrentTrackFlow()
        res.getOrNull()?.let {
            val flow: StateFlow<TrackDto?> = it.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TrackDto.Empty,
            )
            emit(HomeTransform.GoInit(flow, null))
        } ?: run {
            val e: AppError = res.exceptionOrNull()
                ?.let { AppError.DataBaseError(it) } ?: run { AppError.NotFound }
            val flow = MutableStateFlow<TrackDto?>(null)
            emit(HomeTransform.GoInit(flow, e))
        }
        */
        emit(HomeTransform.GoInit())
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