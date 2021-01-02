package com.electroniccode.leafy

import android.app.Activity
import android.widget.ImageView
import android.view.View
import android.view.inputmethod.InputMethodManager

fun ImageView.toggleFullScreen() {

    val uiOptions = systemUiVisibility
    var newOptions = uiOptions

    val isImmersiveModeEnabled: Boolean = ((uiOptions or ImageView.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions)

    newOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

    systemUiVisibility = newOptions

}
