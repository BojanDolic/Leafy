package com.electroniccode.leafy.viewmodels

import android.net.Uri
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class CreatePitanjeViewModel : ViewModel() {

    var slikaUri: Uri? = null

    var obradaPitanja: MutableLiveData<Boolean> = MutableLiveData(false)

    var countDownTimer: CountDownTimer? = null


}