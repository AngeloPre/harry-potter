package com.example.harrypotter.model

import com.google.gson.annotations.SerializedName

data class CharacterById(
    val name: String,
    val species: String,
    val house: String,
    @SerializedName("image")
    val photo: String
)
