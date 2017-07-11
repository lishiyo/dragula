package com.lishiyo.kotlin.features.dragndrop.drag

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.features.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.dragndrop.ui.ObservableScrollView
import com.lishiyo.kotlin.features.dragndrop.viewmodels.BlockView

/**
 * Interface for grabbing necessary views and notifying drag and drop events
 */
interface CanvasDragCallback : DropOwner {
    /**
     * External drop - drop a block view *out* of a block row.
     *
     * @param draggedView
     *      the block view being dragged out
     * @param dragFromView
     *      the view we are dragging the blockview from, usually a BlockRow
     * @param dropToPosition
     *      the index of where to drop in entire layout
     */
    fun onDragBlockOut(draggedView: View, dragFromView: View, dropToPosition: Int)

    /**
     * Internal drop - drop a block view *inside* a block row.
     *
     * @param draggedView
     *      the block view dragged
     * @param dragFromView
     *      the view we are dragging blockview from, usually a BlockRow
     * @param dropToBlockRow
     *
     */
    fun onDragBlockIn(draggedView: View, dragFromView: View, dropToBlockRow: BlockRow, internalDropPosition: Int)

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
    @CanvasSpacer
    val spacer: View

    /**
     * @return The [View] to use as the drop target
     */
    val trash: View

    /**
     * List of rows that can accept the drop.
     */
    val blockRows: MutableList<BlockRow>

    /**
     * Remove a blockview
     *
     * @param dragFromView
     * 	    the block row this view was dragged from
     * @param draggedView
     *      the block view to delete
     */
    fun removeDraggedView(dragFromView: View, draggedView: BlockView)
}