package com.example.harrypotter.helper

import com.example.harrypotter.R
import com.example.harrypotter.model.Spell

/**
 * Define um icone adequado para cada feitico retornado pela API com base no
 * significado (nome + descricao), de forma deterministica. Feiticos ofensivos
 * (maldicoes, fogo, ataques) recebem destaque vermelho; os demais, roxo.
 */
object SpellIcon {

    data class Style(val iconRes: Int, val backgroundRes: Int, val tintRes: Int)

    private data class Rule(val keywords: List<String>, val icon: Int, val red: Boolean)

    // Ordem importa: a primeira regra cujo termo aparecer no texto vence.
    private val RULES = listOf(
        Rule(listOf("shield", "protect", "conceal", "invisible", "disillusion", "surroundings", "patronus", "dementor"), R.drawable.ic_shield, false),
        Rule(listOf("flame", "fire", "explosion", "spark", "flare", "bombard", "burn"), R.drawable.ic_spell_fire, true),
        Rule(listOf("killing", "unforgivable", "dark mark", "torture", "unbearable pain", "complete control", "the death", "to pieces", "destroy"), R.drawable.ic_spell_bolt, true),
        Rule(listOf("cut", "sever", "lacerat"), R.drawable.ic_spell_cut, true),
        Rule(listOf("disarm", "drop whatever", "opponent", "attack", "jinx", "hex", "curse", "boils", "pimples", "bats", "tickl"), R.drawable.ic_spell_bolt, true),
        Rule(listOf("unlock", "opens locked", "locked door"), R.drawable.ic_spell_unlock, false),
        Rule(listOf("water"), R.drawable.ic_spell_water, false),
        Rule(listOf("heal", "bandage", "wound", "broken bones", "airway", "revive", "awaken", "ailment", "poison", "paralysis", "injuries"), R.drawable.ic_spell_heal, false),
        Rule(listOf("illuminat", "light"), R.drawable.ic_spell_sun, false),
        Rule(listOf("levitat", "float", "propell", "transport", "travel", "apparate", "slide", "ankle", "into the air"), R.drawable.ic_spell_levitate, false),
        Rule(listOf("reveal", "detect", "secret", "identity", "mind", "memory", "presence", "eavesdrop", "sight", "eyes"), R.drawable.ic_spell_reveal, false),
        Rule(listOf("clean"), R.drawable.ic_spell_broom, false),
        Rule(listOf("voice", "silenc", "buzz", "sound", "amplif", "tongue"), R.drawable.ic_spell_sound, false),
        Rule(listOf("repair", "fix", "mend", "build", "structure", "tent", "expand", "extend", "capacity"), R.drawable.ic_spell_repair, false),
        Rule(listOf("freeze", "immobil", "petrif", "bound", "slows", "unconscious", "stun"), R.drawable.ic_spell_freeze, false)
    )

    fun styleFor(spell: Spell): Style {
        val text = TextNormalizer.normalize("${spell.name} ${spell.description}")
        val rule = RULES.firstOrNull { r -> r.keywords.any { text.contains(it) } }
        val icon = rule?.icon ?: R.drawable.ic_spells
        return if (rule?.red == true) {
            Style(icon, R.drawable.bg_spell_icon_red, R.color.spell_icon_red)
        } else {
            Style(icon, R.drawable.bg_spell_icon_purple, R.color.spell_icon_purple)
        }
    }
}
