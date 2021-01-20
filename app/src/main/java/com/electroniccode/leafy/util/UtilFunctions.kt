package com.electroniccode.leafy.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.electroniccode.leafy.R
import com.electroniccode.leafy.fragments.PICK_IMAGE_RQ
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern

object UtilFunctions {

    enum class RANKOVI(val odgovora: Int) {
        FARMER_POCETNIK(5),
        FARMER_AMATER(10),
        NAPREDNI_FARMER(15)
    }

    /**
     * Pretvara ime bolesti u ime pogodno za firebase query
     *
     * @param bolestName Ime bolesti generisane od strane ML modela
     * @return Vraća ime bolesti pogodno za firebase query
     */
    fun parseBolestName(bolestName: String): String =
        when(bolestName) {
            "hrdja" -> "Rđa"
            "sive_pjege" -> "Sive pjege"
            "zdrav" -> "Zdrav"
            "rana_plamenjaca" -> "Rana plamenjača"
            "kasna_plamenjaca" -> "Kasna plamenjača"
            else -> "Nepoznato"

        }

    /**
     * Određuje rank na osnovu broja najboljih odgovora
     *
     * @param brojNajboljihOdgovora broj najboljih odgovora korisnika
     * @return Vraća naziv ranka
     */
    fun getRank(brojNajboljihOdgovora: Int): String {

        if(brojNajboljihOdgovora < 5)
            return "Farmer početnik"
        else if(brojNajboljihOdgovora >= 5 && brojNajboljihOdgovora < 10)
            return "Farmer amater"
        else if(brojNajboljihOdgovora >= 10 && brojNajboljihOdgovora < 15)
            return "Napredni farmer"
        else if(brojNajboljihOdgovora >= 15 && brojNajboljihOdgovora < 24)
            return "Farmer ekspert"
        else return "Nepoznato"
    }

    /**
     * Otvara novi activity za biranje slike iz galerije
     *
     * @param fragment Fragment u kojem se poziva
     * @param requestCode request code koji se koristi pri vraćanju rezultata
     */
    fun pickGalleryImage(fragment: Fragment, requestCode: Int) {
        val intent =
            Intent(Intent.ACTION_PICK)
        //intent.setType("image/*")
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        fragment.startActivityForResult(intent, requestCode)
    }

    /**
     * Vraća broj odgovora koji je potreban do sledećeg ranka za korisnikov trenutni rank
     *
     * @param brojNajboljihOdgovora trenutni broj najboljih odgovora korisnika
     * @return maksimalan broj odgovora trenutnog korisnikovog ranka nakon koga slijedi sledeći rank
     */
    private fun getRankMaxPoints(brojNajboljihOdgovora: Int): Int {
        return brojNajboljihOdgovora.run {
            var toNextRank = 0
            if(this < 5) {  toNextRank = RANKOVI.FARMER_POCETNIK.odgovora }
            else if(this < 10 && this >= 5) {  toNextRank = RANKOVI.FARMER_AMATER.odgovora }
            else if(this < 15 && this >= 10) {  toNextRank = RANKOVI.NAPREDNI_FARMER.odgovora }
            else if(this >= 15) {  toNextRank = brojNajboljihOdgovora }

            toNextRank
        }
    }

    /**
     * Na osnovu pozicije vraća odgovarajuću ikonu za dropdrown
     *
     * @param pos pozicija u listi (adapter pozicija)
     * @param res resursi
     *
     * @return Vraća odgovarajuću ikonu
     */
    fun getDrawableBasedOnId(pos: Int, res: Resources): Drawable? {
        var drawable: Drawable? = null
        when(pos) {

            Constants.PROIZVOD_PLANT_TYPE.TYPE_KUKURUZ.ordinal -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.corn_icon, null)
            }
            Constants.PROIZVOD_PLANT_TYPE.TYPE_KROMPIR.ordinal -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.potato_icon, null)
            }
            Constants.PROIZVOD_PLANT_TYPE.TYPE_JABUKA.ordinal -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.apple_icon, null)
            }
            Constants.PROIZVOD_PLANT_TYPE.TYPE_KRASTAVAC.ordinal -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.krastavac_icon, null)
            }
            Constants.PROIZVOD_PLANT_TYPE.TYPE_PSENICA.ordinal -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.psenica_icon, null)
            }
            Constants.PROIZVOD_PLANT_TYPE.TYPE_SLJIVA.ordinal -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.sljiva_icon, null)
            }

        }
        return drawable
    }

    /**
     * Na osnovu imena vraća odgovarajuću ikonu
     *
     * @param name ime proizvoda
     * @param res resursi
     *
     * @return Vraća odgovarajuću ikonu
     */
    fun getDrawableBasedOnName(name: String, res: Resources): Drawable? {
        var drawable: Drawable? = null
        when(name) {
            "Kukuruz" -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.corn_icon, null)
            }
            "Pšenica" -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.psenica_icon, null)
            }
            "Krompir" -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.potato_icon, null)
            }
            "Krastavac" -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.krastavac_icon, null)
            }
            "Jabuka" -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.apple_icon, null)
            }
            "Sljiva" -> {
                drawable = ResourcesCompat.getDrawable(res, R.drawable.sljiva_icon, null)
            }
        }
        return drawable
    }

    /**
     * Izračunava procenat od ukupnog broja najboljih odgovora
     *
     * @param brojNajboljihOdgovora Korisnikov trenutni broj najboljih odgovora
     * @return Vraća procenat koji je korisnik ostvario
     */
    fun getPercentageToNextRank(brojNajboljihOdgovora: Int): Int {
        val toNextLvl = getRankMaxPoints(brojNajboljihOdgovora)

        return ((brojNajboljihOdgovora.toDouble() / toNextLvl) * 100).toInt()
    }

    /**
     * Provjerava da li tekst sadrži specijalne karaktere u sebi
     *
     * @see com.electroniccode.leafy.util.Constants.specialChars
     * @param string tekst koji treba provjeriti
     * @return Vraća boolean koji određuje da li string sadrži ili ne sadrži specijalne karaktere
     */
    fun containsSpecialChars(string: String): Boolean {
        val pattern = Pattern.compile(Constants.specialChars)
        val matcher = pattern.matcher(string)
        return matcher.find()
    }

    /**
     * Kompresuje sliku pomoću coroutinsa i stavlja u File
     *
     * @param context context
     * @param imageUri Uri slike
     * @return Vraća Deferred tipa File
     */
    suspend fun compressImageToFile(context: Context, imageUri: Uri): Deferred<File> {

        val tempFile = File.createTempFile("pref", "suf")
        tempFile.deleteOnExit()

        val inputStream = context.contentResolver.openInputStream(imageUri)

        return withContext(Dispatchers.IO) {
            async {

                val outputStream = FileOutputStream(tempFile)
                IOUtils.copy(inputStream, outputStream)
                return@async Compressor.compress(context, tempFile)

            }
        }

    }

}