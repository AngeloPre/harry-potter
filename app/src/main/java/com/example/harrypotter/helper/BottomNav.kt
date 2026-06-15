package com.example.harrypotter.helper

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.harrypotter.R
import com.example.harrypotter.controller.EstudantePorCasa
import com.example.harrypotter.controller.Feiticos
import com.example.harrypotter.controller.MainActivity
import com.example.harrypotter.controller.PersonagemPorID

/**
 * Configura a barra de navegacao inferior (incluida via @layout/bottom_nav)
 * em qualquer tela, destacando a aba atual.
 */
object BottomNav {

    enum class Tab { HOME, CHARACTERS, SPELLS, HOUSES }

    fun setup(activity: AppCompatActivity, active: Tab? = null) {
        bind(activity, R.id.navHome, R.id.ivHome, R.id.tvHome, active == Tab.HOME) {
            go(activity, MainActivity::class.java, clearTop = true)
        }
        bind(activity, R.id.navCharacters, R.id.ivCharacters, R.id.tvCharacters, active == Tab.CHARACTERS) {
            go(activity, PersonagemPorID::class.java)
        }
        bind(activity, R.id.navSpells, R.id.ivSpells, R.id.tvSpells, active == Tab.SPELLS) {
            go(activity, Feiticos::class.java)
        }
        bind(activity, R.id.navHouses, R.id.ivHouses, R.id.tvHouses, active == Tab.HOUSES) {
            go(activity, EstudantePorCasa::class.java)
        }
    }

    private fun bind(
        activity: AppCompatActivity,
        navId: Int,
        iconId: Int,
        textId: Int,
        isActive: Boolean,
        onClick: () -> Unit
    ) {
        val item = activity.findViewById<View>(navId)
        if (isActive) {
            val purple = ContextCompat.getColor(activity, R.color.purple_primary)
            activity.findViewById<ImageView>(iconId).setColorFilter(purple)
            activity.findViewById<TextView>(textId).apply {
                setTextColor(purple)
                setTypeface(typeface, Typeface.BOLD)
            }
            item.setOnClickListener(null)
        } else {
            item.setOnClickListener { onClick() }
        }
    }

    private fun go(activity: AppCompatActivity, target: Class<*>, clearTop: Boolean = false) {
        if (activity::class.java == target) return
        val intent = Intent(activity, target)
        if (clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
    }
}
