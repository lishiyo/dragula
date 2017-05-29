package com.lishiyo.kotlin.di

import android.content.Context
import dagger.Component
import javax.inject.Singleton

/**
 * Wire up the injection providers (modules) to the injection targets (activities, frags).
 *
 * Created by connieli on 5/28/17.
 */
@Component(
        modules = arrayOf(
                AppModule::class
        )
)
@Singleton
interface AppComponent {
    fun getAppContext() : Context
}