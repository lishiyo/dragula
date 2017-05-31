package com.lishiyo.kotlin.casualq.ui

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
import com.lishiyo.kotlin.casualq.data.QuestionData
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
    @Inject lateinit var firebaseDb: FirebaseDatabase

    // UI
    @BindView(R.id.questions_list) lateinit var list: RecyclerView
    @BindView(R.id.temp_add_button) lateinit var composeButton: Button
    @BindView(R.id.temp_add_text) lateinit var composeEditText: EditText

    private val questionsAdapter = QuestionsAdapter(this)
    lateinit var questionsRef: DatabaseReference

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

    private fun initFirebase() {
        // Get a reference to the questions child items in the database
        questionsRef = firebaseDb.getReference("questions")

        // Assign a listener to detect changes to the child items
        // of the database reference.
        questionsRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError?) {
                Log.w("connie", "Failed to read value.", error?.toException())
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                Log.w("connie", "onChildMoved! $previousChildName")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                Log.w("connie", "onChildChanged! $previousChildName")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val question = dataSnapshot.getValue(Question::class.java)
                questionsAdapter.removeQuestion(question)
            }

            // This function is called once for each child that exists
            // when the listener is added. Then it is called
            // each time a new child is added.
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val question = dataSnapshot.getValue(Question::class.java)
                questionsAdapter.addQuestion(question)
            }
        })

        // Add items via the Button and EditText at the bottom of the window.
        composeButton.setOnClickListener {
            // Create a new child DatabaseReference with a auto-generated ID.
            val childRef = questionsRef.push()

            // Set the child's data to the value passed in from the text box.
            val text = composeEditText.text.toString()
            val source = "custom"

            if (!text.isBlank()) {
                val newQuestion = QuestionData(childRef.key, text, source)
                childRef.setValue(newQuestion)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        compositeDisposable.add(
                manager.getQuestions(context) // from local then firebase
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { results ->
                                    // convert to viewmodels
                                    val questions = Question.createFromList(results)
                                    questionsAdapter.setQuestions(questions)
                                }, { throwable ->
                            Log.e("connie", throwable.message)
                        })
        )

    }
    override fun onItemSelected(item: Question?) {
        // delete the item?
        val myQuery = questionsRef.orderByValue().equalTo(item.toString())

        myQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
                Log.e("connie", "delete failed")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChildren()) {
//                    val firstChild = dataSnapshot.children.iterator().next()
//                    firstChild.ref.removeValue()
                }
            }

        })
    }
}