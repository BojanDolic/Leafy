package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.models.Preparat

class LeafyBookPreparatAdapter(val preparati: List<Preparat?>) : RecyclerView.Adapter<LeafyBookPreparatAdapter.PreparatViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.preparat_recyclerview_element, parent, false)
        return PreparatViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreparatViewHolder, position: Int) {

        val preparat = preparati.get(position)

        holder.nazivPreparata.text = preparat?.imePreparata



      Glide.with(holder.itemView.context)
            .load(preparat?.slika)
            .into(holder.slikaPreparata)

    }

    override fun getItemCount(): Int {
        return preparati.size
    }

    inner class PreparatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var slikaPreparata: ImageView
        var nazivPreparata: TextView

        init {

            slikaPreparata = itemView.findViewById(R.id.leafy_book_slika_preparata)
            nazivPreparata = itemView.findViewById(R.id.leafy_book_naziv_preparata)

        }


    }

}