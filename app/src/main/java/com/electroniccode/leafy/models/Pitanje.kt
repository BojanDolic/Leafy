package com.electroniccode.leafy.models

import com.google.firebase.Timestamp

data class Pitanje(
    val tekstPitanja: String = "",
    val opis: String = "",
    val slikaPitanja: String = "",
    @Transient val datum: Timestamp? = null,
    @Transient val komentari: List<Komentar>? = null
) {

    override fun equals(other: Any?): Boolean {

        if(javaClass != other?.javaClass)
            return false
        other as Pitanje

        if(tekstPitanja != other.tekstPitanja)
            return false
        if(opis != other.opis)
            return false
        if(slikaPitanja != other.slikaPitanja)
            return false
        if(datum !== other.datum)
            return false
        if(komentari !== other.komentari)
            return false

        return true
    }
}
