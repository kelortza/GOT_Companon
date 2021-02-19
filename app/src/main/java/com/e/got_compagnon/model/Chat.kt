package com.e.got_compagnon.model

import com.google.firebase.Timestamp

data class Chat (
    val userId: String? = null,
    val message: String? = null,
    //val sentAt: Long? = null,
    val sentAt: Timestamp? = null,
    val isSent: Boolean? = null, //first check
    val imageUrl: String? = null,

    //User
    val username: String? = null,
    val avatarUrl: String? = null

    //Speed vs Consistencia -> no existe uno mejor o peor es segun lo que necesitemos
)