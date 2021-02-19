package com.e.got_compagnon.model

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Games(
    //val games: List<Game>,
    //val status: String,
    //val totalResults: Int
    val Image: String,
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
){
    @SerialName("box_art_url")val imageUrl: String?
    get(){
        return Image?.replace("{width}x{height}", "500x500")
    }
}
