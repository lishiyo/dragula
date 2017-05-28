package com.segunfamisa.kotlin.github.ui

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.segunfamisa.kotlin.commons.adapter.ViewType
import com.segunfamisa.kotlin.commons.adapter.ViewTypeDelegateAdapter
import com.segunfamisa.kotlin.commons.extensions.inflate
import com.segunfamisa.kotlin.github.ui.viewmodel.GithubUser
import com.segunfamisa.kotlin.samples.retrofit.R
import kotlinx.android.synthetic.main.github_list_item_user.view.*



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
            parent.inflate(R.layout.github_list_item_user)) {
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