package com.lishiyo.kotlin.github.ui

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lishiyo.kotlin.commons.adapter.ViewType
import com.lishiyo.kotlin.commons.adapter.ViewTypeDelegateAdapter
import com.lishiyo.kotlin.commons.extensions.inflate
import com.lishiyo.kotlin.github.ui.viewmodel.GithubUser
import com.lishiyo.kotlin.samples.retrofit.R
import kotlinx.android.synthetic.main.list_item_github_user.view.*


/**
 * Handles {@link SearchResponse#User}.
 */
class UserDelegateAdapter(val viewListener: onViewSelectedListener?) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onItemSelected(item: GithubUser?)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return UserViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as UserViewHolder
        holder.bind(item as GithubUser)
    }

    inner class UserViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.list_item_github_user)) {
        var currentItem: GithubUser? = null

        init {
            super.itemView.setOnClickListener { viewListener?.onItemSelected(currentItem)}
        }

        fun bind(item: GithubUser) = with(itemView) {
            currentItem = item

            username.text = item.login
            avatar.setImageURI(item.avatarUrl)
        }
    }
}