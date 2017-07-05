package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.PerActivity
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.DroppableContainer
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

    override fun onSwap(dragPosition: Int, dropPosition: Int) {
        Log.d(DEBUG_TAG, "onSwap! drag: $dragPosition to drop in: $dropPosition")
        // get the blockview at dragPosition and remove it and its block
        // add that block to the drop position
        // add to current list of blocks
    }
    override var scrollView: ObservableScrollView = activity.scrollLayout
    override val contentView: ViewGroup = activity.contentLayout
    override val spacer: View
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val trash: View
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun deleteView(view: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var blockRows = arrayListOf<DroppableContainer>()
    private var layout: LinearLayout
    private lateinit var canvasDragHelper: CanvasDragHelper

    init {
        Log.d(DEBUG_TAG, "init CanvasLayoutHelper!")
        layout = activity.contentLayout
        scrollView = activity.scrollLayout

        setupDragAndDrop()
    }

    private fun setupDragAndDrop() {
        canvasDragHelper = CanvasDragHelper.init(activity, this)
    }

    fun clearAndSetBlockRows(rows: List<DroppableContainer>) {
        blockRows = rows as ArrayList<DroppableContainer>
        layout.removeAllViews()

        Observable.fromIterable(blockRows)
                .subscribe({
                    row -> addBlockRow(row, layout.childCount)
                })
    }

    internal fun addBlockRow(row: DroppableContainer, position: Int): DroppableContainer {
        layout.addView(row, position)
//        row.getFocusObservable()
//                .subscribe({ activity.setFocusedBlockView(it) })
        row.initDragAndDrop()

        return row
    }
}