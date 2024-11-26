package com.cesoft.cesgas.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cesoft.cesgas.Page


fun NavGraphBuilder.homePage(navController: NavController) {
    composable(route = Page.Home.route) {

        //ProcessEffects(stateMachine, navController)
        //ProcessStates(stateMachine)
    }
}
