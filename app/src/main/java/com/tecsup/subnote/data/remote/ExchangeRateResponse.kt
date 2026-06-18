package com.tecsup.subnote.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API de tipo de cambio (ExchangeRate-API, endpoint abierto y gratuito).
 *
 * Ejemplo real de https://open.er-api.com/v6/latest/USD :
 * {
 *   "result": "success",
 *   "base_code": "USD",
 *   "rates": { "USD": 1, "PEN": 3.75, "EUR": 0.92, ... }
 * }
 *
 * A diferencia de otras APIs, esta devuelve TODAS las monedas en el mapa "rates",
 * relativas a la moneda base que pedimos en la URL. Para convertir, basta con
 * leer rates[monedaDestino].
 */
data class ExchangeRateResponse(
    val result: String,
    @SerializedName("base_code") val baseCode: String,
    val rates: Map<String, Double>
)
