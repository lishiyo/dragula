package com.lishiyo.kotlin.casualq.ui.viewmodel

import com.lishiyo.kotlin.casualq.Constants
import com.lishiyo.kotlin.casualq.data.QuestionData
import com.lishiyo.kotlin.commons.adapter.ViewType

/**
 * Created by connieli on 5/28/17.
 */
/**
 * View model versions of the data.
 *
 * Created by connieli on 5/28/17.
 */
data class Question(
        val text: String = "",
        val source: String = ""
) : ViewType {
    constructor(model: QuestionData) : this(model.text, model.source)

    override fun getViewType(): Int = Constants.QUESTION

    companion object Factory {
        // parse from data to viewmodels
        fun createFromList(models: List<QuestionData>) : List<Question> {
            val viewmodels: ArrayList<Question> = ArrayList()
            models.mapTo(viewmodels) { Question(it) }

            return viewmodels
        }

        // parse from viewmodels to data to store into Firebase
        fun convertToData(viewmodels: List<Question>) : List<QuestionData> {
            val models: ArrayList<QuestionData> = ArrayList()
            viewmodels.mapTo(models) { QuestionData(it) }

            return models
        }
    }
}
