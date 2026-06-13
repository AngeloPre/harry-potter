package com.example.harrypotter.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.harrypotter.R

class MainActivity : AppCompatActivity() {

    private lateinit var StudentBtn: Button
    private lateinit var ProfessorBtn: Button
    private lateinit var StudByHouseBtn: Button
    private lateinit var SpellBtn: Button
    private lateinit var ExitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        StudentBtn = findViewById(R.id.listPersBtn)
        ProfessorBtn = findViewById(R.id.listProfBtn)
        StudByHouseBtn = findViewById(R.id.StudByHouseBtn)
        SpellBtn = findViewById(R.id.SpellBtn)
        ExitBtn = findViewById(R.id.ExitBtn)

        StudentBtn.setOnClickListener {
            val intent = Intent(this, PersonagemPorID::class.java)
            startActivity(intent)
        }

        ProfessorBtn.setOnClickListener {
            val intent = Intent(this, ProfessorEscola::class.java)
            startActivity(intent)
        }

        StudByHouseBtn.setOnClickListener {
            val intent = Intent(this, EstudantePorCasa::class.java)
            startActivity(intent)
        }

        SpellBtn.setOnClickListener {
            val intent = Intent(this, Feiticos::class.java)
            startActivity(intent)
        }

        ExitBtn.setOnClickListener {
            finish()
        }

    }
}