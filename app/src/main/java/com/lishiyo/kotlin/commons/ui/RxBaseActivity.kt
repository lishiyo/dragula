package com.lishiyo.kotlin.commons.ui

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by connieli on 6/25/17.
 */
open class RxBaseActivity : AppCompatActivity() {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}