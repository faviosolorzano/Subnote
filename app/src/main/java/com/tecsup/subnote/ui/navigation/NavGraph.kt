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
import com.tecsup.subnote.ui.formulario.FormularioSuscripcionScreen
import com.tecsup.subnote.ui.lista.ListaSuscripcionesScreen
import com.tecsup.subnote.ui.resumen.ResumenScreen
import com.tecsup.subnote.viewmodel.DetalleViewModel
import com.tecsup.subnote.viewmodel.DetalleViewModelFactory
import com.tecsup.subnote.viewmodel.FormularioViewModel
import com.tecsup.subnote.viewmodel.FormularioViewModelFactory
import com.tecsup.subnote.viewmodel.ListaViewModel
import com.tecsup.subnote.viewmodel.ListaViewModelFactory
import com.tecsup.subnote.viewmodel.ResumenViewModel
import com.tecsup.subnote.viewmodel.ResumenViewModelFactory

@Composable
fun NavGraph(repository: SuscripcionRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Lista.route) {

        // ---- Lista ----
        composable(Screen.Lista.route) {
            val viewModel: ListaViewModel = viewModel(factory = ListaViewModelFactory(repository))
            ListaSuscripcionesScreen(
                viewModel = viewModel,
                onSuscripcionClick = { id -> navController.navigate(Screen.Detalle.crearRuta(id)) },
                onNuevaClick = { navController.navigate(Screen.Formulario.crearRutaNueva()) },
                onResumenClick = { navController.navigate(Screen.Resumen.route) }
            )
        }

        // ---- Detalle ----
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
                onEditar = { editId -> navController.navigate(Screen.Formulario.crearRutaEditar(editId)) }
            )
        }

        // ---- Formulario (crear / editar) ----
        composable(
            route = Screen.Formulario.route,
            arguments = listOf(navArgument("id") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            val viewModel: FormularioViewModel = viewModel(factory = FormularioViewModelFactory(repository))
            FormularioSuscripcionScreen(
                suscripcionId = if (id == -1L) null else id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }

        // ---- Resumen ----
        composable(Screen.Resumen.route) {
            val viewModel: ResumenViewModel = viewModel(factory = ResumenViewModelFactory(repository))
            ResumenScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
