package com.electroniccode.leafy.models

import java.io.Serializable

data class Usjev(
    val imeUsjeva: String = "",
    val bolesti: List<String> = listOf(),
    val kolicinaPrimjene: List<String> = listOf(),
    val vrijemePrimjene: List<String> = listOf(),
    val brojPrimjena: List<String> = listOf()
) : Serializable
