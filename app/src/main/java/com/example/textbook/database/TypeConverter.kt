package com.example.textbook.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {
    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun list2String(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun string2List(string: String): List<String> {
        return gson.fromJson(string, listType)
    }
}