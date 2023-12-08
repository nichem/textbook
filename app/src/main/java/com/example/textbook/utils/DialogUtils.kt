package com.example.textbook.utils

import android.app.AlertDialog
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

fun Context.showAskDialog(msg: String, onCancel: () -> Unit = {}, onConfirm: () -> Unit) {
    AlertDialog.Builder(this)
        .setMessage(msg)
        .setPositiveButton("确认") { _, _ -> onConfirm() }
        .setNegativeButton("取消") { _, _ -> onCancel() }
        .show()
}