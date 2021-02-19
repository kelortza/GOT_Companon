package com.e.got_compagnon.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://api.twitch.tv/helix/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create<Endpoints>(Endpoints::class.java)
}