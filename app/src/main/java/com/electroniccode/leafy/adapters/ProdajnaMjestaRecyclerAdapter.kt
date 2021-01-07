package com.electroniccode.leafy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.electroniccode.leafy.databinding.ProdajnoMjestoItemBinding
import com.electroniccode.leafy.models.Mjesto
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.android.material.card.MaterialCardView
import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

class ProdajnaMjestaRecyclerAdapter(
    options: FirestorePagingOptions<Mjesto>,
    val noItemsCard: MaterialCardView,
    val shimmerLoader: ShimmerFrameLayout) : FirestorePagingAdapter<Mjesto, ProdajnaMjestaRecyclerAdapter.MjestoViewHolder>(options) {

    public interface OnProdajnoMjestoClickListener {
        fun onProdajnoMjestoClicked(doc: DocumentSnapshot?)
    }

    private lateinit var listener: OnProdajnoMjestoClickListener

    public fun setOnProdajnoMjestoClickListener(listener: OnProdajnoMjestoClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MjestoViewHolder {
        val inflatedView = ProdajnoMjestoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MjestoViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: MjestoViewHolder, position: Int, model: Mjesto) {
        holder.bindDataToViews(model)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when(state) {

            LoadingState.LOADING_INITIAL -> {
                shimmerLoader.startShimmer()
            }

            LoadingState.FINISHED -> {
                shimmerLoader.stopShimmer()
                shimmerLoader.visibility = View.GONE
                if(itemCount == 0)
                    noItemsCard.visibility = View.VISIBLE
                else noItemsCard.visibility = View.GONE
            }
            else -> noItemsCard.visibility = View.GONE
        }
    }

    inner class MjestoViewHolder(val binding: ProdajnoMjestoItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindDataToViews(mjesto: Mjesto) {

            binding.prodajnoMjestoItemTitle.text = mjesto.imeMjesta

            val latFormated = String.format("Lat: %.6f, Lng: %.6f", mjesto.lat, mjesto.lng)
            binding.prodajnoMjestoItemCoords.text = latFormated

            binding.prodajnoMjestoItemCard.setOnClickListener {
                listener.onProdajnoMjestoClicked(getItem(adapterPosition))
            }

        }

    }
}