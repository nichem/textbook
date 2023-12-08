package com.example.textbook.utils

import android.os.Environment
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.TimeZone

object DownloadUtil {
    private val okHttpClient: OkHttpClient = OkHttpClient()

    /**
     * @param url 下载连接
     * @param downloadFile 要写入的文件对象
     * @param listener 下载监听
     */
    fun download(url: String, downloadFile: File, listener: OnDownloadListener) {
        val request: Request = Request
            .Builder()
            .url(url)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("test", e.stackTraceToString())
                // 下载失败
                listener.onDownloadFailed()
            }

            override fun onResponse(call: Call, response: Response) {
                var ins: InputStream? = null
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream? = null
                try {
                    ins = response.body!!.byteStream()
                    val total = response.body!!.contentLength()
                    fos = FileOutputStream(downloadFile)
                    var sum: Long = 0
                    while (true) {
                        len = ins.read(buf)
                        if (len == -1) break
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        // 下载中
                        listener.onDownloading(progress)
                    }
                    fos.flush()
                    // 下载完成
                    listener.onDownloadSuccess()
                } catch (e: Exception) {
                    Log.e("test", e.stackTraceToString())
                    listener.onDownloadFailed()
                } finally {
                    try {
                        ins?.close()
                    } catch (e: IOException) {
                    }
                    try {
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }
        })
    }

    interface OnDownloadListener {
        /**
         * 下载成功
         */
        fun onDownloadSuccess()

        /**
         * @param progress
         * 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         */
        fun onDownloadFailed()
    }
}