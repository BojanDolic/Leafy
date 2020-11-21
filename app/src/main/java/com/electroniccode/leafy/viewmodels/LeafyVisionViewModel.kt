package com.electroniccode.leafy.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.electroniccode.leafy.models.Bolest
import com.electroniccode.leafy.util.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions
import java.io.IOException

class LeafyVisionViewModel(app: Application) : AndroidViewModel(app) {

    var imageUri: Uri? = null
    var bolest: Bolest? = null

    val context = app.applicationContext

    private var kukuruzModel: AutoMLImageLabelerLocalModel
    private var krompirModel: AutoMLImageLabelerLocalModel

    private var kukuruzLabeler: ImageLabeler
    private var krompirLabeler: ImageLabeler

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    init {
        kukuruzModel = AutoMLImageLabelerLocalModel.Builder()
            .setAssetFilePath("kukuruzModel/manifest.json")
            .build()

        krompirModel = AutoMLImageLabelerLocalModel.Builder()
            .setAssetFilePath("krompirModel/manifest.json")
            .build()

        val kukuruzLabelerOptions = AutoMLImageLabelerOptions.Builder(kukuruzModel)
            .setConfidenceThreshold(0.5f)
            .build()

        val krompirLabelerOptions = AutoMLImageLabelerOptions.Builder(krompirModel)
            .setConfidenceThreshold(0.5f)
            .build()

        kukuruzLabeler = ImageLabeling.getClient(kukuruzLabelerOptions)
        krompirLabeler = ImageLabeling.getClient(krompirLabelerOptions)

    }

    fun getBolestInfoFromDatabase(imeBolesti: String): Task<DocumentSnapshot> = db.collection("bolesti").document(imeBolesti).get()

    fun labelImageUsingML(imageUri: Uri, type: Int): Task<List<ImageLabel>> {
        val image: InputImage
        image = InputImage.fromFilePath(context, imageUri)

        if(type == Constants.PLANT_TYPE_CORN)
            return kukuruzLabeler.process(image)
        else return krompirLabeler.process(image)
    }


}