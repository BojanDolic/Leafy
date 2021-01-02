package com.electroniccode.leafy.adapters

import android.media.Image
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.electroniccode.leafy.R
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.models.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat

class ForumPitanjaAdapter(
    options: FirestorePagingOptions<Pitanje>,
    progress: CircularProgressIndicator,
    val database: FirebaseFirestore) : FirestorePagingAdapter<Pitanje, ForumPitanjaAdapter.PitanjeViewHolder>(options) {


    val progressIndicator = progress


    interface OnPitanjeClickListener {
        fun onPitanjeClicked(snapshot: DocumentSnapshot?)
    }

    lateinit var pitanjeClickInterface: OnPitanjeClickListener

    fun setOnPitanjeClickListener(listener: OnPitanjeClickListener) {
        pitanjeClickInterface = listener
    }


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


        database.document("korisnici/${model.idAutora}")
            .get().addOnCompleteListener {

                if (it.isSuccessful) {

                    val user = it.result?.toObject(User::class.java)

                    user?.let {
                        holder.imeAutora.text = user.korisnickoIme
                        Glide.with(holder.itemView.context)
                            .load(user.slikaKorisnika)
                            .transform(CircleCrop())
                            .error(R.drawable.no_profile_pic_placeholder)
                            .into(holder.slikaAutora)
                    } ?: run {
                        holder.imeAutora.text = "Nepoznato"
                    }
                } else {
                    holder.imeAutora.text = "Nepoznato"
                }


            }


    }


    override fun onLoadingStateChanged(state: LoadingState) {
        super.onLoadingStateChanged(state)

        when(state) {

            LoadingState.LOADING_INITIAL -> {
                progressIndicator.show()
            }
            LoadingState.FINISHED, LoadingState.ERROR -> {
                progressIndicator.hide()
                progressIndicator.setVisibilityAfterHide(View.GONE)
            }



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
        var imeAutora: TextView
        var slikaAutora: ImageView

        init {

            slikaPitanja = v.findViewById(R.id.forum_card_slika)
            pitanje = v.findViewById(R.id.forum_card_title)
            datum = v.findViewById(R.id.forum_card_date)
            imeAutora = v.findViewById(R.id.forum_card_username)
            slikaAutora = v.findViewById(R.id.forum_card_username_photo)

            v.setOnClickListener {
                pitanjeClickInterface.onPitanjeClicked(getItem(adapterPosition))
            }

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