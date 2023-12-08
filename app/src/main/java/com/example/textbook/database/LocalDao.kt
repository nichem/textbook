package com.example.textbook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.textbook.App.Companion.PAGE_SIZE

@Dao
interface LocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTextbook(textbook: Textbook)

    @Query("select count(*) from textbook")
    fun getTextbookCount(): Int

    @Query("select * from textbook LIMIT $PAGE_SIZE OFFSET :offset")
    fun loadTextbooks(offset: Int): List<Textbook>

    @Query("select * from textbook WHERE isFavorite=1 LIMIT $PAGE_SIZE OFFSET :offset")
    fun loadFavoriteTextbooks(offset: Int): List<Textbook>

    @Update
    fun updateTextbook(textbook: Textbook): Int

    @Query("SELECT * from textbook WHERE title LIKE :key LIMIT $PAGE_SIZE OFFSET :offset")
    fun findTextbookByTitle(key: String, offset: Int): List<Textbook>

    @RawQuery
    fun findTextbookByTitle(rawQuery: SupportSQLiteQuery): List<Textbook>

//    @Query("SELECT * from textbook WHERE title LIKE :key AND isFavorite=1 LIMIT $PAGE_SIZE OFFSET :offset")
//    fun findFavoriteByTitle(key: String, offset: Int): List<Textbook>
}