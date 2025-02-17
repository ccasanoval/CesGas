package com.cesoft.cesgas

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cesoft.cesgas.ui.home.HomeViewModel
import com.cesoft.cesgas.ui.home.HomePage
import com.cesoft.cesgas.ui.map.MapPage
import com.cesoft.cesgas.ui.map.MapViewModel

sealed class Page(val route: String) {

    data object Home: Page("home")
    data object Settings: Page("settings")
    data object Map: Page("map")
    data object TrackDetail: Page("trackDetail/{id}") {
        private const val ARG_ID = "id"
        fun createRoute(id: Long) = "trackDetail/$id"
        fun getId(savedStateHandle: SavedStateHandle): Long? =
            savedStateHandle.get<String>(ARG_ID)?.toLong()
    }
}

@Composable
fun PageNavigation() {
    val navController = rememberNavController()
    NavHost(navController, Page.Home.route) {
        composable(route = Page.Home.route) {
            HomePage(navController, hiltViewModel<HomeViewModel>())
        }
        composable(route = Page.Map.route) {
            MapPage(navController, hiltViewModel<MapViewModel>())
        }
    }
}
