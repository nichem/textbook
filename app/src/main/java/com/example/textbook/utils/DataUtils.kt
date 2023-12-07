package com.example.textbook.utils

import android.content.res.AssetManager
import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataUtils {
    suspend fun isGenerate(): Boolean {
        return Repository.getTextbookCount() == 0
    }

    suspend fun generateData(assets: AssetManager) {
        val names = listOf("part_100.json", "part_101.json", "part_102.json")
        names.forEach { name ->
            val ins = assets.open(name)
            withContext(Dispatchers.IO) {
                val text = ConvertUtils.inputStream2String(ins, Charsets.UTF_8.name())
                val array = JsonParser().parse(text).asJsonArray
                array.forEach {
                    val textbook = it.asJsonObject.toTextbook()
                    Repository.insertTextbook(textbook)
                }

                Log.d("test", "数据量：${Repository.getTextbookCount()}")
            }
        }
    }

    private suspend fun JsonObject.toTextbook(): Textbook {
        val id = get("id").asString
        val title = get("title").asString
        val download =
            "https://r2-ndr.ykt.cbern.com.cn/edu_product/esp/assets_document/${id}.pkg/pdf.pdf"
        val properties = getAsJsonObject("custom_properties")
        val previewObj = properties.getAsJsonObject("preview")
        val preview = parsePreview(previewObj)
        val thumbnails = properties.getAsJsonArray("thumbnails").map {
            it.asString
        }
        val size = properties.get("size").asInt
        val tagIdsArray = getAsJsonArray("tag_list")
        val tagIds = parseTags(tagIdsArray)
        return Textbook(
            id, title, download, preview, thumbnails, size, tagIds
        )
    }

    private fun parsePreview(preview: JsonObject): List<String> {
        val keys = preview.keySet().toMutableList()
        keys.sortBy {
            it.replace("Slide", "").toInt()
        }
        return keys.map {
            preview.get(it).asString
        }
    }

    private suspend fun parseTags(tags: JsonArray): List<String> {
        return tags.map {
            it.asJsonObject.get("tag_name").asString
        }
    }
}