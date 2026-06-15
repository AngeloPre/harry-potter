package com.example.harrypotter.controller

import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harrypotter.R
import com.example.harrypotter.adapter.HouseCharacterAdapter
import com.example.harrypotter.helper.BottomNav
import com.example.harrypotter.service.HarryPotterService
import com.example.harrypotter.service.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstudantePorCasa : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var houseCharacterAdapter: HouseCharacterAdapter
    private lateinit var harryPotterService: HarryPotterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estudante_por_casa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        radioGroup = findViewById(R.id.radioGroup)
        recyclerView = findViewById(R.id.recyclerView)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)
        houseCharacterAdapter = HouseCharacterAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = houseCharacterAdapter

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            getCharactersByHouse(getHouseByRadioButtonId(checkedId))
        }

        // Pre-seleciona a casa enviada pela tela principal (ou Gryffindor por padrao)
        val house = intent.getStringExtra(EXTRA_HOUSE)
        radioGroup.check(getRadioButtonIdByHouse(house))

        BottomNav.setup(this, BottomNav.Tab.HOUSES)
    }

    private fun getHouseByRadioButtonId(checkedId: Int): String {
        return when (checkedId) {
            R.id.rbSlytherin -> "slytherin"
            R.id.rbRavenclaw -> "ravenclaw"
            R.id.rbHufflepuff -> "hufflepuff"
            else -> "gryffindor"
        }
    }

    private fun getRadioButtonIdByHouse(house: String?): Int {
        return when (house?.lowercase()) {
            "slytherin" -> R.id.rbSlytherin
            "ravenclaw" -> R.id.rbRavenclaw
            "hufflepuff" -> R.id.rbHufflepuff
            else -> R.id.rbGryffindor
        }
    }

    companion object {
        const val EXTRA_HOUSE = "extra_house"
    }

    private fun getCharactersByHouse(house: String) {
        lifecycleScope.launch {
            try {
                val characters = withContext(Dispatchers.IO) {
                    harryPotterService.getCharactersByHouse(house)
                }

                houseCharacterAdapter.updateCharacters(characters)
            } catch (e: Exception) {
                Log.e("EstudantePorCasa", "Erro ao obter personagens por casa", e)
                Toast.makeText(
                    this@EstudantePorCasa,
                    "Erro ao obter estudantes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
