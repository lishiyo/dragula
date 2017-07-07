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
import com.lishiyo.kotlin.commons.extensions.setDragStart
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.samples.retrofit.R
import io.reactivex.Observable



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
    @BindView(R.id.image) lateinit var image: SimpleDraweeView

    private var block: Block? = null

    val bodyFocusObservable: Observable<out BlockView> by lazy {
        RxView.focusChanges(image)
                .filter({ isFocused -> isFocused })
                .map({ _ -> this })
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.block_view, this, true)
        orientation = HORIZONTAL
        ButterKnife.bind(this)

        val params: LinearLayout.LayoutParams = if (layoutParams == null)
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) else layoutParams as LayoutParams
        layoutParams = params

        setBackgroundColor(resources.getColor(R.color.material_deep_teal_500))

        val imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.block_3).build()
        image.setImageURI(imageRequest.sourceUri)
    }

    override fun initDragAndDrop() {
        setOnLongClickListener {
            val dragData = ClipData.newPlainText(
                    MaxOneBlockView::class.java.simpleName, // label
                    "max one" // text in the clip
            )
            val shadowBuilder = View.DragShadowBuilder(this)
//        val shadowBuilder = CanvasImageShadowBuilder(v)

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
        // resize given max width
    }

    override fun limitPerContainer(): Int {
        return 3
    }

    override fun weight(): Int {
        return 1
    }
}