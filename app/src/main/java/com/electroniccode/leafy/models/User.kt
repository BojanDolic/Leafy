package com.electroniccode.leafy.models

data class User(
    var uid: String = "",
    val korisnickoIme: String = "",
    val slikaKorisnika: String = "",
    val token: String = "",
    val odgovora: Int = 0,
    val najboljihOdgovora: Int = 0,
    val pitanja: Int = 0) {

}