package com.lishiyo.kotlin

import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.lishiyo.kotlin.features.casualq.ui.QuestionsFragment
import com.lishiyo.kotlin.features.scribbles.ScribblesFragment
import com.lishiyo.kotlin.features.toolkit.ToolkitFragment
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Created by connieli on 5/28/17.
 */
class RootPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    enum class Tab(val index: Int, @StringRes val titleResId: Int) {
        TOOLKIT(0, R.string.tab_toolkit_label),
        Q(1, R.string.tab_casual_q_label),
        SCRIBBLES(2, R.string.tab_scribbles_label);

        val title: String = App.getAppContext().resources.getString(titleResId)
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment = when (position) {
        Tab.TOOLKIT.index -> ToolkitFragment.newInstance(null)
        Tab.Q.index -> QuestionsFragment.newInstance(null)
        Tab.SCRIBBLES.index -> ScribblesFragment.newInstance(null)
        else -> ToolkitFragment.newInstance(null)
    }

    override fun getCount(): Int {
        return Tab.values().size
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence = when (position) {
        Tab.TOOLKIT.index -> Tab.TOOLKIT.title
        Tab.Q.index -> Tab.Q.title
        Tab.SCRIBBLES.index -> Tab.SCRIBBLES.title
        else -> "no title"
    }
}