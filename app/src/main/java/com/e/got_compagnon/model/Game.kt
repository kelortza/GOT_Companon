package com.e.got_compagnon.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String? = null,
    val name: String? = null,
    val box_art_url: String? = null
)
