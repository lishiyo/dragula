package com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
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

    @BindView(R.id.image) lateinit var image: ImageView

    private var block: Block? = null

    val bodyFocusObservable: Observable<out BlockView> by lazy {
        RxView.focusChanges(image)
                .filter({ isFocused -> isFocused })
                .map({ _ -> this })
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.block_view, this, true)
        orientation = HORIZONTAL
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//        params.height = resources.getDimensionPixelSize(R.dimen.block_view_height)
        layoutParams = params

        ButterKnife.bind(this)

        setBackgroundColor(resources.getColor(R.color.material_red_A100))
        image.setImageDrawable(resources.getDrawable(R.drawable.block_1))
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