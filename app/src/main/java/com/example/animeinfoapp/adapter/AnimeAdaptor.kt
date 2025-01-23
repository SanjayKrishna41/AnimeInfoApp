package com.example.animeinfoapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animeinfoapp.databinding.ItemAnimeDetailBinding
import com.example.animeinfoapp.models.Data

class AnimeAdaptor : RecyclerView.Adapter<AnimeAdaptor.AnimeListViewHolder>() {

    // implemntation of diff utils to over come notifydatachange
    private val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    // async List differ is a tool that takes 2 list and compares them and calculate the differences
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeListViewHolder {
        val itemBinding =
            ItemAnimeDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeListViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AnimeListViewHolder, position: Int) {
        val animeData = differ.currentList[position]
        Glide.with(holder.itemView).load(
            animeData.images.jpg.image_url
        ).into(holder.itemBinding.ivPoster)
        holder.itemBinding.tvTitle.text = "Title : ${animeData.title}"
        holder.itemBinding.tvEpisodes.text = "Episodes : ${animeData.episodes}"
        holder.itemBinding.tvRatings.text = "Rating Score : ${animeData.score}"

        holder.itemBinding.root.setOnClickListener {
            onItemCLickListener?.let {
                it(animeData)
            }
        }
    }

    override fun getItemCount(): Int {
        // since we are using differ we need to return count from the current list
        return differ.currentList.size
    }

    private var onItemCLickListener: ((Data) -> Unit)? = null

    fun setOnItemClickListener(listener: (Data) -> Unit) {
        onItemCLickListener = listener
    }

    inner class AnimeListViewHolder(var itemBinding: ItemAnimeDetailBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}