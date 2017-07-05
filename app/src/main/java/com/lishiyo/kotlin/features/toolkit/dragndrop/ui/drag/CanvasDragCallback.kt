package com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.ObservableScrollView

/**
 * Created by connieli on 7/5/17.
 */
/**
 * Interface for grabbing necessary views and notifying drag and drop events
 */
interface CanvasDragCallback {
    /**
     * notify the caller that the user dropped a view
     * @param dragPosition
     * 		The original position
     *
     * @param dropPosition
     * 		The new position
     */
    fun onSwap(dragPosition: Int, dropPosition: Int)

    /**
     * @return the [ScrollView] used for setting up the Drag and Drop operation, and scrolling when necessary
     */
    val scrollView: ObservableScrollView

    /**
     * @return The [ViewGroup] that is activity as the drop target.
     */
    val contentView: ViewGroup

    /**
     * @return A [View] to use as spacer while a view is dragged, but not dropped
     */
    val spacer: View

    /**
     * @return The [View] to use as the drop target
     */
    val trash: View

    /**
     * Remove a view
     * @param view
     * * 		The [View] to delete
     */
    fun deleteView(view: View)
}