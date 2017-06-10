package com.lishiyo.kotlin

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Entry point - root viewpager with tabs.
 *
 * Created by connieli on 5/28/17.
 */
class RootActivity : AppCompatActivity() {

    lateinit var rootPagerAdapter: RootPagerAdapter

    @BindView(R.id.viewpager) lateinit var viewpager: ViewPager
    @BindView(R.id.bottom_navigation) lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        viewpager.apply {
            rootPagerAdapter = RootPagerAdapter(supportFragmentManager)
            adapter = rootPagerAdapter
        }

        bottomNav.apply {
            setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_github -> viewpager.currentItem = 0
                    R.id.action_notes -> viewpager.currentItem = 1
                    R.id.action_wifi -> viewpager.currentItem = 2
                    else -> viewpager.currentItem = 0
                }
                true
            }
        }
    }
}
