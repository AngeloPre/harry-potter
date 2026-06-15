package com.example.harrypotter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.harrypotter.R
import com.example.harrypotter.helper.SpellIcon
import com.example.harrypotter.helper.TextNormalizer
import com.example.harrypotter.model.Spell

class SpellAdapter(
    private val onClick: (Spell) -> Unit
) : RecyclerView.Adapter<SpellAdapter.SpellViewHolder>() {

    private val allSpells = mutableListOf<Spell>()
    private val visibleSpells = mutableListOf<Spell>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spell, parent, false)
        return SpellViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SpellViewHolder, position: Int) {
        holder.bind(visibleSpells[position], position)
    }

    override fun getItemCount(): Int = visibleSpells.size

    fun submitList(spells: List<Spell>) {
        allSpells.clear()
        allSpells.addAll(spells)
        visibleSpells.clear()
        visibleSpells.addAll(spells)
        notifyDataSetChanged()
    }

    /**
     * Filtra a lista comparando nome e descricao ja normalizados (sem acento,
     * sem diferenciar maiusculas/minusculas) contra a busca normalizada.
     */
    fun filter(query: String) {
        val normalizedQuery = TextNormalizer.normalize(query)
        visibleSpells.clear()
        if (normalizedQuery.isEmpty()) {
            visibleSpells.addAll(allSpells)
        } else {
            allSpells.filterTo(visibleSpells) { spell ->
                TextNormalizer.normalize(spell.name).contains(normalizedQuery) ||
                    TextNormalizer.normalize(spell.description).contains(normalizedQuery)
            }
        }
        notifyDataSetChanged()
    }

    class SpellViewHolder(
        itemView: View,
        private val onClick: (Spell) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val iconBg: FrameLayout = itemView.findViewById(R.id.spellIconBg)
        private val icon: ImageView = itemView.findViewById(R.id.spellIcon)
        private val name: TextView = itemView.findViewById(R.id.spellName)
        private val description: TextView = itemView.findViewById(R.id.spellDescription)

        fun bind(spell: Spell, position: Int) {
            name.text = spell.name
            description.text = truncate(spell.description)

            val style = SpellIcon.styleFor(spell)
            iconBg.setBackgroundResource(style.backgroundRes)
            icon.setImageResource(style.iconRes)
            icon.setColorFilter(ContextCompat.getColor(itemView.context, style.tintRes))

            itemView.setOnClickListener { onClick(spell) }
        }

        // "Pipe" que reduz a descricao na lista para no maximo 25 caracteres.
        private fun truncate(text: String): String {
            return if (text.length > MAX_DESCRIPTION) {
                text.take(MAX_DESCRIPTION).trimEnd() + "…"
            } else {
                text
            }
        }

        companion object {
            private const val MAX_DESCRIPTION = 25
        }
    }
}
