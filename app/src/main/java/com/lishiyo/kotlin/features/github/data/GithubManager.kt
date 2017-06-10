package com.lishiyo.kotlin.features.github.data

/**
 * API wrapper around github api service
 */
class GithubManager(val apiService: GithubApiService) {

    fun searchUsers(location: String, language: String): io.reactivex.Observable<Result> {
        return apiService.search(query = "location:$location language:$language")
    }

}