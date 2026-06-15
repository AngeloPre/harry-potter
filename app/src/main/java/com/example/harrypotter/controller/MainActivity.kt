package com.example.harrypotter.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.harrypotter.R
import com.example.harrypotter.helper.BottomNav

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK)
        )
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cards principais
        findViewById<View>(R.id.cardCharacters).setOnClickListener { open(PersonagemPorID::class.java) }
        findViewById<View>(R.id.cardProfessors).setOnClickListener { open(ProfessorEscola::class.java) }
        findViewById<View>(R.id.cardSpells).setOnClickListener { open(Feiticos::class.java) }

        // Estudantes por Casa
        findViewById<View>(R.id.seeAll).setOnClickListener { openHouse(null) }

        configureHouse(
            R.id.houseGryffindor, R.drawable.shield_gryffindor,
            R.string.gryffindor, R.string.gryffindor_trait,
            R.drawable.badge_gryffindor, R.color.badge_gryffindor_text, "gryffindor"
        )
        configureHouse(
            R.id.houseSlytherin, R.drawable.shield_slytherin,
            R.string.slytherin, R.string.slytherin_trait,
            R.drawable.badge_slytherin, R.color.badge_slytherin_text, "slytherin"
        )
        configureHouse(
            R.id.houseHufflepuff, R.drawable.shield_hufflepuff,
            R.string.hufflepuff, R.string.hufflepuff_trait,
            R.drawable.badge_hufflepuff, R.color.badge_hufflepuff_text, "hufflepuff"
        )
        configureHouse(
            R.id.houseRavenclaw, R.drawable.shield_ravenclaw,
            R.string.ravenclaw, R.string.ravenclaw_trait,
            R.drawable.badge_ravenclaw, R.color.badge_ravenclaw_text, "ravenclaw"
        )

        // Sair
        findViewById<View>(R.id.ExitBtn).setOnClickListener { finish() }

        // Barra inferior
        BottomNav.setup(this, BottomNav.Tab.HOME)
    }

    private fun configureHouse(
        wrapperId: Int,
        shieldRes: Int,
        nameRes: Int,
        traitRes: Int,
        badgeRes: Int,
        badgeTextColor: Int,
        house: String
    ) {
        val card = findViewById<View>(wrapperId)
        card.findViewById<FrameLayout>(R.id.houseShield).setBackgroundResource(shieldRes)
        card.findViewById<TextView>(R.id.houseName).setText(nameRes)
        card.findViewById<TextView>(R.id.houseBadge).apply {
            setText(traitRes)
            setBackgroundResource(badgeRes)
            setTextColor(resources.getColor(badgeTextColor, theme))
        }
        card.setOnClickListener { openHouse(house) }
    }

    private fun openHouse(house: String?) {
        val intent = Intent(this, EstudantePorCasa::class.java)
        if (house != null) {
            intent.putExtra(EstudantePorCasa.EXTRA_HOUSE, house)
        }
        startActivity(intent)
    }

    private fun open(target: Class<*>) {
        startActivity(Intent(this, target))
    }
}
