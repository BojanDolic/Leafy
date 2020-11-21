package com.electroniccode.leafy.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CreatePitanjeViewModel : ViewModel() {

    var slikaUri: Uri? = null

    var obradaPitanja: MutableLiveData<Boolean> = MutableLiveData(false)

}