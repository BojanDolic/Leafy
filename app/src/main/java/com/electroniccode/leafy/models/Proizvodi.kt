package com.electroniccode.leafy.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class Proizvodi (
    @JsonProperty("listaProizvoda")
    val idProizvoda: Array<String> = arrayOf(),

    @JsonProperty("udaljenostProizvoda")
    val udaljenosti: Array<Float> = arrayOf()
) : Serializable