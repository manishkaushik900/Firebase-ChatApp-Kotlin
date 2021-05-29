package com.demo.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.chatapp.R
import com.demo.chatapp.data.model.User
import com.demo.chatapp.databinding.ItemPersonBinding
import com.demo.chatapp.glide.GlideApp
import com.demo.chatapp.utils.StorageUtil
import com.google.firebase.firestore.DocumentSnapshot

class UsersAdapter(private val listener: UserItemListener) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val dataset = mutableListOf<DocumentSnapshot>()

    interface UserItemListener {
        fun onClickedUser(id: String, user: User)
    }

    fun setItems(items: List<DocumentSnapshot>) {
        this.dataset.clear()
        this.dataset.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataset.get(position))
    }

    override fun getItemCount(): Int = dataset.size


    class ViewHolder(
        private val itemBinding: ItemPersonBinding,
        private val listener: UserItemListener
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private lateinit var user: User
        private lateinit var id:String

        init {
            itemBinding.root.setOnClickListener(this)
        }

        fun bind(users: DocumentSnapshot) {

            this.id = users.id
            this.user = users.toObject(User::class.java)!!

            if (user.profilePicturePath != null)
                GlideApp.with(itemBinding.root.context)
                    .load(StorageUtil.pathToReference(user.profilePicturePath ?: ""))
                    .placeholder(R.drawable.baseline_account_circle_purple_600_24dp)
                    .into(itemBinding.imageViewProfilePicture)

            itemBinding.textViewName.text = user.name
            itemBinding.textViewBio.text = user.bio

            itemBinding.cardViewItemPerson.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onClickedUser(id,user)
        }


    }


}