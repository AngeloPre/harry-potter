package com.example.harrypotter.controller

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
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
import com.example.harrypotter.helper.CharacterIdMapper
import com.example.harrypotter.model.CharacterById
import com.example.harrypotter.service.HarryPotterService
import com.example.harrypotter.service.RetrofitProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonagemPorID : AppCompatActivity() {

    private lateinit var idEditText: EditText
    private lateinit var nomeTextView: TextView
    private lateinit var especieTextView: TextView
    private lateinit var casaTextView: TextView
    private lateinit var ocupacaoTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var casaBadge: TextView
    private lateinit var fotoImageView: ImageView
    private lateinit var botaoPrev: ImageView
    private lateinit var botaoNext: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var harryPotterService: HarryPotterService

    private val minId = CharacterIdMapper.minId()
    private val maxId = CharacterIdMapper.maxId()
    private var currentId = minId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personagem_por_id)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idEditText = findViewById(R.id.etID)
        nomeTextView = findViewById(R.id.tvNome)
        especieTextView = findViewById(R.id.tvSpecies)
        casaTextView = findViewById(R.id.tvHouse)
        ocupacaoTextView = findViewById(R.id.tvOccupation)
        statusTextView = findViewById(R.id.tvStatus)
        casaBadge = findViewById(R.id.tvHouseBadge)
        fotoImageView = findViewById(R.id.ivPhoto)
        botaoPrev = findViewById(R.id.btnPrev)
        botaoNext = findViewById(R.id.btnNext)
        progressBar = findViewById(R.id.progressBar)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)

        botaoPrev.setOnClickListener { goToId(currentId - 1) }
        botaoNext.setOnClickListener { goToId(currentId + 1) }

        // Clicar/editar o numero: confirma ao pressionar "ok" ou ao sair do campo
        idEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                commitTypedId()
                idEditText.clearFocus()
                true
            } else {
                false
            }
        }
        idEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) commitTypedId()
        }

        BottomNav.setup(this, BottomNav.Tab.CHARACTERS)

        goToId(minId)
    }

    /** Le o id digitado, valida e carrega; se invalido, volta ao id atual. */
    private fun commitTypedId() {
        val typed = idEditText.text.toString().trim().toIntOrNull()
        if (typed == null) {
            idEditText.setText(currentId.toString())
        } else {
            goToId(typed)
        }
    }

    /** Navega para um id, travando entre minId e maxId (nao volta abaixo de 1). */
    private fun goToId(id: Int) {
        val clamped = id.coerceIn(minId, maxId)
        currentId = clamped
        idEditText.setText(clamped.toString())
        idEditText.setSelection(idEditText.text.length)

        updateArrows()

        val uuid = CharacterIdMapper.getUuid(clamped) ?: return
        getCharacterApi(uuid)
    }

    private fun updateArrows() {
        setArrowEnabled(botaoPrev, currentId > minId)
        setArrowEnabled(botaoNext, currentId < maxId)
    }

    private fun setArrowEnabled(arrow: ImageView, enabled: Boolean) {
        arrow.isEnabled = enabled
        arrow.alpha = if (enabled) 1f else 0.3f
    }

    private fun getCharacterApi(uuid: String) {
        lifecycleScope.launch {
            try {
                showProgressBar()

                val characters = withContext(Dispatchers.IO) {
                    harryPotterService.getCharacterById(uuid)
                }

                hideProgressBar()

                val character = characters.firstOrNull()
                if (character != null) {
                    showCharacterInfo(character)
                } else {
                    Toast.makeText(this@PersonagemPorID, "Personagem nao encontrado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                hideProgressBar()
                Toast.makeText(
                    this@PersonagemPorID,
                    "Erro ao obter personagem: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCharacterInfo(character: CharacterById) {
        nomeTextView.text = character.name
        especieTextView.text = translateSpecies(character.species)
        casaTextView.text = character.house.ifBlank { "Sem casa" }
        ocupacaoTextView.text = occupationOf(character)
        statusTextView.text = if (character.alive) "Vivo" else "Morto"

        bindHouseBadge(character.house)

        if (character.photo.isNotBlank()) {
            Picasso.get().load(character.photo).into(fotoImageView)
        } else {
            fotoImageView.setImageDrawable(null)
        }
    }

    private fun bindHouseBadge(house: String) {
        val colorRes = houseColor(house)
        if (house.isBlank() || colorRes == null) {
            casaBadge.visibility = View.GONE
        } else {
            casaBadge.visibility = View.VISIBLE
            casaBadge.text = house
            casaBadge.background.mutate().setTint(ContextCompat.getColor(this, colorRes))
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

    /** Ocupacao derivada dos campos da API (nao ha campo literal de ocupacao). */
    private fun occupationOf(character: CharacterById): String {
        return when {
            character.hogwartsStaff -> "Funcionário de Hogwarts"
            character.hogwartsStudent -> "Estudante de Hogwarts"
            character.wizard -> "Bruxo(a)"
            else -> "—"
        }
    }

    private fun translateSpecies(species: String): String {
        if (species.isBlank()) return "—"
        return SPECIES_PT[species.lowercase()] ?: species.replaceFirstChar { it.uppercase() }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    companion object {
        private val SPECIES_PT = mapOf(
            "human" to "Humano",
            "half-giant" to "Meio-gigante",
            "giant" to "Gigante",
            "half-human" to "Meio-humano",
            "ghost" to "Fantasma",
            "werewolf" to "Lobisomem",
            "house-elf" to "Elfo doméstico",
            "goblin" to "Duende",
            "centaur" to "Centauro",
            "vampire" to "Vampiro",
            "dragon" to "Dragão",
            "cat" to "Gato",
            "owl" to "Coruja",
            "poltergeist" to "Poltergeist"
        )
    }
}
