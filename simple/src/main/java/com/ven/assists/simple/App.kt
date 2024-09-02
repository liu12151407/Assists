package com.ven.assists.simple

import android.app.Application
import com.blankj.utilcode.util.Utils

class App : Application() {

    companion object{
        const val TARGET_PACKAGE_NAME = "com.tencent.mm"
    }
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}