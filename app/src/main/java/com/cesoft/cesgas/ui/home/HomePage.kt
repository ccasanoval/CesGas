package com.cesoft.cesgas.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesgas.ui.common.LoadingCompo
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeState
import kotlin.reflect.KFunction1

@Composable
fun HomePage(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController,
                context = context
            )
        },
        onBackPressed = {
            viewModel.execute(HomeIntent.Close)
        },
    ) { state: HomeState ->
        when(state) {
            is HomeState.Loading -> {
                viewModel.execute(HomeIntent.Load)
                LoadingCompo()
            }
            is HomeState.Init -> {
                Init(state = state, reduce = viewModel::execute)
            }
        }
    }
}

@Composable
private fun Init(
    state: HomeState.Init,
    reduce: KFunction1<HomeIntent, Unit>
) {
    Header()
    StationList()
}

@Composable
private fun Header() {
    Text("Filtros para la busqueda")
}

@Composable
private fun StationList() {
    Text("Station list")
// {"IDProducto":"1","NombreProducto":"Gasolina 95 E5","NombreProductoAbreviatura":"G95E5"},
// {"IDProducto":"23","NombreProducto":"Gasolina 95 E10","NombreProductoAbreviatura":"G95E10"},
// {"IDProducto":"20","NombreProducto":"Gasolina 95 E5 Premium","NombreProductoAbreviatura":"G95E5+"},
// {"IDProducto":"3","NombreProducto":"Gasolina 98 E5","NombreProductoAbreviatura":"G98E5"},
// {"IDProducto":"21","NombreProducto":"Gasolina 98 E10","NombreProductoAbreviatura":"G98E10"},
// {"IDProducto":"4","NombreProducto":"Gasóleo A habitual","NombreProductoAbreviatura":"GOA"},
// {"IDProducto":"5","NombreProducto":"Gasóleo Premium","NombreProductoAbreviatura":"GOA+"},
// {"IDProducto":"6","NombreProducto":"Gasóleo B","NombreProductoAbreviatura":"GOB"},{"IDProducto":"7","NombreProducto":"Gasóleo C","NombreProductoAbreviatura":"GOC"},{"IDProducto":"16","NombreProducto":"Bioetanol","NombreProductoAbreviatura":"BIE"},{"IDProducto":"8","NombreProducto":"Biodiésel","NombreProductoAbreviatura":"BIO"},
// {"IDProducto":"17","NombreProducto":"Gases licuados del petróleo","NombreProductoAbreviatura":"GLP"},
// {"IDProducto":"18","NombreProducto":"Gas natural comprimido","NombreProductoAbreviatura":"GNC"},
// {"IDProducto":"19","NombreProducto":"Gas natural licuado","NombreProductoAbreviatura":"GNL"},
// {"IDProducto":"22","NombreProducto":"Hidrógeno","NombreProductoAbreviatura":"H2"},
// {"IDProducto":"9","NombreProducto":"Fuelóleo bajo índice azufre","NombreProductoAbreviatura":"FOB"},
// {"IDProducto":"10","NombreProducto":"Fuelóleo especial","NombreProductoAbreviatura":"FOE"},
// {"IDProducto":"11","NombreProducto":"Gasóleo para uso marítimo","NombreProductoAbreviatura":"MGO"},
// {"IDProducto":"12","NombreProducto":"Gasolina de aviación","NombreProductoAbreviatura":"GNAV"},
// {"IDProducto":"13","NombreProducto":"Queroseno de aviación JET_A1","NombreProductoAbreviatura":"JETA1"},
// {"IDProducto":"14","NombreProducto":"Queroseno de aviación JET_A2","NombreProductoAbreviatura":"JETA2"}]

}