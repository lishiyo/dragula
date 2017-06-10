package com.lishiyo.kotlin.features.casualq.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.database.*
import com.lishiyo.kotlin.App
import com.lishiyo.kotlin.commons.ui.RxBaseFragment
import com.lishiyo.kotlin.features.casualq.data.QuestionData
import com.lishiyo.kotlin.features.casualq.data.QuestionsManager
import com.lishiyo.kotlin.features.casualq.ui.viewmodel.Question
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Shows saved questions from Firebase.
 *
 * Created by connieli on 5/28/17.
 */
class QuestionsFragment : RxBaseFragment(), QuestionDelegateAdapter.onViewSelectedListener {
    // Data
    @Inject lateinit var manager: QuestionsManager
    @Inject lateinit var firebaseDb: FirebaseDatabase

    // UI
    @BindView(R.id.questions_list) lateinit var list: RecyclerView
    @BindView(R.id.btn_add_question) lateinit var composeButton: Button
    @BindView(R.id.add_question_text) lateinit var composeEditText: EditText
    @BindView(R.id.btn_delete_all) lateinit var deleteAllBtn: Button
    @BindView(R.id.btn_reseed) lateinit var reseedBtn: Button

    private val questionsAdapter = QuestionsAdapter(this)
    lateinit private var localQuestionsObservable: Observable<List<QuestionData>>

    // == FIREBASE: https://kotlin-sandbox.firebaseio.com/
    // https://console.firebase.google.com/u/0/project/kotlin-sandbox/database/data/
    lateinit var questionsRef: DatabaseReference
    lateinit var rootChildEventListener: ChildEventListener

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
//            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = questionsAdapter
        }

        initFirebase()
    }

    override fun onResume() {
        super.onResume()

        localQuestionsObservable = manager.getQuestions(context) // TODO: get from remote
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        compositeDisposable.add(
                localQuestionsObservable
                        .subscribe(
                                { results ->
                                    // convert data to viewmodels
                                    val questions = Question.createFromList(results)
                                    questionsAdapter.setQuestions(questions)
                                },
                                {
                                    throwable -> Log.e("connie", throwable.message)
                                })
        )
    }

    private fun initFirebase() {
        // Get a reference to the questions child items in the database
        questionsRef = firebaseDb.getReference("questions")

        // Assign a listener to detect changes to the child items
        // of the database reference.
        rootChildEventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError?) {
                Log.d("connie", "Failed to read value.", error?.toException())
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                Log.d("connie", "onChildMoved! $previousChildName")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                Log.d("connie", "onChildChanged! $previousChildName")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val question = dataSnapshot.getValue(Question::class.java)
                Log.d("connie", "onChildRemoved! " + question!!.text)
                questionsAdapter.removeQuestion(question!!)
            }

            // This function is called once for each child that exists
            // when the listener is added. Then it is called
            // each time a new child is added.
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val question = dataSnapshot.getValue(Question::class.java)
                Log.d("connie", "onChildAdded! " + question!!.text)
                questionsAdapter.addQuestion(question!!)
            }
        }

        questionsRef.addChildEventListener(rootChildEventListener)

        initButtons()
    }

    private fun initButtons() {
        // Add items via the Button and EditText at the bottom of the window.
        composeButton.setOnClickListener {
            // Create a new child DatabaseReference with a auto-generated ID.
            val childRef = questionsRef.push()

            // Set the child's data to the value passed in from the text box.
            val text = composeEditText.text.toString()
            val source = "custom"
            val level = 0

            if (!text.isBlank()) {
                val newQuestion = QuestionData(childRef.key, text, source, level, saved = true)
                childRef.setValue(newQuestion)
            }
        }

        deleteAllBtn.setOnClickListener {
            // Wipe out everything in firebase
            questionsRef.removeValue()
        }

        reseedBtn.setOnClickListener {
            // clear, then repopulate from local files
            questionsRef.removeValue()
            manager.populateFirebaseFromLocal(context, questionsRef, QuestionsManager.DEFAULT_SEED_FILE)
        }
    }

    override fun onPause() {
        super.onPause() // clears disposable

        tearDown()
    }

    override fun onItemSelected(question: Question) {
        // item clicked!
        val query = questionsRef.orderByKey().equalTo(question._id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
                Log.e("connie", "delete failed: " + question.text)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.i("connie", "data changed for: " + question.text)
                }
            }
        })
    }

    private fun tearDown() {
        questionsRef.removeEventListener(rootChildEventListener)
    }
}