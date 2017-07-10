package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.POSITION_INVALID
import com.lishiyo.kotlin.commons.extensions.findChildPosition
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.PerActivity
import com.lishiyo.kotlin.features.toolkit.dragndrop.drag.CanvasDragCallback
import com.lishiyo.kotlin.features.toolkit.dragndrop.drag.CanvasDragHelper
import com.lishiyo.kotlin.features.toolkit.dragndrop.drag.DropOwner
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.ObservableScrollView
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Created by connieli on 7/1/17.
 */
@PerActivity
@Named("CanvasLayoutHelper")
class CanvasLayoutHelper
    @Inject constructor(val activity: DragNDropActivity,
                        val blockViewProviderMap: Map<Class<out Block>, @JvmSuppressWildcards Provider<BlockView>>,
                        @CanvasSpacer val spacerProvider: Provider<View>)
    : CanvasDragCallback, DropOwner {

    private lateinit var canvasDragHelper: CanvasDragHelper

    override var scrollView: ObservableScrollView = activity.scrollLayout
    override val contentView: ViewGroup = activity.contentLayout // mLayout, this is temp_layout
    override val spacer: View = spacerProvider.get()
    override val trash: View = activity.trash
    // keep track of current block rows which hold the blocks
    override val blockRows : MutableList<BlockRow> = arrayListOf()

    init {
        setupDragAndDrop()
    }

    override fun getSpacerPosition(spacer: View): Int {
        return contentView.findChildPosition(spacer)
    }

    override fun handleDrop(callback: CanvasDragCallback, event: DragEvent, draggedView: View, dropToPosition: Int, spacer: View?): Boolean {
        Log.d(DEBUG_TAG, "EXTERNAL ++ ACTION_DROP dropToPosition $dropToPosition")
        val draggedFromView = CanvasDragHelper.getDragFromBlockRow(draggedView, this)
        if (dropToPosition != POSITION_INVALID) {
            onDragBlockOut(draggedView, draggedFromView, dropToPosition)
        }

        return true
    }

    // external drop - dropped outside a block row
    override fun onDragBlockOut(draggedView: View, dragFromView: View, dropToPosition: Int) {
        Log.d(DEBUG_TAG, "onDragBlockOut ++ TO: $dropToPosition")

        if (draggedView is BlockView) {
            // remove the draggedView from the dragFromBlockRow
            removeDraggedView(dragFromView, draggedView)

            // create a new blockrow with this view
            val newBlockRow = BlockRow(activity)
            newBlockRow.addBlockView(draggedView)
            // add to the layout
            addBlockRow(newBlockRow, dropToPosition)
            draggedView.onDrop(true)
        }
    }

    // internal drop - dropped inside a block row
    override fun onDragBlockIn(draggedView: View, dragFromView: View, dropToBlockRow: BlockRow, internalDropPosition: Int) {
        Log.d(DEBUG_TAG, "onDragBlockIn ++ TO blockRowIndex: ${blockRows.indexOf(dropToBlockRow)} " +
                "at internalPos: $internalDropPosition")

        if (draggedView is BlockView) {
            // remove the draggedView from the dragFromBlockRow
            removeDraggedView(dragFromView, draggedView)

            // tell blockrow to insert draggedView at the internal position
            dropToBlockRow.addBlockViewAt(draggedView, internalDropPosition)
            draggedView.onDrop(true)
        }
    }

    override fun deleteView(view: View) {
        contentView.removeView(view)
        if (view is BlockRow) {
            blockRows.remove(view)
        }
    }

    fun setupDragAndDrop() {
        // TODO: should be called AFTER activity's block rows have been set
        canvasDragHelper = CanvasDragHelper.init(activity, this, spacer)
    }

    fun clearAndSetBlockRows(rows: List<BlockRow>) {
        blockRows.clear()
        contentView.removeAllViews()

        Observable.fromIterable(rows)
                .subscribe({
                    row -> addBlockRow(row, contentView.childCount)
                })
    }

    private fun addBlockRow(row: BlockRow, position: Int): BlockRow {
        // add row to layout, initialize drop and drop on the row (add drag long click listener)
        contentView.addView(row, position)
        if (position < blockRows.size) {
            blockRows.add(position, row)
        } else {
            // adding at very end
            blockRows.add(row)
        }

        row.initDragAndDrop(canvasDragHelper.blockRowDragListener)

        return row
    }

    private fun removeDraggedView(dragFromView: View, draggedView: BlockView) {
        // remove the draggedView from the dragFromBlockRow
        if (dragFromView is BlockRow) {
            dragFromView.removeBlockView(draggedView)
            // delete the block row if it's now empty
            if (dragFromView.blockViews.isEmpty()) {
                deleteView(dragFromView)
            }
        }
    }
}