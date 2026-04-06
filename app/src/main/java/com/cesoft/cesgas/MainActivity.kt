package com.cesoft.cesgas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cesoft.cesgas.ui.home.HomePresenter
import com.cesoft.cesgas.ui.home.HomeScreen
import com.cesoft.cesgas.ui.home.HomeState
import com.cesoft.cesgas.ui.map.MapPresenter
import com.cesoft.cesgas.ui.map.MapScreen
import com.cesoft.cesgas.ui.map.MapState
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.ui.ui
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var homePresenter: HomePresenter
    @Inject lateinit var mapPresenter: MapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val circuit = Circuit.Builder()
            .addPresenterFactory { screen, navigator, _ ->
                when (screen) {
                    is HomeScreen -> {
                        homePresenter.navigator = navigator
                        homePresenter
                    }
                    is MapScreen -> {
                        mapPresenter.navigator = navigator
                        mapPresenter
                    }
                    else -> null
                }
            }
            .addUiFactory { screen, _ ->
                when (screen) {
                    is HomeScreen -> ui<HomeState> { state, modifier ->
                        Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
                            Surface(modifier = Modifier.padding(innerPadding)) {
                                HomeScreen(state)
                            }
                        }
                    }
                    is MapScreen -> ui<MapState> { state, modifier ->
                        Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
                            Surface(modifier = Modifier.padding(innerPadding)) {
                                Spacer(Modifier.padding(100.dp))
                                MapScreen(state)
                            }
                        }
                    }
                    else -> null
                }
            }
            .build()

        setContent {
            val backStack = rememberSaveableBackStack(root = HomeScreen)
            val navigator = rememberCircuitNavigator(backStack)
            CircuitCompositionLocals(circuit) {
                NavigableCircuitContent(
                    navigator = navigator,
                    backStack = backStack
                )
            }
        }
    }
}
