package com.example.harrypotter.controller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import com.example.harrypotter.model.StaffCharacter
import com.example.harrypotter.service.HarryPotterService
import com.example.harrypotter.service.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfessorEscola : AppCompatActivity() {

    private lateinit var etProf: EditText
    private lateinit var tvName: TextView
    private lateinit var tvAltNames: TextView
    private lateinit var tvSpeciesProf: TextView
    private lateinit var tvHouseProf: TextView
    private lateinit var btnSearchProf: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var harryPotterService: HarryPotterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_professor_escola)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etProf = findViewById(R.id.etProf)
        tvName = findViewById(R.id.tvName)
        tvAltNames = findViewById(R.id.tvAltNames)
        tvSpeciesProf = findViewById(R.id.tvSpeciesProf)
        tvHouseProf = findViewById(R.id.tvHouseProf)
        btnSearchProf = findViewById(R.id.btnSearchProf)
        progressBar = findViewById(R.id.progressBar2)
        harryPotterService = HarryPotterService(RetrofitProvider.harryPotterApiService)

        clearProfessorInfo()

        btnSearchProf.setOnClickListener {
            searchProfessor()
        }

        BottomNav.setup(this)
    }

    private fun searchProfessor() {
        val searchName = normalizeName(etProf.text.toString())

        if (searchName.isBlank()) {
            Toast.makeText(this, "Digite o nome do professor", Toast.LENGTH_SHORT).show()
            return
        }

        getStaffApi(searchName)
    }

    private fun getStaffApi(searchName: String) {
        lifecycleScope.launch {
            try {
                showProgressBar()

                val staff = withContext(Dispatchers.IO) {
                    harryPotterService.getStaff()
                }

                hideProgressBar()

                val professor = staff.firstOrNull { it.matchesSearchName(searchName) }
                if (professor != null) {
                    showProfessorInfo(professor)
                } else {
                    clearProfessorInfo()
                    Toast.makeText(
                        this@ProfessorEscola,
                        "Professor nao encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                hideProgressBar()
                Log.e("ProfessorEscola", "Erro ao obter professores", e)
                Toast.makeText(
                    this@ProfessorEscola,
                    "Erro ao obter professores: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun StaffCharacter.matchesSearchName(searchName: String): Boolean {
        val professorName = normalizeName(name)
        val nameParts = professorName.split(" ")

        return professorName == searchName || nameParts.any { it == searchName }
    }

    private fun normalizeName(name: String): String {
        return name.trim()
            .lowercase()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    private fun showProfessorInfo(professor: StaffCharacter) {
        tvName.text = "Nome: ${professor.name}"
        tvAltNames.text = "Nomes alternativos: ${formatAlternateNames(professor.alternateNames)}"
        tvSpeciesProf.text = "Especie: ${professor.species}"
        tvHouseProf.text = "Casa: ${professor.house.ifBlank { "Sem casa" }}"
    }

    private fun formatAlternateNames(alternateNames: List<String>): String {
        return if (alternateNames.isEmpty()) {
            "Nenhum"
        } else {
            alternateNames.joinToString(separator = "\n")
        }
    }

    private fun clearProfessorInfo() {
        tvName.text = ""
        tvAltNames.text = ""
        tvSpeciesProf.text = ""
        tvHouseProf.text = ""
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        btnSearchProf.isEnabled = false
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        btnSearchProf.isEnabled = true
    }
}
