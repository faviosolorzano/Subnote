package com.tecsup.subnote.data.repository

import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.local.SuscripcionDao
import com.tecsup.subnote.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow

class SuscripcionRepository(private val dao: SuscripcionDao) {

    // ---- Métodos de Favio: NO se modifican (ListaViewModel y DetalleViewModel los usan) ----
    fun obtenerTodas(): Flow<List<Suscripcion>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Long): Suscripcion? = dao.obtenerPorId(id)

    suspend fun insertar(suscripcion: Suscripcion): Long = dao.insertar(suscripcion)

    suspend fun actualizar(suscripcion: Suscripcion) = dao.actualizar(suscripcion)

    suspend fun eliminar(suscripcion: Suscripcion) = dao.eliminar(suscripcion)

    // ---- NUEVO: agregado por Daniella para la conversión de moneda ----
    /**
     * Devuelve cuántas unidades de [monedaDestino] equivalen a 1 unidad de [monedaOrigen].
     * Ej: obtenerTipoCambio("USD", "PEN") -> 3.75
     *
     * Si ambas monedas son iguales devuelve 1.0 (no hace falta llamar a la red).
     * Si la API no trae la moneda destino, devuelve 1.0 como valor seguro.
     *
     * Esta función es 'suspend': debe llamarse dentro de una corrutina
     * (viewModelScope.launch en el ViewModel). Puede lanzar excepción si no hay
     * Internet; quien la llame debe envolverla en try/catch.
     */
    suspend fun obtenerTipoCambio(monedaOrigen: String, monedaDestino: String): Double {
        if (monedaOrigen == monedaDestino) return 1.0
        val respuesta = RetrofitInstance.api.obtenerTipoCambio(base = monedaOrigen)
        return respuesta.rates[monedaDestino] ?: 1.0
    }
}
