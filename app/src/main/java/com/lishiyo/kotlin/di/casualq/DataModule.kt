package com.lishiyo.kotlin.di.casualq

import com.google.firebase.database.FirebaseDatabase
import com.lishiyo.kotlin.casualq.data.QuestionsManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun createQuestionsManager(): QuestionsManager {
        return QuestionsManager()
    }

    @Provides
    @Singleton
    fun createFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
}