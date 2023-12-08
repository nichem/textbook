package com.example.textbook.utils

import android.app.ProgressDialog
import android.content.Context

fun Context.showLoading(
    msg: String,
    canCancelable: Boolean,
    onCancel: () -> Unit = {}
): ProgressDialog {
    val loading = ProgressDialog(this).apply {
        setMessage(msg)
        setCancelable(canCancelable)
        setCanceledOnTouchOutside(canCancelable)
        setOnCancelListener {
            onCancel()
        }
    }
    loading.show()
    return loading

}