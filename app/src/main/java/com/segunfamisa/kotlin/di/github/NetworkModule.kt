package com.segunfamisa.kotlin.di.github

import com.segunfamisa.kotlin.github.data.GithubApiService
import com.segunfamisa.kotlin.github.data.GithubManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Wire up the injection providers (modules) to the injection targets (activities, frags).
 *
 * Created by connieli on 5/28/17.
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    @Named("github")
    fun createRetrofitGithub(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
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