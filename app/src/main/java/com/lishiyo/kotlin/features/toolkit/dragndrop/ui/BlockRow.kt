package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER_VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

        // set long click listener on each of the block views
    }

    fun setBlockViews(vararg newBlockViews: BlockView) {
        blockViews.clear()
        newBlockViews.forEach({
            addBlockView(it)
        })
    }

    fun addBlockView(newBlockView: BlockView) {
        blockViews.add(newBlockView)
        rootView.addView(newBlockView as View)
    }
}
