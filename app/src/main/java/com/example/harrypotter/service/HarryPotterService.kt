package com.example.harrypotter.service

import com.example.harrypotter.model.CharacterById
import com.example.harrypotter.model.HouseCharacter
import com.example.harrypotter.model.Spell
import com.example.harrypotter.model.StaffCharacter

class HarryPotterService(
    private val harryPotterApiService: HarryPotterApiService
) {
    suspend fun getCharacterById(id: String): List<CharacterById> {
        return harryPotterApiService.getCharacterById(id)
    }

    suspend fun getStaff(): List<StaffCharacter> {
        return harryPotterApiService.getStaff()
    }

    suspend fun getCharactersByHouse(house: String): List<HouseCharacter> {
        return harryPotterApiService.getCharactersByHouse(house)
    }

    suspend fun getSpells(): List<Spell> {
        return harryPotterApiService.getSpells()
    }
}
