package com.lishiyo.kotlin.di.casualq

import com.lishiyo.kotlin.features.casualq.data.CommentsResponse

/**
 * Created by connieli on 6/9/17.
 */
interface RedditApiService {
    @retrofit2.http.GET("r/{subreddit}/comments/{postId}")
    fun getCommentsForPost(@retrofit2.http.Path("subreddit") subreddit: String,
                           @retrofit2.http.Path("postId") postId: String,
                           @retrofit2.http.Query("limit") query: Int = 20,
                           @retrofit2.http.Query("showmore") page: Boolean = true,
                           @retrofit2.http.Query("sort") sort: String = "top"): io.reactivex.Observable<CommentsResponse>


    /**
     * Companion object for the factory
     */
    companion object Factory {
        val BASE_URL = "https://api.reddit.com/"

        fun create(): RedditApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

            return retrofit.create(RedditApiService::class.java)
        }
    }
}