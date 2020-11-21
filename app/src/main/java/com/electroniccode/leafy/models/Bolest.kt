package com.electroniccode.leafy.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

@Keep
data class Bolest(
    val imeBolesti: String = "",
    val slikaBolesti: String = "",
    val naslovi: List<String>? = null,
    val opis: List<String>? = null,
    val slike: List<String>? = null,
    val preparati: List<String>? = null
) : Serializable