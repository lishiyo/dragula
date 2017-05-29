package com.segunfamisa.kotlin

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
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
    companion object {
        lateinit var appComponent: AppComponent
        lateinit var githubComponent: GithubComponent
    }

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        githubComponent = DaggerGithubComponent.builder()
                .networkModule(NetworkModule())
                .build()
    }
}