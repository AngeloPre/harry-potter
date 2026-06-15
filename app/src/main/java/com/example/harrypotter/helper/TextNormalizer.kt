package com.example.harrypotter.helper

import java.text.Normalizer

/**
 * Normaliza textos para comparacao de busca:
 * remove acentos/diacriticos, passa para minusculas e tira espacos nas pontas.
 * Ex.: "Feitiço" e "feitico" passam a ser equivalentes.
 */
object TextNormalizer {

    private val DIACRITICS = Regex("\\p{Mn}+")

    fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
        return DIACRITICS.replace(decomposed, "")
            .lowercase()
            .trim()
    }
}
