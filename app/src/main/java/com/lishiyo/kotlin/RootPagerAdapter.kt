package com.lishiyo.kotlin

import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.lishiyo.kotlin.github.ui.GithubFragment
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Created by connieli on 5/28/17.
 */
class RootPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    enum class Tab(val index: Int, @StringRes val titleResId: Int) {
        GITHUB(0, R.string.tab_github_label),
        NOTES(1, R.string.tab_notes_label),
        TUMBLR(2, R.string.tab_tumblr_label);

        val title: String = App.getAppContext().resources.getString(titleResId)
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment = when (position) {
        Tab.GITHUB.index -> GithubFragment.newInstance(null)
        Tab.NOTES.index -> GithubFragment.newInstance(null)
        else -> GithubFragment.newInstance(null)
    }

    override fun getCount(): Int {
        return Tab.values().size
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence = when (position) {
        Tab.GITHUB.index -> Tab.GITHUB.title
        Tab.NOTES.index -> Tab.NOTES.title
        Tab.TUMBLR.index -> Tab.TUMBLR.title
        else -> "no title"
    }
}