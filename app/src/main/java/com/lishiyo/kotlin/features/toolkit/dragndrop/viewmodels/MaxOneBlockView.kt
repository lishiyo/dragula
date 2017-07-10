package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.jakewharton.rxbinding2.view.RxView
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.commons.extensions.setDragStart
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.Observable





/**
 * Corresponds to TextBlockView.
 *
 * BlockView that takes up full width of {@link DroppableContainer}.
 */
class MaxOneBlockView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes), BlockView {
    @BindView(R.id.image) lateinit var image: SimpleDraweeView

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

        // set aspect ratio
//        val imageViewParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//        imageViewParams.width = getScreenWidth()
//        val imageRatio = blockDrawable.intrinsicWidth / blockDrawable.intrinsicHeight as Float
//        layoutParams.height = (layoutParams.width / imageRatio).toInt()
//        image.setLayoutParams(layoutParams)

        setBackgroundColor(resources.getColor(R.color.material_red_A100))
        val imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.block_1).build()
        image.setImageURI(imageRequest.sourceUri)
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
        params.marginStart = context.getPixelSize(R.dimen.block_view_margin)
        params.marginEnd = context.getPixelSize(R.dimen.block_view_margin)

        layoutParams = params
    }

    override fun limitPerContainer(): Int {
        return 1
    }

    override fun getDefaultWeight(): Int {
        return 3
    }
}