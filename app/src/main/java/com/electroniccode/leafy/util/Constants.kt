package com.electroniccode.leafy.util

import android.Manifest
import androidx.datastore.preferences.core.preferencesKey
import java.util.regex.Pattern

object Constants {

    val locationPerm = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    const val PLANT_TYPE_CORN = 1
    const val PLANT_TYPE_POTATO = 2

    const val PICK_GALLERY_IMAGE_RQ = 100

    const val TIP_BOLEST = 1
    const val TIP_OSTALO = 2

    //region Leafy book kategorije
    const val KATEGORIJA_BILJKE = "biljke"
    const val KATEGORIJA_FUNGICIDI = "preparati"
    //endregion

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

    const val DATASTORE_NAME = "phonePrivacyStore"
    val DATASTORE_PHONE_KEY = preferencesKey<Boolean>("phoneKey")

    const val PHONE_ACCEPTED = 1
    const val PHONE_DECLINED = 0


}