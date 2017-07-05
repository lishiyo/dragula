package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.PerActivity
import com.lishiyo.kotlin.features.casualq.Constants.DEBUG_TAG
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.DroppableContainer
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
                        @CanvasSpacer val spacerProvider: Provider<View>) {
    private var blockRows = arrayListOf<DroppableContainer>()
    private lateinit var layout: LinearLayout
    private lateinit var scrollView: ObservableScrollView

    init {
        Log.d(DEBUG_TAG, "init CanvasLayoutHelper!")
        layout = activity.contentLayout
        scrollView = activity.scrollLayout
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