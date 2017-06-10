package com.lishiyo.kotlin.features.casualq.ui

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lishiyo.kotlin.features.casualq.ui.viewmodel.Question
import com.lishiyo.kotlin.commons.adapter.ViewType
import com.lishiyo.kotlin.commons.adapter.ViewTypeDelegateAdapter
import com.lishiyo.kotlin.commons.extensions.inflate
import com.lishiyo.kotlin.samples.retrofit.R
import kotlinx.android.synthetic.main.list_item_question.view.*

/**
 * Created by connieli on 5/28/17.
 */
class QuestionDelegateAdapter(val viewListener: QuestionDelegateAdapter.onViewSelectedListener?) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onItemSelected(item: Question)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return QuestionViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        (holder as QuestionViewHolder).bind(item as Question)
    }

    inner class QuestionViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.list_item_question)) {
        var currentItem: Question? = null

        init {
            super.itemView.setOnClickListener { currentItem?.let {
                viewListener?.onItemSelected(it)
            }}
        }

        fun bind(item: Question) = with(itemView) {
            currentItem = item

            question_text.text = item.text
        }
    }
}