package com.e.got_compagnon.Common

import com.e.got_compagnon.Interface.NewsService
import com.e.got_compagnon.Remote.RetrofitClient

object Common {
    val BASE_URL = "http://newsapi.org/v2/"
    val API_KEY = "2346a25a05244ccba23cc13fcaeef8d2"

    val newsService:NewsService
    get()=RetrofitClient.getClient(BASE_URL).create(NewsService::class.java)
}