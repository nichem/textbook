package com.example.textbook.database

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Textbook(
    @PrimaryKey val id: String,
    val title: String,
    /**
     * https://r2-ndr.ykt.cbern.com.cn/edu_product/esp/assets_document/${id}.pkg/pdf.pdf
     */
    val download: String,
    val preview: List<String>,
    val thumbnails: List<String>,
    val size: Int,
    val tags: List<String>,
    var isFavorite: Boolean = false
)
