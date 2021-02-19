package com.e.got_compagnon.service

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json

object NetworkManager {
    fun createHttpClient(): HttpClient {
        val jsonConfig = Json{
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }

        //configure client
        return HttpClient(OkHttp){
            install(JsonFeature){
                serializer = KotlinxSerializer(jsonConfig)
            }
        }
    }
}