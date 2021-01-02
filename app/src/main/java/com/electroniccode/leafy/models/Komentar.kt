package com.electroniccode.leafy.models

import java.io.Serializable

data class Komentar(
    var idKomentara: String = "",
    val tekstKomentara: String = "",
    val idAutora: String = "",
    var imeAutora: String = "",
    var slikaKorisnika: String = "",
    var najboljiKomentar: Boolean = false
) : Serializable