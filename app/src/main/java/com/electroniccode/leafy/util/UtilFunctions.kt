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


    fun parseBolestName(bolestName: String): String =
        when(bolestName) {
            "hrdja" -> "Rđa"
            "sive_pjege" -> "Sive pjege"
            "zdrav" -> "Zdrav"
            "rana_plamenjaca" -> "Rana plamenjača"
            "kasna_plamenjaca" -> "Kasna plamenjača"
            else -> "Nepoznato"

        }

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

    fun pickGalleryImage(fragment: Fragment, requestCode: Int) {
        val intent =
            Intent(Intent.ACTION_PICK)
        //intent.setType("image/*")
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        fragment.startActivityForResult(intent, requestCode)
    }

    private fun getRankMaxPoints(brojNajboljihOdgovora: Int): Int {
        return brojNajboljihOdgovora.run {
            var toNextRank = 0
            if(this < 5) {  toNextRank = RANKOVI.FARMER_POCETNIK.odgovora }
            else if(this < 10 && this >= 5) {  toNextRank = RANKOVI.FARMER_AMATER.odgovora }
            else if(this < 15 && this >= 10) {  toNextRank = RANKOVI.NAPREDNI_FARMER.odgovora }

            toNextRank
        }
    }

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

    fun getPercentageToNextRank(brojNajboljihOdgovora: Int): Int {
        val toNextLvl = getRankMaxPoints(brojNajboljihOdgovora)

        return ((brojNajboljihOdgovora.toDouble() / toNextLvl) * 100).toInt()
    }

    fun containsSpecialChars(string: String): Boolean {
        val pattern = Pattern.compile(Constants.specialChars)
        val matcher = pattern.matcher(string)
        return matcher.find()
    }

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