package com.electroniccode.leafy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.electroniccode.leafy.databinding.ActivityPhotoViewerBinding

class PhotoViewerActivity : AppCompatActivity() {

    val args: PhotoViewerActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPhotoViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = args.photoUrl

        Glide.with(applicationContext)
            .load(url)
            .into(binding.photoviewerImageview)

    }
}