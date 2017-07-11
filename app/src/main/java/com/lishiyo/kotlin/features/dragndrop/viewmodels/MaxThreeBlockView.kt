package com.lishiyo.kotlin.features.dragndrop.viewmodels

import android.content.ClipData
import android.content.Context
import android.support.annotation.DrawableRes
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
import com.lishiyo.kotlin.features.dragndrop.models.Block
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.Observable



/**
 * Corresponds to ImageBlockView.
 *
 * BlockView that can fit up to three to a {@link DroppableContainer{.
 */
class MaxThreeBlockView @JvmOverloads constructor(
        context: Context,
        val attrs: AttributeSet? = null,
        val defStyle: Int = 0,
        val defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes), BlockView {
    @BindView(R.id.image) lateinit var image: SimpleDraweeView

    private var block: Block? = null
    private var currentDrawableId: Int = R.drawable.block_3

    val bodyFocusObservable: Observable<out BlockView> by lazy {
        RxView.focusChanges(image)
                .filter({ isFocused -> isFocused })
                .map({ _ -> this })
    }

    companion object {
        val DRAWABLE_SET = setOf(R.drawable.pizza_queen, R.drawable.poop,
                R.drawable.gudetama, R.drawable.gudetama2, R.drawable.datboi, R.drawable.shibi,
                R.drawable.drag_ghost, R.drawable.narwhall)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.block_view, this, true)
        orientation = HORIZONTAL
        ButterKnife.bind(this)

        val params: LinearLayout.LayoutParams = if (layoutParams == null)
            LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, getDefaultWeight().toFloat()) else layoutParams as LayoutParams
        params.marginStart = context.getPixelSize(R.dimen.block_view_margin)
        params.marginEnd = context.getPixelSize(R.dimen.block_view_margin)
        layoutParams = params

        // resize to image intrinsics
//        layoutParams = image.drawable.matchScreenWidth(context as Activity, params)
//        image.setLayoutParams(layoutParams)

        setBackgroundColor(resources.getColor(R.color.material_deep_teal_500))

        setRandomImage()
    }

    override fun clone(context: Context): BlockView {
        val blockView = MaxThreeBlockView(context, attrs, defStyle, defStyleRes)
        blockView.setImage(currentDrawableId)
        return blockView
    }

    fun setRandomImage() {
        val randomDrawable = DRAWABLE_SET.elementAt((Math.random() * DRAWABLE_SET.size).toInt())
        setImage(randomDrawable)
    }

    fun setImage(@DrawableRes drawableId: Int) {
        currentDrawableId = drawableId
        val imageRequest = ImageRequestBuilder.newBuilderWithResourceId(currentDrawableId).build()
        image.setImageURI(imageRequest.sourceUri)
    }

    override fun initDragAndDrop() {
        setOnLongClickListener {
            val dragData = ClipData.newPlainText(
                    MaxOneBlockView::class.java.simpleName, // label
                    "max three" // text in the clip
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
        params.height = LayoutParams.MATCH_PARENT
        params.weight = getDefaultWeight().toFloat()
        params.marginStart = context.getPixelSize(R.dimen.block_view_margin)

        layoutParams = params
    }

    override fun limitPerContainer(): Int {
        return 3
    }

    override fun getDefaultWeight(): Int {
        return 1
    }
}