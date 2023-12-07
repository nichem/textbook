package com.example.textbook.database

import androidx.room.Room
import com.example.textbook.App
import com.example.textbook.App.Companion.app
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
        val offset = page * App.PAGE_SIZE
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
        val offset = page * App.PAGE_SIZE
        return withContext(IO) {
            localDao.loadFavoriteTextbooks(offset)
        }
    }
}