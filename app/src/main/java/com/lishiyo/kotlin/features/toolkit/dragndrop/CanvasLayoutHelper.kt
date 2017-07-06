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

    override fun onDragBlockOut(draggedView: View, dragFromBlockRowIndex: Int, dropToPosition: Int) {
        Log.d(DEBUG_TAG, "onDragBlockOut ++ FROM blockRowIndex: $dragFromBlockRowIndex TO: $dropToPosition")
    }

    override fun onDragBlockInto(draggedView: View, dragFromBlockRowIndex: Int, dropToBlockRowIndex: Int) {
        Log.d(DEBUG_TAG, "onDragBlockInto ++ FROM blockRowIndex: $dragFromBlockRowIndex TO blockRowIndex: $dropToBlockRowIndex")
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

    fun setupDragAndDrop() {
        // must be called AFTER activity's block rows have been set
        canvasDragHelper = CanvasDragHelper.init(activity, this)
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
        // add to layout, initialize drop and drop on the row (add drag long click listener)
        contentView.addView(row, position)
        blockRows.add(row)

        row.initDragAndDrop(canvasDragHelper.dragListener)

        return row
    }
}