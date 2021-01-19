package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.databinding.BolestCardItemBinding
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.models.Bolest
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class BolestiRecyclerAdapter(options: FirestorePagingOptions<Bolest>) :
    FirestorePagingAdapter<Bolest, BolestiRecyclerAdapter.ViewHolder>(options) {

    private lateinit var listener: OnAdapterItemClickedListener

    public fun setOnClickListener(listener: OnAdapterItemClickedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BolestCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Bolest) {
        holder.bindView(model)
    }

    inner class ViewHolder(val binding: BolestCardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(bolest: Bolest) {
            with(binding) {

                bolestCardTitle.text = bolest.imeBolesti
                bolestCardDesc.text = bolest.opis[0]

                Glide.with(root.context)
                    .load(bolest.slikaBolesti)
                    .into(bolestCardImage)

                root.setOnClickListener {
                    listener.onItemClicked(bolest)
                }

            }

        }

    }

}