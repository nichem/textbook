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
import com.example.textbook.App.Companion.app
import com.example.textbook.R
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.example.textbook.databinding.ItemTextbookBinding
import com.example.textbook.utils.getFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class TextbookAdapter(diffCallback: TextbookComparator) :
    PagingDataAdapter<Textbook, TextbookAdapter.TextbookViewHolder>(diffCallback) {
    class TextbookViewHolder(itemView: View, val binding: ItemTextbookBinding) :
        RecyclerView.ViewHolder(itemView) {
    }


    override fun onBindViewHolder(holder: TextbookViewHolder, position: Int) {
        val textbook = getItem(position)
        if (textbook != null) {
            Glide.with(holder.itemView)
                .load(textbook.thumbnails[0])
//                .override(120, 200)
                .into(holder.binding.imageView)
            holder.binding.tvTitle.text = pureTitle(textbook.title)
            updateFavoriteUI(holder.binding, textbook.isFavorite)
            updateDownloadUI(holder.binding, textbook)
        }
        holder.binding.layoutFavorite.setOnClickListener {
            if (textbook == null) return@setOnClickListener
            callback?.onFavoriteClick(this, textbook, position)
        }

        holder.binding.root.setOnFocusChangeListener { _, hasFocus ->
            if (textbook == null) return@setOnFocusChangeListener
            if (hasFocus) {
                callback?.onItemClick(this, textbook, position)
            }
        }

        holder.binding.layoutDownload.setOnClickListener {
            if (textbook == null) return@setOnClickListener
            callback?.onDownloadClick(this, textbook, position)
        }
    }

    private fun updateFavoriteUI(binding: ItemTextbookBinding, isFavorite: Boolean) {
        binding.imageViewFavorite.setImageResource(
            if (isFavorite) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24
        )
        binding.tvFavorite.setTextColor(
            if (isFavorite) ColorUtils.getColor(R.color.select_color)
            else ColorUtils.getColor(R.color.normal_color)
        )
        binding.tvFavorite.text = if (isFavorite) "取消收藏" else "收藏"
    }

    private fun updateDownloadUI(binding: ItemTextbookBinding, textbook: Textbook) {
        val file = textbook.getFile(app)
        val isDownload = file.exists() && file.length() > 10
        binding.tvDownload.text = if (isDownload) "删除" else "下载"
        binding.tvDownload.setTextColor(
            if (isDownload) ColorUtils.getColor(R.color.select_color)
            else ColorUtils.getColor(R.color.normal_color)
        )
        binding.imageViewDownload.setImageResource(
            if (isDownload) R.drawable.baseline_block_24
            else R.drawable.baseline_download_24
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextbookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_textbook, parent, false)
        return TextbookViewHolder(
            view, ItemTextbookBinding.bind(view)
        )
    }

    fun pureTitle(title: String): String {
//        val pure = title.replace("义务教育教科书·", "")
        return "《$title》"
    }

    var callback: Callback? = null

    interface Callback {
        fun onFavoriteClick(adapter: TextbookAdapter, textbook: Textbook, position: Int)

        fun onItemClick(adapter: TextbookAdapter, textbook: Textbook, position: Int)

        fun onDownloadClick(adapter: TextbookAdapter, textbook: Textbook, position: Int)
    }
}


class TextbookComparator : DiffUtil.ItemCallback<Textbook>() {
    override fun areItemsTheSame(oldItem: Textbook, newItem: Textbook): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Textbook, newItem: Textbook): Boolean {
        return oldItem == newItem
    }

}