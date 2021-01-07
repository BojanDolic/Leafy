package com.electroniccode.leafy.util

import java.util.regex.Pattern

object Constants {

    const val PLANT_TYPE_CORN = 1
    const val PLANT_TYPE_POTATO = 2

    const val PICK_GALLERY_IMAGE_RQ = 100

    const val TIP_BOLEST = 1
    const val TIP_OSTALO = 2

    val proizvodi = arrayOf(
        "Kukuruz",
        "Pšenica",
        "Krompir",
        "Krastavac",
        "Jabuka",
        "Sljiva")

    enum class PROIZVOD_PLANT_TYPE {
        TYPE_KUKURUZ,
        TYPE_PSENICA,
        TYPE_KROMPIR,
        TYPE_KRASTAVAC,
        TYPE_JABUKA,
        TYPE_SLJIVA
    }

    const val specialChars = "[!@#\$%&*()_+=|<>?{}\\\\[\\\\]~-]"

}