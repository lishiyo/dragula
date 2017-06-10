package com.lishiyo.kotlin.features.github.ui

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
import com.lishiyo.kotlin.commons.ui.RxBaseFragment
import com.lishiyo.kotlin.features.github.data.GithubManager
import com.lishiyo.kotlin.features.github.ui.viewmodel.GithubUser
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by connieli on 5/28/17.
 */
class GithubFragment: RxBaseFragment(), UserDelegateAdapter.onViewSelectedListener {

    // Data
    @Inject lateinit var repository: GithubManager

    // UI
    @BindView(R.id.github_list) lateinit var list: RecyclerView
    private val githubAdapter = GithubAdapter(this)

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
        val view: View = inflater.inflate(R.layout.fragment_github, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
                            Log.e("connie", throwable.message)
                        })
        )
    }

    override fun onItemSelected(item: GithubUser?) {
        Log.i("connie", "clicked github user! ${item?.login}")
    }

}