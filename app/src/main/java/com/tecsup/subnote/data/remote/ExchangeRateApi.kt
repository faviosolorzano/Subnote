package com.tecsup.subnote.data.remote

import retrofit2.http.GET
import retrofit2.http.Path


interface ExchangeRateApi {

    @GET("v6/latest/{base}")
    suspend fun obtenerTipoCambio(
        @Path("base") base: String
    ): ExchangeRateResponse
}
