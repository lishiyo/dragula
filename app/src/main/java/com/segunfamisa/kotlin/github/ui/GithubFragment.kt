package com.segunfamisa.kotlin.github.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.segunfamisa.kotlin.App
import com.segunfamisa.kotlin.commons.extensions.inflate
import com.segunfamisa.kotlin.commons.ui.RxBaseFragment
import com.segunfamisa.kotlin.github.data.GithubManager
import com.segunfamisa.kotlin.github.ui.viewmodel.GithubUser
import com.segunfamisa.kotlin.samples.retrofit.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_github.*
import javax.inject.Inject

/**
 * Created by connieli on 5/28/17.
 */
class GithubFragment: RxBaseFragment(), UserDelegateAdapter.onViewSelectedListener {

    private val githubAdapter = GithubAdapter(this)

    @Inject lateinit var repository: GithubManager

    companion object {
        fun newInstance(bundle: Bundle?): GithubFragment {
            val f = GithubFragment()
            f.arguments = bundle
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.githubComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.activity_github)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        github_list.apply {
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
                            Log.e("connie", throwable.message)
                        })
        )
    }

    override fun onItemSelected(item: GithubUser?) {
        Log.i("connie", "clicked github user! ${item?.login}")
    }

}