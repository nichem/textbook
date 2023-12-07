package com.example.textbook.utils

import android.app.ProgressDialog
import android.content.Context

fun Context.showLoading(msg: String): ProgressDialog {
    val loading = ProgressDialog(this).apply {
        setMessage(msg)
    }
    loading.show()
    return loading

}