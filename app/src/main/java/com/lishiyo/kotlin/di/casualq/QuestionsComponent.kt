package com.lishiyo.kotlin.di.casualq

import com.lishiyo.kotlin.casualq.ui.QuestionsFragment
import com.lishiyo.kotlin.di.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by connieli on 5/28/17.
 */
@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        DataModule::class
))
interface QuestionsComponent {
    fun inject(target: QuestionsFragment)
}