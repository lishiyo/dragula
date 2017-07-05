package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.jakewharton.rxbinding2.view.RxView
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

        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//        params.height = resources.getDimensionPixelSize(R.dimen.block_view_height)
        layoutParams = params
        image.layoutParams = params

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

    private fun setLayoutParams() {

    }

    private fun getScreenWidth(): Int {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        return size.x
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

    }

    override fun limitPerContainer(): Int {
        return 1
    }
}