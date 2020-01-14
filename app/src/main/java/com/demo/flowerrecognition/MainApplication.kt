package com.demo.flowerrecognition

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Instabug.Builder(this, "1ec1801a286cb453874aeb4fde39ced7")
            .setInvocationEvents(
                InstabugInvocationEvent.SHAKE,
                InstabugInvocationEvent.SCREENSHOT
            )
            .build()
    }
}