package com.lishiyo.kotlin.casualq.data

import com.google.gson.annotations.SerializedName

/**
 * Created by connieli on 5/28/17.
 */
data class QuestionData(
        @SerializedName("text") val text: String = "",
        @SerializedName("source") val source: String = ""
)

/**
 * Local file response.
 */
data class QuestionsResponse(
        val questions: Map<String, List<QuestionData>>
)