package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block

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

    fun initDragAndDrop()
}