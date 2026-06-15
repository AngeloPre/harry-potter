package com.example.harrypotter.controller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var fotoImageView: ImageView
    private lateinit var botaoPesquisar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var harryPotterService: HarryPotterService

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
        fotoImageView = findViewById(R.id.ivPhoto)
        botaoPesquisar = findViewById(R.id.btnSearch)
        progressBar = findViewById(R.id.progressBar)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)

        clearCharacterInfo()

        botaoPesquisar.setOnClickListener {
            searchCharacterBySimpleId()
        }

        BottomNav.setup(this, BottomNav.Tab.CHARACTERS)
    }

    private fun searchCharacterBySimpleId() {
        val simpleId = idEditText.text.toString().trim().toIntOrNull()

        if (simpleId == null) {
            Toast.makeText(this, "Digite um ID numerico", Toast.LENGTH_SHORT).show()
            return
        }

        val uuid = CharacterIdMapper.getUuid(simpleId)

        if (uuid == null) {
            Toast.makeText(
                this,
                "ID invalido. Use: ${CharacterIdMapper.validIds()}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        getCharacterApi(uuid)
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
                    clearCharacterInfo()
                    Toast.makeText(
                        this@PersonagemPorID,
                        "Personagem nao encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                hideProgressBar()
                Log.e("PersonagemPorID", "Erro ao obter personagem", e)
                Toast.makeText(
                    this@PersonagemPorID,
                    "Erro ao obter personagem: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCharacterInfo(character: CharacterById) {
        nomeTextView.text = "Nome: ${character.name}"
        especieTextView.text = "Especie: ${character.species}"
        casaTextView.text = "Casa: ${character.house.ifBlank { "Sem casa" }}"

        if (character.photo.isNotBlank()) {
            Picasso.get().load(character.photo).into(fotoImageView)
        } else {
            fotoImageView.setImageDrawable(null)
            Toast.makeText(this, "Personagem sem foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearCharacterInfo() {
        nomeTextView.text = ""
        especieTextView.text = ""
        casaTextView.text = ""
        fotoImageView.setImageDrawable(null)
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        fotoImageView.visibility = View.GONE
        botaoPesquisar.isEnabled = false
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        fotoImageView.visibility = View.VISIBLE
        botaoPesquisar.isEnabled = true
    }
}
