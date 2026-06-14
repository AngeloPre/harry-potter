package com.example.harrypotter.controller

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.harrypotter.R
import com.example.harrypotter.model.Spell

class SpellDetail : AppCompatActivity() {

    private lateinit var tvSpellName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnSpellExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_spell_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvSpellName = findViewById(R.id.tvSpellName)
        tvDescription = findViewById(R.id.tvDescription)
        btnSpellExit = findViewById(R.id.btnSpellExit)

        val spell = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(SPELL_EXTRA, Spell::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(SPELL_EXTRA) as? Spell
        }

        if (spell != null) {
            tvSpellName.text = spell.name
            tvDescription.text = spell.description
        } else {
            tvSpellName.text = ""
            tvDescription.text = ""
            Toast.makeText(this, "Feitico nao encontrado", Toast.LENGTH_SHORT).show()
        }

        btnSpellExit.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val SPELL_EXTRA = "SPELL"
    }
}
