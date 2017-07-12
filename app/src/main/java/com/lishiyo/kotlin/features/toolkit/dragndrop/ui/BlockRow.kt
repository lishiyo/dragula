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
import com.lishiyo.kotlin.commons.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.checkRemoveParent
import com.lishiyo.kotlin.commons.extensions.findChildPosition
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.dragula.R
import com.lishiyo.kotlin.features.toolkit.dragndrop.drag.CanvasDragCallback
import com.lishiyo.kotlin.features.toolkit.dragndrop.drag.CanvasDragHelper.Companion.getDragFromBlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.drag.DropOwner
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView

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
) : LinearLayout(context, attrs, defStyle, defStyleRes), DropOwner {

    var rootView: ViewGroup = LayoutInflater.from(context).inflate(R.layout.droppable_container, this, true) as ViewGroup
    val blockViews = arrayListOf<BlockView>()

    companion object {
        val TAG: String = BlockRow::class.java::getSimpleName.toString()
        const val DROP_POSITION_TOP = -1000
        const val DROP_POSITION_BOTTOM = 1000
        const val DROP_POSITION_INVALID = -1
    }

    init {
        orientation = HORIZONTAL
        gravity = CENTER_VERTICAL
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, context.getPixelSize(R.dimen.block_view_height_large))
        params.topMargin = context.getPixelSize(R.dimen.block_row_margin)
        layoutParams = params
        setBackgroundColor(resources.getColor(R.color.material_grey_50))
    }

    override fun getSpacerPosition(spacer: View): Int {
        return rootView.findChildPosition(spacer)
    }

    override fun handleDrop(callback: CanvasDragCallback, event: DragEvent, draggedView: View, dropToPosition: Int, spacer: View?):
            Boolean {
        val draggedFromView = getDragFromBlockRow(draggedView, callback)
        val currentBlockRowIndex = callback.blockRows.indexOf(this)
        Log.d(DEBUG_TAG, "INTERNAL ++ ACTION_DROP ! dragging to blockRowIndex $currentBlockRowIndex with dropToPosition $dropToPosition")
        when (dropToPosition) {
            BlockRow.DROP_POSITION_INVALID -> Log.d(DEBUG_TAG, "dropping in invalid position in blockRow!")
            BlockRow.DROP_POSITION_TOP -> callback.onDragBlockOut(draggedView, draggedFromView, currentBlockRowIndex)
            BlockRow.DROP_POSITION_BOTTOM -> callback.onDragBlockOut(draggedView, draggedFromView,
                    currentBlockRowIndex + 1)
            else -> {
                // will go inside the block row
                callback.onDragBlockIn(draggedView, draggedFromView, this, dropToPosition)
            }
        }

        return true
    }

    // can this block view drop in here right now, given the current children?
    fun canDropIn(newBlockView: BlockView): Boolean {
        val limitAllowedInContainer = (blockViews.plus(newBlockView)).minBy { it.limitPerContainer() }?.limitPerContainer() ?: 0
        return blockViews.contains(newBlockView) || limitAllowedInContainer > blockViews.size
    }

    // add the vertical inner spacer
    fun addInnerSpacer(spacer: View, position: Int): View {
        checkRemoveParent(spacer)

        rootView.addView(spacer, position)

        spacer.setBackgroundResource(R.drawable.canvas_spacer_background_vertical)
        val lp = spacer.layoutParams
        lp.width = context.getPixelSize(R.dimen.canvas_spacer_height)
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        spacer.layoutParams = lp

        return spacer
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
        rootView.addView(newBlockView)
        newBlockView.initDragAndDrop()
    }

    fun addBlockViewAt(newBlockView: BlockView, position: Int) {
        checkRemoveParent(newBlockView as View)

        blockViews.add(position, newBlockView)
        rootView.addView(newBlockView, position)
        newBlockView.initDragAndDrop()
    }

    fun removeBlockView(blockView: BlockView) {
        val blockViewIndex = blockViews.indexOf(blockView)
        val removed = blockViews.remove(blockView)
        Log.d(TAG, "removeBlockView! $blockViewIndex in current ${blockViews.size} ++ removed? $removed")
        rootView.removeView(blockView as View)
    }

    // If drag listener is set on the BlockRow parent - need to convert coordinates
    fun getDropPosition(event: DragEvent, owner: View): Int {
        // shortcircuit the empty case
        if (blockViews.isEmpty()) {
            return 0
        }

        // iterate over and find first that matches
        val dropZones = createDropZones()
        val rawEventY = event.y + owner.scrollY
        val rawEventX = event.x + owner.scrollX
        val localX = (rawEventX - x).toInt()
        val localY = (rawEventY - y).toInt()
        for ((zone, position) in dropZones) {
            if (zone.contains(localX, localY)) {
                return position
            }
        }

        return DROP_POSITION_INVALID
    }

    private fun createDropZones(): MutableMap<Rect, Int> {
        return createLocalDropZones()
    }

    private fun createLocalDropZones(): MutableMap<Rect, Int> {
        val zones = mutableMapOf<Rect, Int>()
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
            zones.put(topHalfZone, DROP_POSITION_TOP)
            zones.put(bottomHalfZone, DROP_POSITION_BOTTOM)
        }

        return zones
    }
}
