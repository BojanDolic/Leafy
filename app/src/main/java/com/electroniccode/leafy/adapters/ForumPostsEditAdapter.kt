package com.electroniccode.leafy.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.ForumCardBinding
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.models.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat


class ForumPostsEditAdapter(options: FirestorePagingOptions<Pitanje>) : FirestorePagingAdapter<Pitanje, ForumPostsEditAdapter.PostsViewHolder>(options) {

    public interface OnForumEditPostCardClick {
        fun onForumPostLongClicked(dokument: DocumentSnapshot?)
    }

    private lateinit var listener: OnForumEditPostCardClick

    fun setOnForumEditPostCardClickedListener(listener: OnForumEditPostCardClick) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val cardBinding = ForumCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(cardBinding)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int, model: Pitanje) {
        holder.bindData(model)
    }

    inner class PostsViewHolder(private val cardBinding: ForumCardBinding) : RecyclerView.ViewHolder(cardBinding.root) {

        fun bindData(pitanje: Pitanje) = with(cardBinding) {

            getItem(adapterPosition)

            forumCardTitle.text = pitanje.tekstPitanja

            val datum = pitanje.datum?.toDate()
            val formatiranDatum = DateFormat.getDateInstance().format(datum!!)

            forumCardDate.text = formatiranDatum

            if(TextUtils.equals(pitanje.slikaPitanja, "")) {
                Glide.with(forumCardSlika.context)
                    .load(R.drawable.no_image_placeholder)
                    .into(forumCardSlika)
            } else {
                forumCardSlika.imageTintList = null
                Glide.with(forumCardSlika.context)
                    .load(pitanje.slikaPitanja)
                    .into(forumCardSlika)
            }

            FirebaseFirestore.getInstance().document("korisnici/${pitanje.idAutora}")
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {

                        val user = it.result?.toObject(User::class.java)

                        user?.let {
                            forumCardUsername.text = user.korisnickoIme
                            Glide.with(forumCardSlika.context)
                                .load(user.slikaKorisnika)
                                .transform(CircleCrop())
                                .error(R.drawable.no_profile_pic_placeholder)
                                .into(forumCardUsernamePhoto)
                        } ?: run {
                            forumCardUsername.text = "Nepoznato"
                        }
                    } else {
                        forumCardUsername.text = "Nepoznato"
                    }


                }

            root.setOnLongClickListener {
                listener.onForumPostLongClicked(getItem(adapterPosition))
                true
            }

        }

    }

}