package com.electroniccode.leafy.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.electroniccode.leafy.models.User
import com.electroniccode.leafy.util.UtilFunctions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class ProfileSettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val database by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    val context = getApplication<Application>().applicationContext

    var isUpdating: MutableLiveData<Boolean> = MutableLiveData(false)

    var user: User? = null

    var profileImageUri: Uri? = null
    var username: String = ""


    suspend fun getUserFromDatabase(userId: String): User? {

        val result = database.collection("korisnici").document(userId).get().await()
        return result?.toObject(User::class.java)

    }

    fun updateUserProfileName(username: String, uid: String) {
        isUpdating.value = true
        database.collection("korisnici").document(uid).update("korisnickoIme", username).addOnCompleteListener {
            isUpdating.value = false
        }
    }


    fun updateUserProfilePic(imageUri: Uri, uid: String) {
        viewModelScope.launch {

            isUpdating.value = true

            val compressedImageFile = UtilFunctions.compressImageToFile(context, imageUri).await()
            val finalImageUri = Uri.fromFile(compressedImageFile)

            val storageRef = storage.reference.child("slikeKorisnika/${uid}")

            val result = storageRef.putFile(finalImageUri).await()
            if(result.task.isSuccessful) {
                val url = storageRef.downloadUrl.await().toString()
                database.collection("korisnici").document(uid).update("slikaKorisnika", url).await()
                isUpdating.value = false

            } else isUpdating.value = false

        }
    }


}