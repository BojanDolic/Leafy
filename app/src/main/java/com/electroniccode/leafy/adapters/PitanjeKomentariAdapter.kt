package com.electroniccode.leafy.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.electroniccode.leafy.models.Komentar
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.electroniccode.leafy.R
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.models.User
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PitanjeKomentariAdapter(
    options: FirestoreRecyclerOptions<Komentar>,
    val database: FirebaseFirestore,
    val progressIndicator: CircularProgressIndicator,
    val recycler: RecyclerView,
    val noComment: LinearLayout,
    val komentariCount: TextView,
    val pitanje: Pitanje
) : FirestoreRecyclerAdapter<Komentar, PitanjeKomentariAdapter.KomentarViewHolder>(options) {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    lateinit var listener: OnKomentarMenuItemClicked

    interface OnKomentarMenuItemClicked {
        fun onKomentarDelete(komentarID: String)
        fun onKomentarMarkedBest(komentarID: String)
    }

    fun setOnKomentarMenuClickListener(listener: OnKomentarMenuItemClicked) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.forum_komentar_card, parent, false)
        return KomentarViewHolder(v)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int, model: Komentar) {

        if (model.najboljiKomentar)
            holder.najboljiCommentContainer.visibility = View.VISIBLE
        else holder.najboljiCommentContainer.visibility = View.GONE

        holder.tekstKomentara.text = model.tekstKomentara
        holder.imeAutora.text = model.imeAutora

        database.document("korisnici/${model.idAutora}")
            .get().addOnCompleteListener {

                if (it.isSuccessful) {

                    val user = it.result?.toObject(User::class.java)

                    holder.imeAutora.text = user?.korisnickoIme

                    Glide.with(holder.itemView.context)
                        .load(user?.slikaKorisnika)
                        .transform(CircleCrop())
                        .error(R.drawable.no_profile_pic_placeholder)
                        .into(holder.slikaAutora)
                } else {
                    holder.imeAutora.text = "Nepoznato"
                }


            }


        // Ako je autor komentara, ima mogućnost brisanja svog komentara
        // Ako je autor pitanja, ima mogućnost brisanja svog komentara, a ostale može označiti kao najbolje osim već označenog


        auth.currentUser?.let {

            if (it.uid == pitanje.idAutora) {

                if (it.uid == model.idAutora) {

                    holder.optionsBtn.visibility = View.VISIBLE

                    val popup = PopupMenu(holder.itemView.context, holder.optionsBtn)
                    val inflater = popup.menuInflater
                    inflater.inflate(R.menu.pitanje_komentar_menu, popup.menu)

                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {

                            R.id.obrisi_komentar_popup_btn -> {
                                listener.onKomentarDelete(model.idKomentara)
                                true
                            }
                            else -> false
                        }
                    }

                    holder.optionsBtn.setOnClickListener {
                        popup.show()
                    }


                } else if (it.uid != model.idAutora && !model.najboljiKomentar) {

                    holder.optionsBtn.visibility = View.VISIBLE

                    val popup = PopupMenu(holder.itemView.context, holder.optionsBtn)
                    val inflater = popup.menuInflater
                    inflater.inflate(R.menu.pitanje_komentar_menu_autor, popup.menu)


                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {

                            R.id.pitanje_komentar_menu_select_best_btn -> {
                                listener.onKomentarMarkedBest(model.idKomentara)
                                true
                            }
                            else -> false
                        }
                    }

                    holder.optionsBtn.setOnClickListener {
                        popup.show()
                    }

                } else holder.optionsBtn.visibility = View.GONE


            } else {

                if (it.uid == model.idAutora) {

                    holder.optionsBtn.visibility = View.VISIBLE

                    val popup = PopupMenu(holder.itemView.context, holder.optionsBtn)
                    val inflater = popup.menuInflater
                    inflater.inflate(R.menu.pitanje_komentar_menu, popup.menu)

                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {

                            R.id.obrisi_komentar_popup_btn -> {
                                listener.onKomentarDelete(model.idKomentara)
                                true
                            }
                            else -> false
                        }
                    }

                    holder.optionsBtn.setOnClickListener {
                        popup.show()
                    }


                } else holder.optionsBtn.visibility = View.GONE


            }

        }


    }


    override fun onDataChanged() {
        super.onDataChanged()
        if (snapshots.isEmpty()) {
            progressIndicator.setVisibilityAfterHide(View.GONE)
            progressIndicator.hide()
            recycler.visibility = View.GONE

            noComment.visibility = View.VISIBLE
        } else if (snapshots.isNotEmpty()) {
            noComment.visibility = View.GONE
            progressIndicator.setVisibilityAfterHide(View.GONE)
            progressIndicator.hide()
            recycler.visibility = View.VISIBLE
        }

        komentariCount.text = "Odgovori ($itemCount)"


    }


    inner class KomentarViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var slikaAutora: ImageView
        var imeAutora: TextView
        var najboljiCommentContainer: CardView
        var tekstKomentara: TextView
        var optionsBtn: ImageButton


        init {

            slikaAutora = v.findViewById(R.id.komentar_slika_autora)
            imeAutora = v.findViewById(R.id.komentar_ime_autora)
            najboljiCommentContainer = v.findViewById(R.id.komentar_najbolji_komentar_container)
            tekstKomentara = v.findViewById(R.id.komentar_tekst_komentara)
            optionsBtn = v.findViewById(R.id.komentar_options_btn)

        }


    }


}