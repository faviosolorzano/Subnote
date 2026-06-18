package com.tecsup.subnote.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Instancia única (singleton) de Retrofit para toda la app.
 *
 * BASE_URL apunta al endpoint abierto de ExchangeRate-API, que sí soporta el
 * sol peruano (PEN) y la mayoría de monedas. Se usa Gson para convertir el JSON
 * en objetos ExchangeRateResponse automáticamente.
 *
 * "by lazy" hace que la instancia se cree solo la primera vez que se use.
 */
object RetrofitInstance {

    private const val BASE_URL = "https://open.er-api.com/"

    val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}
