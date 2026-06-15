package com.example.harrypotter.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
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
import com.example.harrypotter.adapter.SpellAdapter
import com.example.harrypotter.helper.BottomNav
import com.example.harrypotter.model.Spell
import com.example.harrypotter.service.HarryPotterService
import com.example.harrypotter.service.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Feiticos : AppCompatActivity() {

    private lateinit var rvSpells: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var spellAdapter: SpellAdapter
    private lateinit var harryPotterService: HarryPotterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK)
        )
        setContentView(R.layout.activity_feiticos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvSpells = findViewById(R.id.rvSpells)
        etSearch = findViewById(R.id.etSearch)
        progressBar = findViewById(R.id.progressBar4)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)

        spellAdapter = SpellAdapter { spell -> openSpellDetail(spell) }
        rvSpells.layoutManager = LinearLayoutManager(this)
        rvSpells.adapter = spellAdapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                spellAdapter.filter(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<View>(R.id.btnProfile).setOnClickListener { finish() }

        BottomNav.setup(this, BottomNav.Tab.SPELLS)

        getSpellsApi()
    }

    private fun openSpellDetail(spell: Spell) {
        val intent = Intent(this, SpellDetail::class.java)
        intent.putExtra(SpellDetail.SPELL_EXTRA, spell)
        startActivity(intent)
    }

    private fun getSpellsApi() {
        lifecycleScope.launch {
            try {
                showProgressBar()

                val spells = withContext(Dispatchers.IO) {
                    harryPotterService.getSpells()
                }

                hideProgressBar()

                spellAdapter.submitList(spells)
                spellAdapter.filter(etSearch.text?.toString().orEmpty())
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
        rvSpells.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        rvSpells.visibility = View.VISIBLE
    }
}
