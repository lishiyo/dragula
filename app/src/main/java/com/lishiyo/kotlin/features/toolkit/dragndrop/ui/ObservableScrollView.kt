package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

/**
 * @author Cyril Mottier with modifications from Manuel Peinado
 */
class ObservableScrollView : ScrollView, ObservableScrollable {
    private var mOnScrollChangedListener: OnScrollChangedCallback? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener!!.onScroll(l, t)
        }
    }

    override fun setOnScrollChangedCallback(callback: OnScrollChangedCallback) {
        mOnScrollChangedListener = callback
    }
}