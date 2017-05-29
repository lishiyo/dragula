package com.lishiyo.kotlin.di.casualq

import com.lishiyo.kotlin.casualq.data.QuestionsManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class QuestionsModule {

    @Provides
    @Singleton
    fun createQuestionsManager(): QuestionsManager {
        return QuestionsManager()
    }
}