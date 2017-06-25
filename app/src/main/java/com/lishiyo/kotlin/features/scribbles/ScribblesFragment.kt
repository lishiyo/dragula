package com.lishiyo.kotlin.features.scribbles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.commons.ui.RxBaseFragment
import com.lishiyo.kotlin.features.scribbles.github.ui.GithubFragment
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Created by connieli on 6/24/17.
 */
class ScribblesFragment: RxBaseFragment() {


    // UI
    @BindView(R.id.github_btn) lateinit var githubBtn: Button

    companion object {
        fun newInstance(bundle: Bundle?): ScribblesFragment {
            val f = ScribblesFragment()
            f.arguments = bundle
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_scribbles, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    fun initClickListeners() {
        githubBtn.setOnClickListener {
            val githubFrag = GithubFragment.newInstance(null)
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, githubFrag, GithubFragment.FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit()
        }
    }
}