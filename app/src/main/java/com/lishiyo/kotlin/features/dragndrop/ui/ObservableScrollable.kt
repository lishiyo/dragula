package com.lishiyo.kotlin.features.dragndrop.ui

/**
 * Created by connieli on 7/1/17.
 */

/**
 * Interface for observable scrollable, duh.
 */
interface ObservableScrollable {

    /**
     * Set the scroll changed callback.
     * @param callback
     *       the [OnScrollChangedCallback].
     */
    fun setOnScrollChangedCallback(callback: OnScrollChangedCallback)
}


/**
 * Interface for scroll changed.
 */
interface OnScrollChangedCallback {

    /**
     * Callback for when the scroll has changed.
     * @param l
     *      the horizontal scroll.
     * @param t
     *      the vertical scroll.
     */
    fun onScroll(l: Int, t: Int)

    /**
     * Callback for when a scroll has ended
     */
    fun onEndScroll()
}
