package com.electroniccode.leafy

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.electroniccode.leafy.models.Preparat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

fun hideKeyboard(activity: Activity) {

    var view = activity.currentFocus

    view?.let {
        val inputMethod =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

suspend fun FirebaseFirestore.getDocuments(ids: List<String>, collection: String): List<Preparat?> {
    val lista = mutableListOf<Preparat?>()

    val job = GlobalScope.async {
        for (id in ids) {
            val preparat = document("$collection/$id").get().await().toObject(Preparat::class.java)
            lista.add(preparat)
        }
    }
    job.await()
    return lista
}

