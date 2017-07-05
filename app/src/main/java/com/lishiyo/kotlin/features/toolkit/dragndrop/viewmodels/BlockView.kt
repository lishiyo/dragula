package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import io.reactivex.Observable

/**
 * Created by connieli on 7/1/17.
 */
interface BlockView {
    companion object {
        // alpha to apply to a view once it is dragged
        val DRAG_ALPHA = 0.13f
    }

    fun setBlock(block: Block)

    fun getBlock(): Block?

    fun getFocusObservable(): Observable<out BlockView>

    // successful drop - resize the view to max width (1/2 or 1/3)
    fun onDrop(successful: Boolean)

    // max num of this blockview type in a DroppableContainer
    fun limitPerContainer(): Int
}