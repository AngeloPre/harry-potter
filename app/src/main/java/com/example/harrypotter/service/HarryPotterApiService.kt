package com.example.harrypotter.service

import com.example.harrypotter.model.CharacterById
import com.example.harrypotter.model.HouseCharacter
import com.example.harrypotter.model.Spell
import com.example.harrypotter.model.StaffCharacter
import retrofit2.http.GET
import retrofit2.http.Path

interface HarryPotterApiService {
    @GET("api/character/{id}")
    suspend fun getCharacterById(@Path("id") id: String): List<CharacterById>

    @GET("api/characters/staff")
    suspend fun getStaff(): List<StaffCharacter>

    @GET("api/characters/house/{house}")
    suspend fun getCharactersByHouse(@Path("house") house: String): List<HouseCharacter>

    @GET("api/spells")
    suspend fun getSpells(): List<Spell>
}
