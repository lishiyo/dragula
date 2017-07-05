package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
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
                        @CanvasSpacer val spacerProvider: Provider<View>)
    : CanvasDragCallback {
    private lateinit var canvasDragHelper: CanvasDragHelper

    override var scrollView: ObservableScrollView = activity.scrollLayout
    override val contentView: ViewGroup = activity.contentLayout // mLayout, this is temp_layout
    override val spacer: View = spacerProvider.get()
    override val trash: View = activity.trash
    // keep track of current block rows which hold the blocks
    override val blockRows : MutableList<BlockRow> = arrayListOf()

    override fun onSwap(dragPosition: Int, dropPosition: Int) {
        Log.d(DEBUG_TAG, "onSwap! drag: $dragPosition to drop in: $dropPosition")
        // get the blockview at dragPosition and remove it and its block
        // add that block to the drop position
        // add to current list of blocks
        val draggedView = contentView.getChildAt(dragPosition)
    }

    override fun deleteView(view: View) {
        contentView.removeView(view)
        if (view is BlockRow) {
            // remove from blocks
        }
    }


    init {
        Log.d(DEBUG_TAG, "init CanvasLayoutHelper!")

        setupDragAndDrop()
    }

    private fun setupDragAndDrop() {
        canvasDragHelper = CanvasDragHelper.init(activity, this)
    }

    fun clearAndSetBlockRows(rows: List<BlockRow>) {
        blockRows.clear()
        contentView.removeAllViews()

        Observable.fromIterable(blockRows)
                .subscribe({
                    row -> addBlockRow(row, contentView.childCount)
                })
    }

    internal fun addBlockRow(row: BlockRow, position: Int): BlockRow {
        // add to layout, initialize drop and drop on the row (add drag long click listener)
        contentView.addView(row, position)
        row.initDragAndDrop()

        return row
    }
}