package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.InnerSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.PerActivity
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.ObservableScrollView
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag.CanvasDragCallback
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag.CanvasDragHelper
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
                        @CanvasSpacer val spacerProvider: Provider<View>,
                        @InnerSpacer val innerSpacerProvider: Provider<View>)
    : CanvasDragCallback {

    private lateinit var canvasDragHelper: CanvasDragHelper

    override var scrollView: ObservableScrollView = activity.scrollLayout
    override val contentView: ViewGroup = activity.contentLayout // mLayout, this is temp_layout
    override val spacer: View = spacerProvider.get()
    override val innerSpacer: View = innerSpacerProvider.get()
    override val trash: View = activity.trash
    // keep track of current block rows which hold the blocks
    override val blockRows : MutableList<BlockRow> = arrayListOf()

    init {
        setupDragAndDrop()
    }

    // external drop - dropped outside a block row
    override fun onDragBlockOut(draggedView: View, dragFromBlockRow: BlockRow?, dropToPosition: Int) {
        Log.d(DEBUG_TAG, "onDragBlockOut ++ FROM blockRowIndex: $dragFromBlockRow TO: $dropToPosition")

        if (draggedView is BlockView) {
            // remove the draggedView from the dragFromBlockRow
            removeDraggedView(dragFromBlockRow, draggedView)

            // create a new blockrow with this view
            val newBlockRow = BlockRow(activity)
            newBlockRow.addBlockView(draggedView)
            addBlockRow(newBlockRow, dropToPosition)
            draggedView.onDrop(true)
        }
    }

    // internal drop - dropped inside a block row
    override fun onDragBlockIn(draggedView: View, dragFromBlockRow: BlockRow?, dropToBlockRow: BlockRow, internalDropPosition: Int) {
        val blockRowIndex = blockRows.indexOf(dropToBlockRow)
        Log.d(DEBUG_TAG, "onDragBlockIn ++ FROM blockRowIndex: $dragFromBlockRow TO blockRowIndex: $blockRowIndex at internalPos: $internalDropPosition")

        if (draggedView is BlockView) {
            // remove the draggedView from the dragFromBlockRow
            removeDraggedView(dragFromBlockRow, draggedView)

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
        // must be called AFTER activity's block rows have been set
        canvasDragHelper = CanvasDragHelper.init(activity, this, spacer, innerSpacer)
    }

    fun clearAndSetBlockRows(rows: List<BlockRow>) {
        blockRows.clear()
        contentView.removeAllViews()

        Observable.fromIterable(rows)
                .subscribe({
                    row -> addBlockRow(row, contentView.childCount)
                })
    }

    internal fun addBlockRow(row: BlockRow, position: Int): BlockRow {
        // add row to layout, initialize drop and drop on the row (add drag long click listener)
        contentView.addView(row, position)
        blockRows.add(position, row)

        row.initDragAndDrop(canvasDragHelper.dragListener)

        return row
    }

    private fun removeDraggedView(dragFromBlockRow: BlockRow?, draggedView: BlockView) {
        // remove the draggedView from the dragFromBlockRow
        dragFromBlockRow?.let {
            it.removeBlockView(draggedView)
            // delete the block row if it's now empty
            if (it.blockViews.isEmpty()) {
                deleteView(dragFromBlockRow)
            }
        }
    }
}