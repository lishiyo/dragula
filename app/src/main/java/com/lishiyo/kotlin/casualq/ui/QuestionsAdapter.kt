package com.lishiyo.kotlin.casualq.ui

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lishiyo.kotlin.casualq.Constants
import com.lishiyo.kotlin.casualq.ui.viewmodel.Question
import com.lishiyo.kotlin.commons.adapter.ViewType
import com.lishiyo.kotlin.commons.adapter.ViewTypeDelegateAdapter

/**
 * Created by connieli on 5/28/17.
 */
class QuestionsAdapter(listener: QuestionDelegateAdapter.onViewSelectedListener? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // backing list
    private val items: ArrayList<ViewType> = ArrayList()
    // map of { ViewType constant => Delegate for that ViewType }
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val loadingItem = object : ViewType {
        override fun getViewType(): Int = Constants.LOADING
    }

    init {
        delegateAdapters.put(Constants.QUESTION, QuestionDelegateAdapter(listener))
        // TODO: add loader type
    }

    /**
     * Set the questions to list.
     */
    fun setQuestions(questions: List<Question>) {
        // TODO: first remove loading and notify
        items.clear()

        items.addAll(questions)
        notifyDataSetChanged()
    }

    fun addQuestions(questions: List<Question>) {
        val originalLength = items.size
        items.addAll(questions)
        notifyItemRangeInserted(originalLength, items.size)
    }

    fun addQuestion(question: Question) {
        items.add(question)
        notifyItemInserted(items.size - 1)
    }

    fun removeQuestion(question: Question) {
        val oldPosition = items.indexOf(question)
        if (oldPosition != -1) {
            items.remove(question)
            notifyItemRemoved(oldPosition)
        }
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