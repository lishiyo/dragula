package com.lishiyo.kotlin.features.dragndrop.viewmodels

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.view.SimpleDraweeView
import com.jakewharton.rxbinding2.view.RxView
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.commons.extensions.setDragStart
import com.lishiyo.kotlin.features.dragndrop.models.Block
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.Observable


/**
 * Corresponds to TextBlockView.
 *
 * BlockView that takes up full width of {@link DroppableContainer}.
 */
class MaxOneBlockView @JvmOverloads constructor(
        context: Context,
        val attrs: AttributeSet? = null,
        val defStyle: Int = 0,
        val defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes), BlockView {
    @BindView(R.id.image) lateinit var image: SimpleDraweeView
    @BindView(R.id.text_block_text) lateinit var textView: TextView

    private var block: Block? = null

    val bodyFocusObservable: Observable<out BlockView> by lazy {
        RxView.focusChanges(image)
                .filter({ isFocused -> isFocused })
                .map({ _ -> this })
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.block_view, this, true)
        ButterKnife.bind(this)
        orientation = HORIZONTAL

        val params: LinearLayout.LayoutParams = if (layoutParams == null)
            LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, getDefaultWeight().toFloat()) else layoutParams as LayoutParams
        params.marginStart = context.getPixelSize(R.dimen.block_view_margin)
        params.marginEnd = context.getPixelSize(R.dimen.block_view_margin)
        layoutParams = params

        setBackgroundColor(resources.getColor(R.color.material_red_A100))

        // switch to text-only
        image.visibility = GONE
        textView.visibility = View.VISIBLE
        textView.setText(R.string.label_textblock)
    }

    override fun clone(context: Context): BlockView {
        return MaxOneBlockView(context, attrs, defStyle, defStyleRes)
    }

    override fun initDragAndDrop() {
        setOnLongClickListener {
            // TODO: add info on which blockrow we are in, if any
            val dragData = ClipData.newPlainText(
                    MaxOneBlockView::class.java.simpleName, // label
                    "max one" // text in the clip
            )
            val shadowBuilder = View.DragShadowBuilder(this)

            it.setDragStart(dragData, shadowBuilder)
        }
    }

    override fun getFocusObservable(): Observable<out BlockView> {
        return bodyFocusObservable
    }

    override fun setBlock(block: Block) {
        this.block = block
    }

    override fun getBlock(): Block? {
        return block
    }

    override fun onDrop(successful: Boolean) {
        // reset params to fill to weight
        val params: LinearLayout.LayoutParams = if (layoutParams == null)
            LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, getDefaultWeight().toFloat()) else layoutParams as LayoutParams
        params.width = 0
        params.weight = getDefaultWeight().toFloat()
        params.height = LayoutParams.MATCH_PARENT

        layoutParams = params
    }

    override fun limitPerContainer(): Int {
        return 1
    }

    override fun getDefaultWeight(): Int {
        return 3
    }
}