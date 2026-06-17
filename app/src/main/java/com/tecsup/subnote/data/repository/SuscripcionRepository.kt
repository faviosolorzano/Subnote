package com.tecsup.subnote.data.repository

import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.local.SuscripcionDao
import kotlinx.coroutines.flow.Flow

class SuscripcionRepository(private val dao: SuscripcionDao) {

    fun obtenerTodas(): Flow<List<Suscripcion>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Long): Suscripcion? = dao.obtenerPorId(id)

    suspend fun insertar(suscripcion: Suscripcion): Long = dao.insertar(suscripcion)

    suspend fun actualizar(suscripcion: Suscripcion) = dao.actualizar(suscripcion)

    suspend fun eliminar(suscripcion: Suscripcion) = dao.eliminar(suscripcion)
}