package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block

/**
 * Created by connieli on 7/1/17.
 */
/**
 * Corresponds to ImageBlockView.
 *
 * BlockView that can fit up to three to a {@link DroppableContainer{.
 */
class MaxThreeBlockView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes), BlockView {
    override fun setBlock(block: Block) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBlock(): Block? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initDragAndDrop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDrop(successful: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun limitInContainer(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}