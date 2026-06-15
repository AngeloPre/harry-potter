package com.example.harrypotter.controller

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.harrypotter.R
import com.example.harrypotter.helper.BottomNav
import com.example.harrypotter.helper.SpellIcon
import com.example.harrypotter.model.Spell

class SpellDetail : AppCompatActivity() {

    private lateinit var iconBg: FrameLayout
    private lateinit var icon: ImageView
    private lateinit var tvSpellName: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_spell_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        iconBg = findViewById(R.id.spellIconBg)
        icon = findViewById(R.id.spellIcon)
        tvSpellName = findViewById(R.id.tvSpellName)
        tvDescription = findViewById(R.id.tvDescription)

        val spell = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(SPELL_EXTRA, Spell::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(SPELL_EXTRA) as? Spell
        }

        if (spell != null) {
            showSpell(spell)
        } else {
            tvSpellName.text = ""
            tvDescription.text = ""
            Toast.makeText(this, "Feitico nao encontrado", Toast.LENGTH_SHORT).show()
        }

        // Volta para a lista de feiticos
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        BottomNav.setup(this, BottomNav.Tab.SPELLS)
    }

    private fun showSpell(spell: Spell) {
        tvSpellName.text = spell.name
        tvDescription.text = spell.description

        val style = SpellIcon.styleFor(spell)
        icon.setImageResource(style.iconRes)
        icon.setColorFilter(ContextCompat.getColor(this, style.tintRes))
        iconBg.setBackgroundResource(
            if (style.backgroundRes == R.drawable.bg_spell_icon_red) {
                R.drawable.bg_spell_circle_red
            } else {
                R.drawable.bg_spell_circle_purple
            }
        )
    }

    companion object {
        const val SPELL_EXTRA = "SPELL"
    }
}
