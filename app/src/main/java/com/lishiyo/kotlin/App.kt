package com.lishiyo.kotlin

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import com.lishiyo.kotlin.di.AppComponent
import com.lishiyo.kotlin.di.AppModule
import com.lishiyo.kotlin.di.DaggerAppComponent

/**
 * Created by connieli on 5/28/17.
 */
class App : Application() {
    // static props
    companion object {
        lateinit var appComponent: AppComponent

        fun getAppContext(): Context {
            return appComponent.getAppContext()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Stetho.initializeWithDefaults(this)

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}