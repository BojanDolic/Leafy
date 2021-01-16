package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.PreparatRecyclerviewElementBinding
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.models.Preparat

class LeafyBookPreparatAdapter(val preparati: List<Preparat?>) :
    RecyclerView.Adapter<LeafyBookPreparatAdapter.PreparatViewHolder>() {

    private lateinit var listener: OnAdapterItemClickedListener

    public fun setOnClickListener(listener: OnAdapterItemClickedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparatViewHolder {
        val binding = PreparatRecyclerviewElementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PreparatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreparatViewHolder, position: Int) {
        val preparat = preparati.get(position)
        holder.bindView(preparat)



        holder.itemView.setOnClickListener {
            listener.onItemClicked(preparat)
        }

    }

    override fun getItemCount(): Int {
        return preparati.size
    }

    inner class PreparatViewHolder(val binding: PreparatRecyclerviewElementBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(preparat: Preparat?) {
            with(binding) {
                leafyBookNazivPreparata.text = preparat?.imePreparata
                leafyBookVrstaPreparata.text = preparat?.tipPreparata

                if(preparat?.slika?.isNotEmpty()!!)
                    Glide.with(root.context)
                        .load(preparat.slika)
                        .into(leafyBookSlikaPreparata)

                preparatCard.setOnClickListener {
                    listener.onItemClicked(preparat)
                }
            }


        }

    }

}