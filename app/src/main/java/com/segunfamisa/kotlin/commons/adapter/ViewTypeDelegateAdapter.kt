package com.segunfamisa.kotlin.commons.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by connieli on 5/28/17.
 */
interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}