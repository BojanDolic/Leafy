package com.electroniccode.leafy.util

object UtilFunctions {

    fun parseBolestName(bolestName: String): String =
        when(bolestName) {

            "hrdja" -> "Rđa"
            "sive_pjege" -> "Sive pjege"
            "zdrav" -> "Zdrav"
            else -> "Nepoznato"

        }

}