package com.lishiyo.kotlin.di.github

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.lishiyo.kotlin.features.scribbles.github.data.GithubApiService
import com.lishiyo.kotlin.features.scribbles.github.data.GithubManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Wire up the injection providers (modules) to the injection targets (activities, frags).
 *
 * Created by connieli on 5/28/17.
 */
@Module
class DataModule {

    @Provides
    @Singleton
    @Named("github")
    fun createRetrofitGithub(): Retrofit {
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
    fun createGithubApi(): GithubApiService {
        return createRetrofitGithub().create(GithubApiService::class.java)
    }

    @Provides
    @Singleton
    fun createGithubManager(): GithubManager {
        return GithubManager(createGithubApi())
    }
}