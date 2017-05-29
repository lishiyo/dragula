package com.lishiyo.kotlin.commons.ui

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by connieli on 5/28/17.
 */
open class RxBaseFragment : Fragment() {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}