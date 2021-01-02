package com.electroniccode.leafy.models

import com.google.firebase.Timestamp
import java.io.Serializable

data class Pitanje(
    var idDokumenta: String = "",
    val tekstPitanja: String = "",
    val opis: String = "",
    val slikaPitanja: String = "",
    val idAutora: String = "",
    @Transient val datum: Timestamp? = null
) : Serializable {

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

        return true
    }
}
