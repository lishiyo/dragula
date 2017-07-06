package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
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

    // UI
    @BindView(R.id.max_one_block) lateinit var maxOneBlock: BlockView // TODO make these blockView
    @BindView(R.id.max_three_block) lateinit var maxThreeBlock: BlockView

    init {
        LayoutInflater.from(context).inflate(R.layout.block_picker_bar, this, true)
        orientation = HORIZONTAL
        ButterKnife.bind(this)

        initBlocks()
    }

    internal fun initBlocks() {
        maxOneBlock.initDragAndDrop()
        maxThreeBlock.initDragAndDrop()
    }
}