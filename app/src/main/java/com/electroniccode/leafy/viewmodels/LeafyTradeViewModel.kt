package com.electroniccode.leafy.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.electroniccode.leafy.models.Mjesto
import com.electroniccode.leafy.util.Constants
import com.electroniccode.leafy.util.UtilFunctions
import java.math.BigDecimal
import java.math.RoundingMode

class LeafyTradeViewModel : ViewModel() {

    val izabranoMjesto: MutableLiveData<Mjesto?> = MutableLiveData(null)

    var izabranProizvod: MutableLiveData<String> = MutableLiveData("")

    var slikaProizvodaUri: MutableLiveData<Uri?> = MutableLiveData(null)

    var naslovProizvoda = ""
    var opisProizvoda = ""

    var cijenaProizvoda: Float = .0f


    fun setCijena(cijena: Float) {
        var cijenaTemp = BigDecimal(cijena.toString())
        cijenaTemp = cijenaTemp.setScale(2, RoundingMode.HALF_UP)
        cijenaProizvoda = cijenaTemp.toFloat()
    }

    fun isCijenaValid(): Boolean {
        return (cijenaProizvoda > 0.0f)
    }

    fun isNaslovProizvodaValid(): Boolean {
        return (naslovProizvoda.length > 10 && !UtilFunctions.containsSpecialChars(naslovProizvoda))
    }

    fun isOpisProizvodaValid(): Boolean {
        return (opisProizvoda.length > 50 && !UtilFunctions.containsSpecialChars(naslovProizvoda))
    }

}