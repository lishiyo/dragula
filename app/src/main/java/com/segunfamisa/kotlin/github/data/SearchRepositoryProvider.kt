package com.segunfamisa.kotlin.github.data


object SearchRepositoryProvider {

    fun provideSearchRepository(): SearchRepository {
        return SearchRepository(GithubApiService.Factory.create())
    }

}