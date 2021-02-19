package com.e.got_compagnon.model

import com.google.gson.annotations.SerializedName

data class TWStream(
    var title:String? = null,
    @SerializedName("user name")
    var username:String? = null,
    private var thumbnail_url: String? = null
){
    val imageUrl: String?
    get(){
        return thumbnail_url?.replace("{width}x{height}", "500x500")
    }
}
