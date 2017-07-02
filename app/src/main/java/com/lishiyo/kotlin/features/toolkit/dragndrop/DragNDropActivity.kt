package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.commons.ui.RxBaseActivity
import com.lishiyo.kotlin.di.dragndrop.DaggerDragNDropComponent
import com.lishiyo.kotlin.di.dragndrop.DragNDropModule
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.ObservableScrollView
import com.lishiyo.kotlin.samples.retrofit.R
import javax.inject.Inject

/**
 * Created by connieli on 7/1/17.
 */
class DragNDropActivity : RxBaseActivity() {

    // root layout
    @BindView(R.id.canvas_layout) lateinit var root: ViewGroup
    // full scrollable view (blocks + reblog tree)
    @BindView(R.id.canvas_scrollview) lateinit var scrollLayout: ObservableScrollView
    // contains the blocks
    @BindView(R.id.temp_layout) lateinit var contentLayout: LinearLayout
    // bottom bar to select current block
    @BindView(R.id.block_picker_toolbar) lateinit var blockPickerBar: LinearLayout
    // trashcan icon
    @BindView(R.id.canvas_trash) lateinit var trash: ImageView

    // DAGGER
    @Inject lateinit var canvasLayoutHelper: CanvasLayoutHelper

    companion object {
        fun createIntent(context: Context, bundle: Bundle?): Intent {
            val intent = Intent(context, DragNDropActivity::class.java)
            bundle?.let { intent.putExtras(it) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dragndrop)
        ButterKnife.bind(this)

        injectDependencies()
    }

    internal fun injectDependencies() {
        val dragNDropComponent = DaggerDragNDropComponent.builder()
                .dragNDropModule(DragNDropModule(this))
                .build()
        dragNDropComponent.inject(this)
    }
}
