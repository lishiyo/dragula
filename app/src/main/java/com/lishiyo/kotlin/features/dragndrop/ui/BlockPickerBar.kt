package com.lishiyo.kotlin.features.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.commons.extensions.childViews
import com.lishiyo.kotlin.features.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R


/**
 * Created by connieli on 7/2/17.
 */
class BlockPickerBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {
    @BindView(R.id.container) lateinit var root: ViewGroup
    init {
        LayoutInflater.from(context).inflate(R.layout.block_picker_bar, this, true)
        orientation = HORIZONTAL
        ButterKnife.bind(this)

        initBlocks()
    }

    internal fun initBlocks() {
        root.childViews.forEach({
            if (it is BlockView) {
                it.initDragAndDrop()
            }
        })
    }
}