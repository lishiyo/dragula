package com.lishiyo.kotlin.di.casualq

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.database.FirebaseDatabase
import com.lishiyo.kotlin.features.casualq.data.QuestionsManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
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

    @Provides
    @Singleton
    @Named("reddit")
    fun createRetrofitReddit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        return Retrofit.Builder()
                .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun createRedditApi(): RedditApiService {
        return createRetrofitReddit().create(RedditApiService::class.java)
    }
}