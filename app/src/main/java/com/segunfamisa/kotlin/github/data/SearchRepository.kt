package com.segunfamisa.kotlin.github.data

/**
 * Repository method to access search functionality of the api service
 */
class SearchRepository(val apiService: com.segunfamisa.kotlin.github.data.GithubApiService) {

    fun searchUsers(location: String, language: String): io.reactivex.Observable<Result> {
        return apiService.search(query = "location:$location language:$language")
    }

}