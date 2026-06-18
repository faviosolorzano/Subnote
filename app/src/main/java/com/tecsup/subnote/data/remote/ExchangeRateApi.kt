package com.tecsup.subnote.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz de Retrofit para consultar el tipo de cambio.
 *
 * El endpoint es:  https://open.er-api.com/v6/latest/{base}
 * donde {base} es la moneda de origen (USD, PEN, EUR, ...).
 *
 * No necesita API key. Devuelve un ExchangeRateResponse con todas las tasas.
 */
interface ExchangeRateApi {

    @GET("v6/latest/{base}")
    suspend fun obtenerTipoCambio(
        @Path("base") base: String
    ): ExchangeRateResponse
}
