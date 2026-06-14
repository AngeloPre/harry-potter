package com.example.harrypotter.model

import com.google.gson.annotations.SerializedName

data class StaffCharacter(
    val name: String,
    @SerializedName("alternate_names")
    val alternateNames: List<String>,
    val species: String,
    val house: String
)
