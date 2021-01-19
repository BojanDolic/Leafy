package com.electroniccode.leafy.models

import java.io.Serializable

data class Biljka(
    val imeBiljke: String = "",
    val slikaBiljke: String = "",
    val naslovi: List<String> = listOf(),
    val opisi: List<String> = listOf()
) : Serializable