package com.example.textbook.utils

import android.app.ProgressDialog
import android.content.Context

fun Context.showLoading(msg: String, isCancel: Boolean): ProgressDialog {
    val loading = ProgressDialog(this).apply {
        setMessage(msg)
        setCancelable(isCancel)
        setCanceledOnTouchOutside(isCancel)
    }
    loading.show()
    return loading

}