package com.e.got_compagnon.Interface

import retrofit2.Call
import com.e.got_compagnon.model.Website
import retrofit2.http.GET

interface NewsService {
    @get:GET("sources?apiKey=2346a25a05244ccba23cc13fcaeef8d2")
    val sources: Call<Website>
}