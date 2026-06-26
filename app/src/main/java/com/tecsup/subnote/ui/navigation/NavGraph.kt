package com.tecsup.subnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.subnote.data.repository.SuscripcionRepository
import com.tecsup.subnote.ui.auth.LoginScreen
import com.tecsup.subnote.ui.auth.RegisterScreen
import com.tecsup.subnote.ui.detalle.DetalleSuscripcionScreen
import com.tecsup.subnote.ui.formulario.FormularioSuscripcionScreen
import com.tecsup.subnote.ui.lista.ListaSuscripcionesScreen
import com.tecsup.subnote.ui.perfil.PerfilScreen
import com.tecsup.subnote.ui.resumen.ResumenScreen
import com.tecsup.subnote.viewmodel.*

@Composable
fun NavGraph(repository: SuscripcionRepository, haySession: Boolean) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())

    val startDestination = if (haySession) Screen.Lista.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginExitoso = {
                    navController.navigate(Screen.Lista.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistroExitoso = {
                    navController.navigate(Screen.Lista.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Lista.route) {
            val viewModel: ListaViewModel = viewModel(factory = ListaViewModelFactory(repository))
            ListaSuscripcionesScreen(
                viewModel = viewModel,
                onSuscripcionClick = { id -> navController.navigate(Screen.Detalle.crearRuta(id)) },
                onNuevaClick = { navController.navigate(Screen.Formulario.crearRutaNueva()) },
                onResumenClick = { navController.navigate(Screen.Resumen.route) },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) },
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                onBack = { navController.popBackStack() },
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
                onEditar = { editId -> navController.navigate(Screen.Formulario.crearRutaEditar(editId)) }
            )
        }

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

        composable(Screen.Resumen.route) {
            val viewModel: ResumenViewModel = viewModel(factory = ResumenViewModelFactory(repository))
            ResumenScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}