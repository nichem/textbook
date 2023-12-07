package com.example.textbook.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.bumptech.glide.Glide
import com.example.textbook.R
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.example.textbook.databinding.ItemTextbookBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class TextbookAdapter(diffCallback: TextbookComparator) :
    PagingDataAdapter<Textbook, TextbookAdapter.TextbookViewHolder>(diffCallback) {
    class TextbookViewHolder(itemView: View, val binding: ItemTextbookBinding) :
        RecyclerView.ViewHolder(itemView) {
    }

    private val scope = CoroutineScope(Main)

    override fun onBindViewHolder(holder: TextbookViewHolder, position: Int) {
        val textbook = getItem(position)
        if (textbook != null) {
            Glide.with(holder.itemView)
                .load(textbook.thumbnails[0])
//                .override(120, 200)
                .into(holder.binding.imageView)
            holder.binding.tvTitle.text = pureTitle(textbook.title)
            holder.binding.imageViewFavorite.setImageResource(
                if (textbook.isFavorite) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24
            )
            holder.binding.tvFavorite.setTextColor(
                if (textbook.isFavorite) ColorUtils.getColor(R.color.select_color)
                else ColorUtils.getColor(R.color.normal_color)
            )
        }
        holder.binding.layoutFavorite.setOnClickListener {
            if (textbook == null) return@setOnClickListener
            scope.launch {
                val row = Repository.favoriteTextbook(textbook, !textbook.isFavorite)
                if (row > 0) {
                    textbook.isFavorite = !textbook.isFavorite
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextbookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_textbook, parent, false)
        return TextbookViewHolder(
            view, ItemTextbookBinding.bind(view)
        )
    }

    private fun pureTitle(title: String): String {
        val pure = title.replace("义务教育教科书·", "")
        return "《$pure》"
    }
}


class TextbookComparator : DiffUtil.ItemCallback<Textbook>() {
    override fun areItemsTheSame(oldItem: Textbook, newItem: Textbook): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Textbook, newItem: Textbook): Boolean {
        return oldItem == newItem && oldItem.isFavorite == newItem.isFavorite
    }

}