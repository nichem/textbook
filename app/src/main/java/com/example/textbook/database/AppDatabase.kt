package com.example.textbook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Textbook::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase() : RoomDatabase() {
    abstract fun getLocalDao(): LocalDao
}