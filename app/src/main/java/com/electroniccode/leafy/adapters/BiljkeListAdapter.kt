package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.databinding.BiljkeItemCardBinding
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.models.Biljka
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class BiljkeListAdapter(options: FirestorePagingOptions<Biljka>)
    : FirestorePagingAdapter<Biljka, BiljkeListAdapter.ViewHolder>(options)  {

    private lateinit var listener: OnAdapterItemClickedListener

    fun setOnClickListener(listener: OnAdapterItemClickedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BiljkeItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Biljka) {
        holder.bindView(model)
    }

    inner class ViewHolder(val binding: BiljkeItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(biljka: Biljka) {
            with(binding) {

                Glide.with(biljkeCardImage.context)
                    .load(biljka.slikaBiljke)
                    .into(biljkeCardImage)

                biljkeNaziv.text = biljka.imeBiljke

                root.setOnClickListener {
                    listener.onItemClicked(biljka)
                }

            }
        }
    }
}