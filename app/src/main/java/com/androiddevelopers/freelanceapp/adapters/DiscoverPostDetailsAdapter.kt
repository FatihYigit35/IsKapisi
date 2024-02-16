package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.RowDiscoverDetailsBinding
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.view.discover.DiscoverDetailsFragmentDirections
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class DiscoverPostDetailsAdapter : RecyclerView.Adapter<DiscoverPostDetailsAdapter.DiscoverPostDetailsViewHolder>() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val diffUtil = object : DiffUtil.ItemCallback<DiscoverPostModel>() {
        override fun areItemsTheSame(oldItem: DiscoverPostModel, newItem: DiscoverPostModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DiscoverPostModel, newItem: DiscoverPostModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var postList: List<DiscoverPostModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class DiscoverPostDetailsViewHolder(val binding: RowDiscoverDetailsBinding) : RecyclerView.ViewHolder(binding.root){
        fun likePost(postId : String,likeCount : List<String>){
            like?.invoke(postId,likeCount)
            binding.ivLike.setImageResource(R.drawable.ic_fill_favorite)
        }
        fun dislikePost(postId : String,likeCount : List<String>){
            dislike?.invoke(postId,likeCount)
            binding.ivLike.setImageResource(R.drawable.ic_favorite)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPostDetailsViewHolder {
        val binding = RowDiscoverDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoverPostDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DiscoverPostDetailsViewHolder, position: Int) {
        val post = postList[position]
        var liked : Boolean? = null
        var postLikeCount : Int?

        if (post.likeCount != null){
            liked = post.likeCount?.contains(userId)
            postLikeCount = post.likeCount!!.size
        }else{
            postLikeCount = 0
        }
        holder.binding.tvLike.text = "${postLikeCount.toString()} beğeni"
        if (post.comments != null){
            holder.binding.tvComment.text = "${post.comments?.size.toString()} yorum"
        }else{
            holder.binding.tvComment.text = "Henüz Yorum Yok"
        }
        Glide.with(holder.itemView.context).load(post.images?.get(0)).into(holder.binding.ivPost)
        Glide.with(holder.itemView.context).load(post.ownerImage.toString()).into(holder.binding.ivUserProfile)
        holder.binding.apply {
            postItem = post
        }
        if (liked == true){
            holder.binding.ivLike.setImageResource(R.drawable.ic_fill_favorite)
        }else{
            holder.binding.ivLike.setImageResource(R.drawable.ic_favorite)
        }
        holder.binding.ivLike.setOnClickListener {
            if (liked == null){
                liked = false
            }
            if (liked!!){
                holder.dislikePost(post.postId.toString(),post.likeCount ?: emptyList())
                postLikeCount -= 1
                holder.binding.tvLike.text ="${postLikeCount.toString()} beğeni"
                liked = false
            }else{
                holder.likePost(post.postId.toString(),post.likeCount ?: emptyList())
                postLikeCount += 1
                holder.binding.tvLike.text ="${postLikeCount.toString()} beğeni"
                liked = true
            }
        }
        holder.binding.ivComment.setOnClickListener {
            val action = DiscoverDetailsFragmentDirections.actionDiscoverDetailsFragmentToCommentsFragment(post.postId.toString())
            Navigation.findNavController(it).navigate(action)
        }
        holder.binding.userInfoBar.setOnClickListener {
            val action = DiscoverDetailsFragmentDirections.actionDiscoverDetailsFragmentToUserProfileFragment(post.postOwner.toString())
            Navigation.findNavController(it).navigate(action)
        }
    }

    var like: ((String,List<String>) -> Unit)? = null
    var dislike: ((String,List<String>) -> Unit)? = null
}
