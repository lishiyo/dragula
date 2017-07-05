package com.lishiyo.kotlin.features.toolkit.dragndrop.ui

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.MaxOneBlockView
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.MaxThreeBlockView
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
    @BindView(R.id.max_one_block) lateinit var maxOneBlock: ImageView
    @BindView(R.id.max_three_block) lateinit var maxThreeBlock: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.block_picker_bar, this, true)
        orientation = HORIZONTAL
        ButterKnife.bind(this)

        initClickListeners()
    }

    internal fun initClickListeners() {
        maxOneBlock.setOnLongClickListener {
            val dragData = ClipData.newPlainText(
                    MaxOneBlockView::class.java.simpleName, // label
                    "max one" // text in the clip
            )
            setDragStart(it, dragData)
        }
        maxThreeBlock.setOnLongClickListener {
            val dragData = ClipData.newPlainText(
                    MaxThreeBlockView::class.java.simpleName, // label
                    "max three" // text in the clip
            )
            setDragStart(it, dragData)
        }
    }

    internal fun setDragStart(v: View, dragData: ClipData): Boolean {
        val shadowBuilder = DragShadowBuilder(v)

        // Start the drag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.startDragAndDrop(dragData, // data to be dragged
                    shadowBuilder, // drag shadow builder
                    v, // send info from dragged to target views in same activity (via `getLocalState`)
                    0 // flags
            )
        } else {
            v.startDrag(dragData, shadowBuilder, v, 0)
        }

        return true
    }

}