package com.example.textbook.database

import android.util.Log
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.textbook.App
import com.example.textbook.App.Companion.PAGE_SIZE
import com.example.textbook.App.Companion.app
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

object Repository {
    private val db = Room.databaseBuilder(
        app, AppDatabase::class.java, "app_database.db"
    ).build()

    private val localDao = db.getLocalDao()

    suspend fun insertTextbook(textbook: Textbook) {
        withContext(IO) {
            localDao.insertTextbook(textbook)
        }
    }

    suspend fun getTextbookCount(): Int {
        return withContext(IO) { localDao.getTextbookCount() }
    }

    suspend fun loadTextbooks(page: Int): List<Textbook> {
        val offset = page * PAGE_SIZE
        return withContext(IO) { localDao.loadTextbooks(offset) }
    }

    /**
     * @return 影响的行数
     */
    suspend fun favoriteTextbook(textbook: Textbook, isFavorite: Boolean): Int {
        val textbook2 = textbook.copy(isFavorite = isFavorite)
        return withContext(IO) {
            localDao.updateTextbook(textbook2)
        }
    }

    suspend fun loadFavoriteTextBooks(page: Int): List<Textbook> {
        val offset = page * PAGE_SIZE
        return withContext(IO) {
            localDao.loadFavoriteTextbooks(offset)
        }
    }

    suspend fun findTextbook(key: String, page: Int): List<Textbook> {
        val offset = page * PAGE_SIZE
        val keys = key.split(" ")
        var query = "SELECT * from textbook WHERE "
        keys.forEach {
            if (it.isBlank()) return@forEach
            query += "title LIKE '%$it%' AND "
        }
        query = query.substring(0, query.length - 4)
        query += "LIMIT $PAGE_SIZE OFFSET $offset"
        Log.d("test", "query: $query")
        return withContext(IO) {
            localDao.findTextbookByTitle(SimpleSQLiteQuery(query))
        }
    }

    var isShowAuthor: Boolean
        get() = MMKV.defaultMMKV().decodeBool("isShowAuthor", true)
        set(value) {
            MMKV.defaultMMKV().encode("isShowAuthor", value)
        }
}