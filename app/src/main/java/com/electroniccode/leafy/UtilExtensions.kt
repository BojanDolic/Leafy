package com.electroniccode.leafy

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.electroniccode.leafy.models.Preparat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View
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

fun Fragment.showErrorSnackbar(view: View, errorText: String, trajanje: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, errorText, trajanje).apply {
        setBackgroundTint(resources.getColor(R.color.errorRed, null))
    }.show()
}

suspend inline fun <reified T> FirebaseFirestore.getDocuments(ids: List<String>, collection: String): List<T?> {
    val lista = mutableListOf<T?>()

    val job = GlobalScope.async {
        for (id in ids) {
            val objekat = document("$collection/$id").get().await().toObject(T::class.java)
            lista.add(objekat)
        }
    }
    job.await()
    return lista
}

