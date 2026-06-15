package com.example.harrypotter.controller

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.harrypotter.R
import com.example.harrypotter.helper.BottomNav
import com.example.harrypotter.helper.TextNormalizer
import com.example.harrypotter.model.StaffCharacter
import com.example.harrypotter.service.HarryPotterService
import com.example.harrypotter.service.RetrofitProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfessorEscola : AppCompatActivity() {

    private lateinit var acProf: AutoCompleteTextView
    private lateinit var detailContainer: LinearLayout
    private lateinit var emptyView: View
    private lateinit var nameView: TextView
    private lateinit var speciesView: TextView
    private lateinit var houseView: TextView
    private lateinit var altNamesView: TextView
    private lateinit var houseBadge: TextView
    private lateinit var photoView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var harryPotterService: HarryPotterService

    private var staff: List<StaffCharacter> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_professor_escola)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        acProf = findViewById(R.id.acProf)
        detailContainer = findViewById(R.id.detailContainer)
        emptyView = findViewById(R.id.emptyView)
        nameView = findViewById(R.id.tvName)
        speciesView = findViewById(R.id.tvSpeciesProf)
        houseView = findViewById(R.id.tvHouseProf)
        altNamesView = findViewById(R.id.tvAltNames)
        houseBadge = findViewById(R.id.tvHouseBadgeProf)
        photoView = findViewById(R.id.ivPhotoProf)
        progressBar = findViewById(R.id.progressBar2)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)

        acProf.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            staff.firstOrNull { it.name == selected }?.let { showProfessorInfo(it) }
            acProf.clearFocus()
        }

        acProf.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTypedName()
                true
            } else {
                false
            }
        }

        BottomNav.setup(this)

        loadStaff()
    }

    private fun loadStaff() {
        lifecycleScope.launch {
            try {
                showProgressBar()

                staff = withContext(Dispatchers.IO) {
                    harryPotterService.getStaff()
                }

                hideProgressBar()

                // Alimenta o autocomplete com os nomes dos professores
                val names = staff.map { it.name }.sorted()
                acProf.setAdapter(
                    ArrayAdapter(this@ProfessorEscola, android.R.layout.simple_dropdown_item_1line, names)
                )
            } catch (e: Exception) {
                hideProgressBar()
                Toast.makeText(
                    this@ProfessorEscola,
                    "Erro ao obter professores: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun searchTypedName() {
        val query = TextNormalizer.normalize(acProf.text.toString())
        if (query.isBlank()) return

        val professor = staff.firstOrNull { it.matchesName(query) }
        if (professor != null) {
            showProfessorInfo(professor)
            acProf.clearFocus()
        } else {
            Toast.makeText(this, "Professor nao encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun StaffCharacter.matchesName(normalizedQuery: String): Boolean {
        val normalizedName = TextNormalizer.normalize(name)
        return normalizedName == normalizedQuery ||
            normalizedName.contains(normalizedQuery) ||
            normalizedName.split(" ").any { it == normalizedQuery }
    }

    private fun showProfessorInfo(professor: StaffCharacter) {
        emptyView.visibility = View.GONE
        detailContainer.visibility = View.VISIBLE

        nameView.text = professor.name
        speciesView.text = translateSpecies(professor.species)
        houseView.text = professor.house.ifBlank { "Sem casa" }
        altNamesView.text = if (professor.alternateNames.isEmpty()) {
            "Nenhum"
        } else {
            professor.alternateNames.joinToString(", ")
        }

        bindHouseBadge(professor.house)

        // Alguns professores nao tem imagem na API (ex.: Dumbledore, Galatea)
        if (professor.photo.isNotBlank()) {
            Picasso.get().load(professor.photo).into(photoView)
        } else {
            photoView.setImageResource(R.drawable.ic_person)
        }
    }

    private fun bindHouseBadge(house: String) {
        val colorRes = houseColor(house)
        if (house.isBlank() || colorRes == null) {
            houseBadge.visibility = View.GONE
        } else {
            houseBadge.visibility = View.VISIBLE
            houseBadge.text = house
            houseBadge.background.mutate().setTint(ContextCompat.getColor(this, colorRes))
        }
    }

    private fun houseColor(house: String): Int? {
        return when (house.lowercase()) {
            "gryffindor" -> R.color.gryffindor
            "slytherin" -> R.color.slytherin
            "hufflepuff" -> R.color.hufflepuff
            "ravenclaw" -> R.color.ravenclaw
            else -> null
        }
    }

    private fun translateSpecies(species: String): String {
        if (species.isBlank()) return "—"
        return when (species.lowercase()) {
            "human" -> "Humano"
            "half-giant" -> "Meio-gigante"
            "ghost" -> "Fantasma"
            "cat" -> "Gato"
            "centaur" -> "Centauro"
            else -> species.replaceFirstChar { it.uppercase() }
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
