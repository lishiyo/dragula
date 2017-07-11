package com.lishiyo.kotlin.features.dragndrop.viewmodels

import android.content.Context
import android.support.annotation.IntRange
import com.lishiyo.kotlin.features.dragndrop.models.Block
import io.reactivex.Observable

/**
 * Created by connieli on 7/1/17.
 */
interface BlockView {
    companion object {
        // alpha to apply to a view once it is dragged
        val DRAG_ALPHA = 0.13f
    }

    // set the long click listener to initiate drag
    fun initDragAndDrop()

    fun setBlock(block: Block)

    fun getBlock(): Block?

    fun getFocusObservable(): Observable<out BlockView>

    // make a clone of this view
    fun clone(context: Context): BlockView

    // successful drop - resize the view to max width (1/2 or 1/3)
    fun onDrop(successful: Boolean)

    // max num of this blockview type in a DroppableContainer
    fun limitPerContainer(): Int

    // getDefaultWeight per container (1-3)
    @IntRange(from = 1, to = 3)
    fun getDefaultWeight(): Int
}
