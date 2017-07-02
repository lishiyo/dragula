package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView

/**
 * Base class for the drop zone rows.
 *
 * Created by connieli on 7/1/17.
 */
class DroppableContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    val blockViewsList = arrayListOf<BlockView>()

    init {
//        LayoutInflater.from(context).inflate(R.layout.view_custom_component, this, true)

        orientation = VERTICAL
    }

}
