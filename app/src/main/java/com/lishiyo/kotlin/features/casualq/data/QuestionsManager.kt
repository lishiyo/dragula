package com.lishiyo.kotlin.features.casualq.data

import android.content.Context
import android.util.Log
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

    companion object {
        val DEFAULT_SEED_FILE = "casualq_seed.json"
    }

    fun getQuestions(context: Context) : Observable<List<QuestionData>> {
        // todo: switch to firebase
        return getQuestionsFromLocal(context)
    }

    /**
     * Retrieve the list from local repo.
     */
    fun getQuestionsFromLocal(context: Context) : Observable<List<QuestionData>> {
        val res = context.loadJsonFromFile(DEFAULT_SEED_FILE)

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

    fun populateFirebaseFromLocal(context: Context, firebaseRef: DatabaseReference, vararg filenames: String) {
        for (file in filenames) {
            Log.i("connie", "populating from file: " + file)

            val res = context.loadJsonFromFile(file)

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
}