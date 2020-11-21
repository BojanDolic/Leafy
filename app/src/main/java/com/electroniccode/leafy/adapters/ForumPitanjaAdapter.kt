package com.electroniccode.leafy.adapters

import android.media.Image
import android.text.Layout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.models.Pitanje
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.type.Date
import org.w3c.dom.Text
import java.text.DateFormat
import java.time.format.DateTimeFormatter

class ForumPitanjaAdapter(options: FirestorePagingOptions<Pitanje>) : FirestorePagingAdapter<Pitanje, ForumPitanjaAdapter.PitanjeViewHolder>(options) {


    override fun onBindViewHolder(holder: PitanjeViewHolder, position: Int, model: Pitanje) {

        val datum = model.datum?.toDate()

        val formatiranDatum = DateFormat.getDateInstance().format(datum!!)

        holder.pitanje.text = model.tekstPitanja
        holder.datum.text = formatiranDatum

        if(TextUtils.equals(model.slikaPitanja, "")) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.no_image_placeholder)
                .into(holder.slikaPitanja)
        } else {
            holder.slikaPitanja.imageTintList = null
            Glide.with(holder.itemView.context)
                .load(model.slikaPitanja)
                .into(holder.slikaPitanja)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PitanjeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_card, parent, false)
        return PitanjeViewHolder(view)
    }

    /*override fun onBindViewHolder(holder: PitanjeViewHolder, position: Int) {

        val pitanje = pitanja.get(position)

        val datum = pitanje.datum?.toDate()

        val formatiranDatum = DateFormat.getDateInstance().format(datum!!)

        holder.pitanje.text = pitanje.tekstPitanja
        holder.datum.text = formatiranDatum

        Glide.with(holder.itemView.context)
            .load(pitanje.slikaPitanja)
            .into(holder.slikaPitanja)



    }*/

    /*override fun getItemCount(): Int {
        return pitanja.size
    }*/

    inner class PitanjeViewHolder constructor(v: View) : RecyclerView.ViewHolder(v) {

        var slikaPitanja: ImageView
        var pitanje: TextView
        var datum: TextView

        init {

            slikaPitanja = v.findViewById(R.id.forum_card_slika)
            pitanje = v.findViewById(R.id.forum_card_title)
            datum = v.findViewById(R.id.forum_card_date)

        }


    }

    /*fun updatePitanjaList(newPitanjaList: List<Pitanje>) {

        val staraPitanja = pitanja
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            DiffUtilCallback(
                staraPitanja,
                newPitanjaList
            )
        )
        pitanja = newPitanjaList
        diffResult.dispatchUpdatesTo(this)

    }

    inner class DiffUtilCallback(
        var oldList: List<Pitanje>,
        var newList: List<Pitanje>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.get(oldItemPosition) === newList.get(newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition))
        }
    }*/


}