package com.segunfamisa.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.segunfamisa.kotlin.samples.retrofit.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Entry point - root viewpager with tabs.
 *
 * Created by connieli on 5/28/17.
 */
class RootActivity : AppCompatActivity() {

    lateinit var rootPagerAdapter: RootPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewpager.apply {
            rootPagerAdapter = RootPagerAdapter(supportFragmentManager)
            adapter = rootPagerAdapter
        }

        bottom_navigation.apply {
            setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_github -> viewpager.currentItem = 0
                    R.id.action_notes -> viewpager.currentItem = 1
                    R.id.action_tumblr -> viewpager.currentItem = 2
                    else -> viewpager.currentItem = 0
                }
                true
            }
        }
    }
}
