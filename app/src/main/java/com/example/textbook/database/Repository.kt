package com.example.textbook.database

import androidx.room.Room
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
}