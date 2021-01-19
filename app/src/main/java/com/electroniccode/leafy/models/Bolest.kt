package com.electroniccode.leafy.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

@Keep
data class Bolest(
    val imeBolesti: String = "",
    val slikaBolesti: String = "",
    val naslovi: List<String> = listOf(),
    val opis: List<String> = listOf(),
    val slike: List<String> = listOf(),
    val preparati: List<String> = listOf()
) : Serializable