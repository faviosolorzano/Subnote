package com.tecsup.subnote.ui.navigation

sealed class Screen(val route: String) {
    object Lista : Screen("lista")
    object Detalle : Screen("detalle/{id}") {
        fun crearRuta(id: Long) = "detalle/$id"
    }
}