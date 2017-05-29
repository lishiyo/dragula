package com.segunfamisa.kotlin.github.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.segunfamisa.kotlin.App
import com.segunfamisa.kotlin.github.data.GithubManager
import com.segunfamisa.kotlin.github.ui.viewmodel.GithubUser
import com.segunfamisa.kotlin.samples.retrofit.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class GithubActivity : AppCompatActivity(), UserDelegateAdapter.onViewSelectedListener {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val githubAdapter = GithubAdapter(this)

    @Inject lateinit var repository: GithubManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: dagger inject
        App.githubComponent.inject(this)

        github_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = githubAdapter
        }

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
                                Log.e("connie", throwable.message)
                            })
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onItemSelected(user: GithubUser?) {
        Log.i("connie", "clicked github user!")
    }
}
