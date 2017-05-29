package com.lishiyo.kotlin.casualq.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.App
import com.lishiyo.kotlin.casualq.data.QuestionsManager
import com.lishiyo.kotlin.casualq.ui.viewmodel.Question
import com.lishiyo.kotlin.commons.ui.RxBaseFragment
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by connieli on 5/28/17.
 */
class QuestionsFragment : RxBaseFragment(), QuestionDelegateAdapter.onViewSelectedListener {
    // Data
    @Inject lateinit var manager: QuestionsManager

    // UI
    @BindView(R.id.questions_list) lateinit var list: RecyclerView
    private val questionsAdapter = QuestionsAdapter(this)

    companion object {
        fun newInstance(bundle: Bundle?): QuestionsFragment {
            val f = QuestionsFragment()
            f.arguments = bundle
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.questionsComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_casualq, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = questionsAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        compositeDisposable.add(
                manager.getQuestionsFromLocal(context)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { results ->
                                    // convert to viewmodels
                                    val questions = Question.createFromList(results)
                                    questionsAdapter.addQuestions(questions)
                                }, { throwable ->
                            Log.e("connie", throwable.message)
                        })
        )
    }
    override fun onItemSelected(item: Question?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}