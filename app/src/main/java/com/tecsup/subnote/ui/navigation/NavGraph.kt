package com.tecsup.subnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.subnote.data.repository.SuscripcionRepository
import com.tecsup.subnote.ui.detalle.DetalleSuscripcionScreen
import com.tecsup.subnote.ui.lista.ListaSuscripcionesScreen
import com.tecsup.subnote.viewmodel.DetalleViewModel
import com.tecsup.subnote.viewmodel.DetalleViewModelFactory
import com.tecsup.subnote.viewmodel.ListaViewModel
import com.tecsup.subnote.viewmodel.ListaViewModelFactory

@Composable
fun NavGraph(repository: SuscripcionRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Lista.route) {

        composable(Screen.Lista.route) {
            val viewModel: ListaViewModel = viewModel(factory = ListaViewModelFactory(repository))
            ListaSuscripcionesScreen(
                viewModel = viewModel,
                onSuscripcionClick = { id -> navController.navigate(Screen.Detalle.crearRuta(id)) },
                onNuevaClick = { },
                onResumenClick = { }
            )
        }

        composable(
            route = Screen.Detalle.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val viewModel: DetalleViewModel = viewModel(factory = DetalleViewModelFactory(repository))
            DetalleSuscripcionScreen(
                suscripcionId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEditar = { }
            )
        }
    }
}