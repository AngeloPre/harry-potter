package com.example.harrypotter.controller

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.SystemBarStyle
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var houseCharacterAdapter: HouseCharacterAdapter
    private lateinit var harryPotterService: HarryPotterService

    private val radioButtons = mutableListOf<RadioButton>()

    private val radioToHouse = mapOf(
        R.id.rbGryffindor to "gryffindor",
        R.id.rbSlytherin to "slytherin",
        R.id.rbRavenclaw to "ravenclaw",
        R.id.rbHufflepuff to "hufflepuff"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK)
        )
        setContentView(R.layout.activity_estudante_por_casa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)
        houseCharacterAdapter = HouseCharacterAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = houseCharacterAdapter

        setupRadioButtons()

        // Pre-seleciona a casa enviada pela tela principal (ou Gryffindor por padrao)
        val house = intent.getStringExtra(EXTRA_HOUSE) ?: "gryffindor"
        val radioId = radioToHouse.entries.firstOrNull { it.value == house }?.key ?: R.id.rbGryffindor
        selectRadio(findViewById(radioId))

        BottomNav.setup(this, BottomNav.Tab.HOUSES)
    }

    private fun setupRadioButtons() {
        radioToHouse.keys.forEach { id ->
            val rb = findViewById<RadioButton>(id)
            radioButtons.add(rb)
            rb.setOnClickListener { selectRadio(rb) }
        }
    }

    private fun selectRadio(selected: RadioButton) {
        radioButtons.forEach { it.isChecked = (it == selected) }
        val house = radioToHouse[selected.id] ?: return
        getCharactersByHouse(house)
    }

    private fun getCharactersByHouse(house: String) {
        lifecycleScope.launch {
            try {
                val characters = withContext(Dispatchers.IO) {
                    harryPotterService.getCharactersByHouse(house)
                }

                houseCharacterAdapter.updateCharacters(characters)
                recyclerView.scrollToPosition(0)
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

    companion object {
        const val EXTRA_HOUSE = "extra_house"
    }
}
