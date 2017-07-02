package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
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

    // successful drop - resize the view to shortest photo in row
    fun onDrop(successful: Boolean)

    // max num of this blockview type in a DroppableContainer
    fun limitInContainer() : Boolean
}
