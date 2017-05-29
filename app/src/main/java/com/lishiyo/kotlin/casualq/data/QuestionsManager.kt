package com.lishiyo.kotlin.casualq.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lishiyo.kotlin.commons.extensions.loadJsonFromFile
import io.reactivex.Observable

/**
 * Manager to retrieve data from local or remote repos.
 *
 * Created by connieli on 5/28/17.
 */
class QuestionsManager {

    /**
     * Retrieve the list from local.
     */
    fun getQuestionsFromLocal(context: Context) : Observable<List<QuestionData>> {
        val res = context.loadJsonFromFile("casualq_seed.json")

        val fullRespType = object : TypeToken<QuestionsResponse>() {}.type
        val fullRespNode = Gson().fromJson<QuestionsResponse>(res, fullRespType)

        // todo flatten all the sources
        val redditQuestions = fullRespNode.questions["reddit"]

        return Observable.just(redditQuestions)
    }
}