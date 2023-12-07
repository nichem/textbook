package com.example.textbook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.textbook.App.Companion.PAGE_SIZE

@Dao
interface LocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTextbook(textbook: Textbook)

    @Query("select count(*) from textbook")
    fun getTextbookCount(): Int

    @Query("select * from textbook LIMIT $PAGE_SIZE OFFSET :offset")
    fun loadTextbooks(offset: Int): List<Textbook>
}