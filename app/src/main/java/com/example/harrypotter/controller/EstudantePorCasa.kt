package com.example.harrypotter.controller

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstudantePorCasa : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var houseCharacterAdapter: HouseCharacterAdapter
    private lateinit var harryPotterService: HarryPotterService

    private val filters = listOf(
        HouseFilter(R.id.filterGryffindor, "gryffindor", "Gryffindor", R.color.gryffindor),
        HouseFilter(R.id.filterSlytherin, "slytherin", "Slytherin", R.color.slytherin),
        HouseFilter(R.id.filterRavenclaw, "ravenclaw", "Ravenclaw", R.color.ravenclaw),
        HouseFilter(R.id.filterHufflepuff, "hufflepuff", "Hufflepuff", R.color.hufflepuff)
    )
    private var selectedHouse: String = "gryffindor"

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

        setupFilters()

        // Pre-seleciona a casa enviada pela tela principal (ou Gryffindor por padrao)
        val house = intent.getStringExtra(EXTRA_HOUSE) ?: "gryffindor"
        selectHouse(house)

        BottomNav.setup(this, BottomNav.Tab.HOUSES)
    }

    private fun setupFilters() {
        filters.forEach { filter ->
            val card = findViewById<MaterialCardView>(filter.wrapperId)
            val color = ContextCompat.getColor(this, filter.colorRes)

            card.findViewById<TextView>(R.id.houseFilterName).text = filter.display
            card.findViewById<ImageView>(R.id.houseShield).setColorFilter(color)
            card.findViewById<android.view.View>(R.id.houseArc)
                .background.mutate().setTint(color)

            card.setOnClickListener { selectHouse(filter.house) }
        }
    }

    private fun selectHouse(house: String) {
        selectedHouse = house

        val strokePx = (2 * resources.displayMetrics.density).toInt()
        val elevationPx = 2 * resources.displayMetrics.density

        filters.forEach { filter ->
            val card = findViewById<MaterialCardView>(filter.wrapperId)
            val isSelected = filter.house == house
            if (isSelected) {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_surface))
                card.strokeColor = ContextCompat.getColor(this, filter.colorRes)
                card.strokeWidth = strokePx
                card.cardElevation = elevationPx
            } else {
                card.setCardBackgroundColor(Color.parseColor("#F0EEF6"))
                card.strokeWidth = 0
                card.cardElevation = 0f
            }
        }

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

    private data class HouseFilter(
        val wrapperId: Int,
        val house: String,
        val display: String,
        val colorRes: Int
    )

    companion object {
        const val EXTRA_HOUSE = "extra_house"
    }
}
