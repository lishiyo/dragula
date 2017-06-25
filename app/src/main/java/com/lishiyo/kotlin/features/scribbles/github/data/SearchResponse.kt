package com.lishiyo.kotlin.features.scribbles.github.data

import com.google.gson.annotations.SerializedName

/**
 * Equivalent of user data class in kotlin
 */
data class User(
        @SerializedName("login") val login: String,
        @SerializedName("id") val id: Long,
        @SerializedName("avatar_url") val avatarUrl: String,
        @SerializedName("url") val url: String,
        @SerializedName("html_url") val htmlUrl: String,
        @SerializedName("followers_url") val followersUrl: String,
        @SerializedName("following_url") val followingUrl: String,
        @SerializedName("organizations_url") val organizationsUrl: String,
        @SerializedName("repos_url") val reposUrl: String,
        @SerializedName("starred_url") val starredUrl: String,
        @SerializedName("gists_url") val gistsUrl: String,
        @SerializedName("type") val type: String,
        @SerializedName("score") val score: Double
)

/**
 * Entire search result data class
 */
data class Result (
        val total_count: Int,
        val incomplete_results: Boolean,
        val items: List<User>
)