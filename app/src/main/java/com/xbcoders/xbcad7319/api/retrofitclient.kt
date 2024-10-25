package com.xbcoders.xbcad7319.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofitclient {
    // This object was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    // Base URL of the API, fetched from the application constants for consistency.

    // Base URL for the API, sourced from application constants to ensure uniformity.
    private const val BASE_URL = "https://express-mart.onrender.com/api/"

    //private const val BASE_URL = "http://10.0.2.2:5000/api/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}