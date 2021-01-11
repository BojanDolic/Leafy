package com.electroniccode.leafy.models

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Proizvod (
    val autorID: String = "",
    var slikaProizvoda: String = "",
    val naslovProizvoda: String = "",
    val opisProizvoda: String = "",
    val cijenaProizvoda: Float = 0.0f,
    val brojTelefona: String = "",
    val tipProizvoda: String = "",
    val mjestoLat: String = "",
    val mjestoLng: String = "",
    @Exclude
    var udaljenost: Float = 0.0f
) : Serializable
