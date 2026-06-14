package com.example.harrypotter.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.harrypotter.R
import com.example.harrypotter.model.Spell
import com.example.harrypotter.service.HarryPotterService
import com.example.harrypotter.service.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Feiticos : AppCompatActivity() {

    private lateinit var lvSpells: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var harryPotterService: HarryPotterService
    private var spells: List<Spell> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feiticos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lvSpells = findViewById(R.id.lvSpells)
        progressBar = findViewById(R.id.progressBar4)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)

        lvSpells.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SpellDetail::class.java)
            intent.putExtra(SpellDetail.SPELL_EXTRA, spells[position])
            startActivity(intent)
        }

        getSpellsApi()
    }

    private fun getSpellsApi() {
        lifecycleScope.launch {
            try {
                showProgressBar()

                spells = withContext(Dispatchers.IO) {
                    harryPotterService.getSpells()
                }

                hideProgressBar()

                val spellNames = spells.map { it.name }
                lvSpells.adapter = ArrayAdapter(
                    this@Feiticos,
                    android.R.layout.simple_list_item_1,
                    spellNames
                )
            } catch (e: Exception) {
                hideProgressBar()
                Log.e("Feiticos", "Erro ao obter feiticos", e)
                Toast.makeText(
                    this@Feiticos,
                    "Erro ao obter feiticos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        lvSpells.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        lvSpells.visibility = View.VISIBLE
    }
}
