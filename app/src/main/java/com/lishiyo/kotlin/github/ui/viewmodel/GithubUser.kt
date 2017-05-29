package com.lishiyo.kotlin.github.ui.viewmodel

import com.lishiyo.kotlin.commons.adapter.Constants
import com.lishiyo.kotlin.commons.adapter.ViewType
import com.lishiyo.kotlin.github.data.User

/**
 * View model versions of the data.
 *
 * Created by connieli on 5/28/17.
 */
data class GithubUser(
        val login: String,
        val id: Long,
        val avatarUrl: String,
        val url: String
) : ViewType {
    constructor(model: User) : this(model.login, model.id, model.avatarUrl, model.url)

    override fun getViewType(): Int = Constants.GITHUB_USER

    companion object Factory {
        fun createFromList(models: List<User>) : List<GithubUser> {
            val githubUsers: ArrayList<GithubUser> = ArrayList()
            for (model in models) {
                githubUsers.add(GithubUser(model))
            }

            return githubUsers
        }
    }
}
