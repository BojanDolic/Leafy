package com.electroniccode.leafy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.fragments.BookViewerFragmentDirections

class MainActivity : AppCompatActivity() {

    interface OnBackPressedListener {
        fun onBackPressedInFragment()
    }

    protected var backListener: OnBackPressedListener? = null

    public fun setOnBackPressedListener(listener: OnBackPressedListener?) {
        this.backListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment?.findNavController()

    }

    override fun onBackPressed() {
        if(backListener != null) {
            backListener?.onBackPressedInFragment()
        } else super.onBackPressed()
    }

}