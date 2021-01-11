package com.electroniccode.leafy.viewmodels

import android.app.Application
import android.net.Uri
import android.telephony.PhoneNumberUtils
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.*
import com.electroniccode.leafy.datastore.DataStoreHelper
import com.electroniccode.leafy.models.Mjesto
import com.electroniccode.leafy.models.Proizvod
import com.electroniccode.leafy.util.Constants
import com.electroniccode.leafy.util.UtilFunctions
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.math.RoundingMode

class LeafyTradeViewModel(app: Application) : AndroidViewModel(app) {

    val context = getApplication<Application>().applicationContext

    val izabranoMjesto: MutableLiveData<Mjesto?> = MutableLiveData(null)

    var izabranProizvod: MutableLiveData<String> = MutableLiveData("")

    var slikaProizvodaUri: MutableLiveData<Uri?> = MutableLiveData(null)

    var naslovProizvoda = ""
    var opisProizvoda = ""

    var cijenaProizvoda: Float = .0f

    var brojTelefona: String = ""

    val dataStore = DataStoreHelper(context)


    fun kreirajProizvod(user: FirebaseUser): Proizvod {
        return Proizvod(
            user.uid,
            naslovProizvoda = naslovProizvoda,
            opisProizvoda = opisProizvoda,
            cijenaProizvoda = cijenaProizvoda,
            brojTelefona = brojTelefona,
            mjestoLat = izabranoMjesto.value?.lat.toString(),
            mjestoLng = izabranoMjesto.value?.lng.toString(),
            tipProizvoda = izabranProizvod.value!!
        )
    }

    fun isBrojTelefonaValid(): Boolean {
        return PhoneNumberUtils.isGlobalPhoneNumber(brojTelefona)
    }

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

    fun isPhoneUsageAccepted(): LiveData<Boolean> {
        return dataStore.isPhoneUsageAccepted().asLiveData()
    }


}