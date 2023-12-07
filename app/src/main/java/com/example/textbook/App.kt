package com.example.textbook

import android.app.Application
import com.tencent.mmkv.MMKV

class App : Application() {
    companion object {
        lateinit var app: App
        const val PAGE_SIZE = 10
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        MMKV.initialize(this)
    }
}