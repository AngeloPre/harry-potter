package com.example.harrypotter.service

object HarryPotterProvider {

    val harryPotterService: HarryPotterService =
        HarryPotterService(
            RetrofitProvider.harryPotterApiService
        )
}