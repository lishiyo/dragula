package com.lishiyo.kotlin.casualq.data

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import com.lishiyo.kotlin.casualq.ui.viewmodel.Question

/**
 * Created by connieli on 5/28/17.
 */
@IgnoreExtraProperties
data class QuestionData(
        @SerializedName("text") val text: String = "",
        @SerializedName("source") val source: String = ""
) {
    constructor(viewmodel: Question) : this(viewmodel.text, viewmodel.source)
}

/**
 * Local file response.
 */
data class QuestionsResponse(
        val questions: Map<String, List<QuestionData>>
)