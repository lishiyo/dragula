package com.lishiyo.kotlin.casualq.data

import android.content.Context
import com.google.firebase.database.DatabaseReference
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

    fun getQuestions(context: Context) : Observable<List<QuestionData>> {
        // todo: switch to firebase
        return getQuestionsFromLocal(context)
    }

    /**
     * Retrieve the list from local repo.
     */
    fun getQuestionsFromLocal(context: Context) : Observable<List<QuestionData>> {
        val res = context.loadJsonFromFile("casualq_seed.json")

        val fullRespType = object : TypeToken<QuestionsResponse>() {}.type
        val fullRespNode = Gson().fromJson<QuestionsResponse>(res, fullRespType)

        val allQuestions: ArrayList<QuestionData> = ArrayList()
        for ((_, arr) in fullRespNode.questions) {
            allQuestions.addAll(arr)
        }

        return Observable.just(allQuestions)
    }

    fun getQuestionsFromRemote() : Observable<List<QuestionData>> {
        // todo: retrieve from firebase
        return Observable.just(ArrayList())
    }

    fun populateFirebase(context: Context, firebaseRef: DatabaseReference) {
        val res = context.loadJsonFromFile("casualq_seed.json")

        val fullRespType = object : TypeToken<QuestionsResponse>() {}.type
        val fullRespNode = Gson().fromJson<QuestionsResponse>(res, fullRespType)

        for ((_, questionsFromSource) in fullRespNode.questions) {
            for (question in questionsFromSource) {
                val childRef = firebaseRef.push()
                childRef.setValue(question)
            }
        }

    }
}