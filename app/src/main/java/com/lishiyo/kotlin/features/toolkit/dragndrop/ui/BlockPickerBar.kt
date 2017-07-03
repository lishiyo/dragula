package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
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

    init {
        LayoutInflater.from(context).inflate(R.layout.block_picker_bar, this, true)
        orientation = VERTICAL
    }

}