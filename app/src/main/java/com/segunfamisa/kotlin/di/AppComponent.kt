package com.segunfamisa.kotlin.di

import com.segunfamisa.kotlin.di.github.NetworkModule
import dagger.Component
import javax.inject.Singleton

/**
 * Wire up the injection providers (modules) to the injection targets (activities, frags).
 *
 * Created by connieli on 5/28/17.
 */
@Component(
        modules = arrayOf(
                AppModule::class,
                NetworkModule::class
        )
)
@Singleton
interface AppComponent