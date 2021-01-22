package com.electroniccode.leafy.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
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
        database.collection("korisnici").document(uid).update("korisnickoIme", username)
            .addOnCompleteListener {
                isUpdating.value = false
            }
    }

    /**
     * Funkcija za upload izabrane slike na firebase storage
     *
     * @param imageUri Uri slike koja je izabrana u galeriji
     * @param uid UID korisnika, ujedno i ime slike u storage-u
     */
    fun updateUserProfilePic(imageUri: Uri, uid: String) {
        viewModelScope.launch {

            isUpdating.value = true

            val compressedImageFile = UtilFunctions.compressImageToFile(context, imageUri).await()
            val finalImageUri = Uri.fromFile(compressedImageFile)

            val storageRef = storage.reference.child("slikeKorisnika/${uid}")

            val task = storageRef.putFile(finalImageUri).await()

            Log.d("TAG", "updateUserProfilePic: ${task.error}")

            val url = storageRef.downloadUrl.await()
            database.collection("korisnici").document(uid).update("slikaKorisnika", url.toString()).await()
            isUpdating.value = false


        }
    }


}