package com.tecsup.subnote.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suscripciones")
data class Suscripcion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val monto: Double,
    val moneda: String,
    val cicloCobro: String,
    val fechaProximoCobro: Long,
    val categoria: String,
    val estado: String = "activa",
    val notas: String = ""
)