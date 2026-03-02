package com.sudani.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: SudaniApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SudaniConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SudaniApiService::class.java)
    }
}
