package com.tecsup.subnote.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.local.SuscripcionDao
import com.tecsup.subnote.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class SuscripcionRepository(private val dao: SuscripcionDao) {

    private val firestore = FirebaseFirestore.getInstance()

    fun obtenerTodas(userId: String): Flow<List<Suscripcion>> = dao.obtenerTodas(userId)

    suspend fun obtenerPorId(id: Long): Suscripcion? = dao.obtenerPorId(id)

    suspend fun insertar(suscripcion: Suscripcion): Long {
        val id = dao.insertar(suscripcion)
        val conId = suscripcion.copy(id = id)
        firestore
            .collection("usuarios")
            .document(suscripcion.userId)
            .collection("suscripciones")
            .document(id.toString())
            .set(conId.toMap())
            .await()
        return id
    }

    suspend fun actualizar(suscripcion: Suscripcion) {
        dao.actualizar(suscripcion)
        firestore
            .collection("usuarios")
            .document(suscripcion.userId)
            .collection("suscripciones")
            .document(suscripcion.id.toString())
            .set(suscripcion.toMap())
            .await()
    }

    suspend fun eliminar(suscripcion: Suscripcion) {
        dao.eliminar(suscripcion)
        firestore
            .collection("usuarios")
            .document(suscripcion.userId)
            .collection("suscripciones")
            .document(suscripcion.id.toString())
            .delete()
            .await()
    }

    suspend fun obtenerTipoCambio(monedaOrigen: String, monedaDestino: String): Double {
        if (monedaOrigen == monedaDestino) return 1.0
        val respuesta = RetrofitInstance.api.obtenerTipoCambio(base = monedaOrigen)
        return respuesta.rates[monedaDestino] ?: 1.0
    }
}

fun Suscripcion.toMap(): Map<String, Any> = mapOf(
    "id" to id,
    "userId" to userId,
    "nombre" to nombre,
    "monto" to monto,
    "moneda" to moneda,
    "cicloCobro" to cicloCobro,
    "fechaProximoCobro" to fechaProximoCobro,
    "categoria" to categoria,
    "estado" to estado,
    "notas" to notas
)