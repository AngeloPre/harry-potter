package com.example.harrypotter.model

import java.io.Serializable

data class Spell(
    val name: String,
    val description: String
) : Serializable
