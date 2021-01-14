package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.electroniccode.leafy.databinding.LeafyBuyCardElementBinding
import com.electroniccode.leafy.models.Proizvod
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import android.view.View
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.interfaces.OnDataChangedListener

class ProizvodiPostsEditAdapter(options: FirestoreRecyclerOptions<Proizvod>)
    : FirestoreRecyclerAdapter<Proizvod, ProizvodiPostsEditAdapter.ViewHolder>(options) {

    lateinit var dataListener: OnDataChangedListener
    lateinit var clickListener: OnAdapterItemClickedListener

    public fun setOnDataChangedListener(dataListener: OnDataChangedListener) {
        this.dataListener = dataListener
    }

    public fun setOnItemClickedListener(clickListener: OnAdapterItemClickedListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LeafyBuyCardElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Proizvod) {
        holder.bindView(model)
    }


    override fun onDataChanged() {
        if(itemCount == 0) {
            dataListener.showNoDataPlaceHolder()
        } else {
            dataListener.hideNoDataPlaceHolder()
        }
    }

    inner class ViewHolder(val binding: LeafyBuyCardElementBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(proizvod: Proizvod) {
            binding.apply {

                buyCardTitle.text = proizvod.naslovProizvoda
                buyCardCijenaText.text = String.format(
                    binding.buyCardCijenaText.context.resources.getString(
                        R.string.evro_placeholder_text),
                    proizvod.cijenaProizvoda)
                buyCardUdaljenostText.visibility = View.GONE

                Glide.with(buyCardImage.context)
                    .load(proizvod.slikaProizvoda)
                    .into(buyCardImage)

                root.setOnLongClickListener {
                    clickListener.onItemLongClicked(snapshots.getSnapshot(adapterPosition))
                    true
                }

            }
        }
    }

}