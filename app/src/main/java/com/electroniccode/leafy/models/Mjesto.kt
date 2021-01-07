package com.electroniccode.leafy.models

import java.io.Serializable

/**
 * Data class za prodajno mjesto žitarica
 * Mjesto je označeno na mapi
 * Pri kreiranju prodaje korisnik bira svoje mjesto prodaje ( obično kuća ili trgovina )
 */

data class Mjesto(
    val imeMjesta: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
) : Serializable {


    override fun toString(): String {
        return imeMjesta
    }
}

