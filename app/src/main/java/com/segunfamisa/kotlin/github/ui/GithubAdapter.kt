package com.segunfamisa.kotlin.github.ui

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.segunfamisa.kotlin.commons.adapter.Constants
import com.segunfamisa.kotlin.commons.adapter.ViewType
import com.segunfamisa.kotlin.commons.adapter.ViewTypeDelegateAdapter
import com.segunfamisa.kotlin.github.ui.viewmodel.GithubUser

/**
 * Created by connieli on 5/28/17.
 */
class GithubAdapter(listener: UserDelegateAdapter.onViewSelectedListener? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // backing list
    private val items: ArrayList<ViewType> = ArrayList()
    // map of { ViewType constant => Delegate for that ViewType }
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val loadingItem = object : ViewType {
        override fun getViewType(): Int = Constants.LOADING
    }

    init {
        delegateAdapters.put(Constants.GITHUB_USER, UserDelegateAdapter(listener))
        // TODO: add loader type
    }

    fun addUsers(users: List<GithubUser>) {
        // TODO: first remove loading and notify

        items.addAll(users)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(viewHolder, this.items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun getItemViewType(position: Int): Int {
        // map position to ViewType
        return this.items[position].getViewType()
    }
}