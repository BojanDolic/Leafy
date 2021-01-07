package com.electroniccode.leafy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.electroniccode.leafy.R
import com.electroniccode.leafy.util.UtilFunctions

class ArrayAdapterWithIcon constructor(context: Context, resource: Int, val items: Array<String>): ArrayAdapter<String>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.spinner_row, parent, false)

        inflatedView.findViewById<TextView>(R.id.spinner_title).apply {
            text = items[position]
        }

        val image = inflatedView.findViewById<ImageView>(R.id.spinner_icon)
        val drawable = UtilFunctions.getDrawableBasedOnId(position, context.resources)

        drawable?.let {
            image.setImageDrawable(drawable)
        }

        return inflatedView
    }

}