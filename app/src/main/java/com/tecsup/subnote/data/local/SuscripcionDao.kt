package com.tecsup.subnote.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SuscripcionDao {

    @Query("SELECT * FROM suscripciones ORDER BY fechaProximoCobro ASC")
    fun obtenerTodas(): Flow<List<Suscripcion>>

    @Query("SELECT * FROM suscripciones WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Suscripcion?

    @Insert
    suspend fun insertar(suscripcion: Suscripcion): Long

    @Update
    suspend fun actualizar(suscripcion: Suscripcion)

    @Delete
    suspend fun eliminar(suscripcion: Suscripcion)
}