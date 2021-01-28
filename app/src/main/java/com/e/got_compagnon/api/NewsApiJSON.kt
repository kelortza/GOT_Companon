package com.e.got_compagnon.api

data class NewsApiJSON(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)