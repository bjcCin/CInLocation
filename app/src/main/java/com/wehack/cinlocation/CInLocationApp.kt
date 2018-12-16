package com.wehack.cinlocation

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class CInLocationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }
}