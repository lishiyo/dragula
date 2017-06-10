package com.lishiyo.kotlin.features.casualq.data

import com.lishiyo.kotlin.di.casualq.RedditApiService

/**
 * Created by connieli on 6/10/17.
 */
class RedditManager(val apiService: RedditApiService) {
    companion object {
        val SEED_POST_ID = "6djhr4"
        val SEED_SUBREDDIT = "casualconversation"
    }

    fun getCommentsForPost(subreddit: String = SEED_SUBREDDIT, postId: String = SEED_POST_ID): io.reactivex.Observable<List<Listing>> {
        return apiService.getCommentsForPost(subreddit, postId, limit = 200)
    }
}