package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.LeafyBuyCardElementBinding
import com.electroniccode.leafy.models.Proizvod

class LeafyBuyProizvodiAdapter(val proizvodi: List<Proizvod?>) : RecyclerView.Adapter<LeafyBuyProizvodiAdapter.ItemHolder>() {

    interface OnProizvodClickListener {
        fun onProizvodClicked(proizvod: Proizvod?)
    }

    private lateinit var listener: OnProizvodClickListener

    fun setProizvodClickListener(listener: OnProizvodClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LeafyBuyCardElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.assignValues(proizvodi[position])
    }

    override fun getItemCount(): Int = proizvodi.size


    inner class ItemHolder(val binding: LeafyBuyCardElementBinding) : RecyclerView.ViewHolder(binding.root) {

        fun assignValues(proizvod: Proizvod?) {
            binding.apply {

                Glide.with(binding.buyCardCijenaText.context)
                    .load(proizvod?.slikaProizvoda)
                    .into(binding.buyCardImage)

                buyCardTitle.text = proizvod?.naslovProizvoda

                buyCardUdaljenostText.text = "%.2f km".format(proizvod?.udaljenost)

                buyCardCijenaText.text = String.format(
                    binding.buyCardCijenaText.context.resources.getString(
                        R.string.evro_placeholder_text),
                    proizvod?.cijenaProizvoda)

                binding.root.setOnClickListener {
                    listener.onProizvodClicked(proizvod)
                }

            }
        }

    }

}