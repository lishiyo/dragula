package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.Gravity.CENTER_VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Base class for the drop zone containers.
 *
 * Created by connieli on 7/1/17.
 */
class BlockRow @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {
    var rootView: ViewGroup
    val blockViews = arrayListOf<BlockView>()

    companion object {
        const val TOP_POSITION = -1000
        const val BOTTOM_POSITION = 1000
        const val INVALID_POSITION = -1
    }

    init {
        rootView = LayoutInflater.from(context).inflate(R.layout.droppable_container, this, true) as ViewGroup
        orientation = HORIZONTAL
        gravity = CENTER_VERTICAL
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, context.getPixelSize(R.dimen.block_view_height_large))
        params.topMargin = context.getPixelSize(R.dimen.block_row_margin)
        params.bottomMargin = context.getPixelSize(R.dimen.block_row_margin)
        layoutParams = params
        setBackgroundColor(resources.getColor(R.color.material_grey_50))
    }

    // can this block view drop in here right now, given the current children?
    fun canDropIn(newBlockView: BlockView): Boolean {
        val limitAllowedInContainer = blockViews.minBy { it.limitPerContainer() }?.limitPerContainer() ?: 0
        return limitAllowedInContainer > childCount
    }

    fun initDragAndDrop(dragListener: View.OnDragListener) {
        // set drag listener on the block row
        setOnDragListener(dragListener)

        // TODO: set long click listener on each current block views
    }

    fun setBlockViews(vararg newBlockViews: BlockView) {
        blockViews.clear()
        newBlockViews.forEach({
            addBlockView(it)
        })
    }

    fun addBlockView(newBlockView: BlockView) {
        checkRemoveParent(newBlockView as View)

        blockViews.add(newBlockView)
        rootView.addView(newBlockView as View)
        // TODO: set long click listener on the blockview
    }

    fun addBlockViewAt(newBlockView: BlockView, position: Int) {
        checkRemoveParent(newBlockView as View)

        blockViews.add(position, newBlockView)
        rootView.addView(newBlockView as View, position)
        // TODO: set long click listener on the blockview
    }

    fun removeBlockView(blockView: BlockView) {
        blockViews.remove(blockView)
        rootView.removeView(blockView as View)
    }

    fun checkRemoveParent(view: View) {
        if (view.parent is ViewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    fun getDropPosition(event: DragEvent): Int {
        // shortcircuit the empty case
        if (blockViews.isEmpty()) {
            return 0
        }

        // iterate over and find first that matches
        val dropZones = createDropZones()
        for ((zone, position) in dropZones) {
            if (zone.contains(event.x.toInt(), event.y.toInt())) {
                Log.d(DEBUG_TAG, "found zone! " + zone.toShortString())
                return position
            }
        }

        return INVALID_POSITION
    }

    fun createDropZones(): MutableMap<Rect, Int> {
        val zones = mutableMapOf<Rect, Int>()
        val blockRowWidth = width
        val blockRowHeight = height

        blockViews.map({ it -> it as View}).forEachIndexed { idx, blockView ->
            val blockViewWidth = blockView.width
            val blockViewHalf = (blockView.height / 2.0f).toInt()

            val firstThirdWidth = (blockViewWidth * (1/3f)).toInt()
            val offsetX = blockView.left
            val startOfFirstThird = offsetX + firstThirdWidth
            val firstThirdZone = Rect(offsetX, 0, startOfFirstThird, blockRowHeight)

            val lastThirdWidth = (blockViewWidth * (2/3f)).toInt()
            val startOfLastThird = offsetX + lastThirdWidth
            val lastThirdZone = Rect(startOfLastThird, 0, blockViewWidth + offsetX, blockRowHeight)

            val topHalfZone = Rect(startOfFirstThird, 0, startOfLastThird, blockViewHalf)
            val bottomHalfZone = Rect(startOfFirstThird, blockViewHalf, startOfLastThird, blockRowHeight)

            zones.put(firstThirdZone, idx)
            zones.put(lastThirdZone, idx + 1)
            zones.put(topHalfZone, TOP_POSITION)
            zones.put(bottomHalfZone, BOTTOM_POSITION)
        }

        return zones
    }
}
