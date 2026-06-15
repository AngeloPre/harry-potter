package com.example.harrypotter.model

import com.google.gson.annotations.SerializedName

data class CharacterById(
    val name: String,
    val species: String,
    val house: String,
    val alive: Boolean = true,
    val wizard: Boolean = false,
    val hogwartsStudent: Boolean = false,
    val hogwartsStaff: Boolean = false,
    @SerializedName("image")
    val photo: String
)
