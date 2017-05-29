package com.segunfamisa.kotlin

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import com.segunfamisa.kotlin.di.AppComponent
import com.segunfamisa.kotlin.di.AppModule
import com.segunfamisa.kotlin.di.DaggerAppComponent
import com.segunfamisa.kotlin.di.github.DaggerGithubComponent
import com.segunfamisa.kotlin.di.github.GithubComponent
import com.segunfamisa.kotlin.di.github.NetworkModule

/**
 * Created by connieli on 5/28/17.
 */
class App : Application() {
    // static props
    companion object {
        lateinit var appComponent: AppComponent
        lateinit var githubComponent: GithubComponent

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

        githubComponent = DaggerGithubComponent.builder()
                .networkModule(NetworkModule())
                .build()
    }
}