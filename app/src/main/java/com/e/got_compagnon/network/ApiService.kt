package com.e.got_compagnon.network

import com.e.got_compagnon.Constants
import com.e.got_compagnon.model.TWStreamResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("Client-ID: ${Constants.OAUTH_CLIENT_ID}")
    @GET("streams")
    fun getStreams(): Call<TWStreamResponse>


}