package com.example.harrypotter.model

import com.google.gson.annotations.SerializedName

data class HouseCharacter(
    val name: String,
    val house: String,
    @SerializedName("image")
    val photo: String
)
