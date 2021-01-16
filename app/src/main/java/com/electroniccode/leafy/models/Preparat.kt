package com.electroniccode.leafy.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

class Preparat (
    val imePreparata: String = "",
    val tipPreparata: String = "",
    val opis: String = "",
    val slika: String = "",
    val usjevi: List<String> = listOf(),
    val kolicinaPrimjene: List<String> = listOf(),
    val vrijemePrimjene: List<String> = listOf()
) : Serializable