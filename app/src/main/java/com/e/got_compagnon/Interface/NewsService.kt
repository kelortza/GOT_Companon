package com.e.got_compagnon.Interface

import com.e.got_compagnon.api.NewsApiJSON
import retrofit2.http.GET

interface NewsService {

    //@GET("/v1/search?keywords=ghost+of+tsushima&language=es&apiKey=p33knNopYasyd3ZRiFC-rEWORSbW5DuOyqcJH0F22VpUWG2j")
    @GET("/v2/everything?q=Ghost+Of+Tsushima&sortBy=popularity&apiKey=2346a25a05244ccba23cc13fcaeef8d2")
    suspend fun getNews() : NewsApiJSON

}