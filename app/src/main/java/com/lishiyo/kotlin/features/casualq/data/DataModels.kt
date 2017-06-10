package com.lishiyo.kotlin.features.casualq.data

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import com.lishiyo.kotlin.features.casualq.ui.viewmodel.Question

/**
 * Created by connieli on 5/28/17.
 */
@IgnoreExtraProperties
data class QuestionData(
        @SerializedName("_id") var id: String = "",
        @SerializedName("text") val text: String = "",
        @SerializedName("source") val source: String = "",
        @SerializedName("level") val level: Int = 0,
        @SerializedName("saved") val saved: Boolean = false
) {
    constructor(viewmodel: Question) : this(text = viewmodel.text, source = viewmodel.source)
}

/**
 * Local file response.
 */
data class QuestionsResponse(
        val questions: Map<String, List<QuestionData>>
)