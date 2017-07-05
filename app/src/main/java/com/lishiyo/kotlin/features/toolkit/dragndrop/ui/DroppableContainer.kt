package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER_VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Base class for the drop zone containers.
 *
 * Created by connieli on 7/1/17.
 */
class DroppableContainer @JvmOverloads constructor(
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
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//        params.height = resources.getDimensionPixelSize(R.dimen.block_view_height)
        layoutParams = params
        setBackgroundColor(resources.getColor(R.color.material_grey_50))

    }

    fun initDragAndDrop() {

    }

    fun setBlockViews(vararg newBlockViews: BlockView) {
        blockViews.clear()
        blockViews.addAll(newBlockViews)
        blockViews.forEach({ rootView.addView(it as View) })
    }

    fun addBlockView(newBlockView: BlockView) {
        blockViews.add(newBlockView)
        rootView.addView(newBlockView as View)
    }
}
