package com.example.textbook.utils

import android.content.Context
import com.example.textbook.database.Textbook
import java.io.File

fun Textbook.getFile(context: Context): File {
    return File(context.getExternalFilesDir(null), "${id}.pdf")
}