package com.lishiyo.kotlin.features.scribbles.github.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.App
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.commons.ui.RxBaseActivity
import com.lishiyo.kotlin.features.scribbles.github.data.GithubManager
import com.lishiyo.kotlin.features.scribbles.github.ui.viewmodel.GithubUser
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by connieli on 6/25/17.
 */
class GithubActivity : RxBaseActivity(), UserDelegateAdapter.onViewSelectedListener  {
    // Data
    @Inject lateinit var repository: GithubManager

    // UI
    @BindView(R.id.github_list) lateinit var list: RecyclerView
    private val githubAdapter = GithubAdapter(this)

    companion object {
        fun createIntent(context: Context, bundle: Bundle?): Intent {
            val intent = Intent(context, GithubActivity::class.java)
            bundle?.let { intent.putExtras(it) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github)

        ButterKnife.bind(this)
        App.githubComponent.inject(this)

        list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = githubAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        compositeDisposable.add(
                repository.searchUsers("NYC", "Java")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { searchResults ->
                                    // convert users to viewmodels
                                    val githubUsers = GithubUser.createFromList(searchResults.items)
                                    githubAdapter.addUsers(githubUsers)
                                }, { throwable ->
                            Log.e(DEBUG_TAG, throwable.message)
                        })
        )
    }

    override fun onItemSelected(item: GithubUser?) {
        Log.i(DEBUG_TAG, "clicked github user! ${item?.login}")
    }
}