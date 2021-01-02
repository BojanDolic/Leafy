package com.electroniccode.leafy.util

import android.content.Context
import android.net.Uri
import android.util.Log
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

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

    private fun getRankMaxPoints(brojNajboljihOdgovora: Int): Int {
        return brojNajboljihOdgovora.run {
            var toNextRank = 0
            if(this < 5) {  toNextRank = RANKOVI.FARMER_POCETNIK.odgovora }
            else if(this < 10 && this >= 5) {  toNextRank = RANKOVI.FARMER_AMATER.odgovora }
            else if(this < 15 && this >= 10) {  toNextRank = RANKOVI.NAPREDNI_FARMER.odgovora }

            toNextRank
        }
    }

    fun getPercentageToNextRank(brojNajboljihOdgovora: Int): Int {
        val toNextLvl = getRankMaxPoints(brojNajboljihOdgovora)

        return ((brojNajboljihOdgovora.toDouble() / toNextLvl) * 100).toInt()
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