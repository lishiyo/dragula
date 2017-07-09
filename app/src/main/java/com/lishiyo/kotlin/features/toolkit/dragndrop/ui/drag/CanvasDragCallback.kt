package com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.ObservableScrollView

/**
 * Created by connieli on 7/5/17.
 */
/**
 * Interface for grabbing necessary views and notifying drag and drop events
 */
interface CanvasDragCallback {
    /**
     * Drop a block view *out* of a block row (need to add new block row).
     *
     * @param draggedView
     *      the block view being dragged out
     * @param dragFromBlockRow
     *      the index of the block row we are dragging view from, or {@link POSITION_INVALID} if not dragging from a block row
     * @param dropToPosition
     *      the index of where to drop in entire layout
     */
    fun onDragBlockOut(draggedView: View, dragFromBlockRow: BlockRow?, dropToPosition: Int)

    // can drop inside the block, or maybe out
    fun onDragBlockIn(draggedView: View, dragFromBlockRow: BlockRow?, dropToBlockRow: BlockRow, internalDropPosition: Int)

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

//    /**
//     * @return The inner spacer inside block rows
//     */
//    @InnerSpacer
//    val innerSpacer: View

    /**
     * @return The [View] to use as the drop target
     */
    val trash: View

    /**
     * List of rows that can accept the drop.
     */
    val blockRows: MutableList<BlockRow>

    /**
     * Remove a view
     * @param view
     * * 		The [View] to delete
     */
    fun deleteView(view: View)
}