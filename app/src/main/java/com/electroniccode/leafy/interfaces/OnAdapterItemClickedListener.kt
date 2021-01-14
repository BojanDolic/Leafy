package com.electroniccode.leafy.interfaces

import com.google.firebase.firestore.DocumentSnapshot

interface OnAdapterItemClickedListener {
    fun onItemLongClicked(dokument: DocumentSnapshot?)
}