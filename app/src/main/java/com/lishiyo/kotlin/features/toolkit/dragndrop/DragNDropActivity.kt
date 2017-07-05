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
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.MaxOneBlock
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.MaxThreeBlock
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.ObservableScrollView
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by connieli on 7/1/17.
 */
class DragNDropActivity : RxBaseActivity() {

    // root layout
    @BindView(R.id.canvas_layout) lateinit var root: ViewGroup
    // full scrollable view (block rows + reblog tree)
    @BindView(R.id.canvas_scrollview) lateinit var scrollLayout: ObservableScrollView
    // contains the block rows
    @BindView(R.id.temp_layout) lateinit var contentLayout: LinearLayout
    // bottom bar to select current block
    @BindView(R.id.block_picker_bar) lateinit var blockPickerBar: LinearLayout
    // trashcan icon
    @BindView(R.id.canvas_trash) lateinit var trash: ImageView

    // DAGGER
    @Inject lateinit var layoutHelper: CanvasLayoutHelper
    @Inject lateinit var blockViewProviderMap: Map<Class<out Block>, @JvmSuppressWildcards Provider<BlockView>>

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

        initContentLayout()
    }

    private fun initContentLayout() {
        // add initial rows - first with a MaxOne, second with a MaxThree
        layoutHelper.clearAndSetBlockRows(createInitialBlockRows())
    }

    private fun createInitialBlockRows(): List<BlockRow> {
        val initialRows = mutableListOf<BlockRow>()
        initialRows.add(createMaxOneBlockRow())
        initialRows.add(createMaxThreeBlockRow())
        return initialRows
    }

    private fun createMaxOneBlockRow(): BlockRow {
        // create the block, set to a block view
        val textBlock = MaxOneBlock()
        val blockView = blockViewProviderMap[MaxOneBlock::class.java]?.get()!!
        blockView.setBlock(textBlock)

        // create the first block row with MaxOne
        val firstBlockRow = BlockRow(this)
        firstBlockRow.setBlockViews(blockView)

        return firstBlockRow
    }

    private fun createMaxThreeBlockRow(): BlockRow {
        // create the block, set to a block view
        val imageBlock = MaxThreeBlock()
        val blockView = blockViewProviderMap[MaxThreeBlock::class.java]?.get()!!
        blockView.setBlock(imageBlock)

        // create the second block row with MaxThree
        val firstBlockRow = BlockRow(this)
        firstBlockRow.setBlockViews(blockView)

        return firstBlockRow
    }

    private fun injectDependencies() {
        val dragNDropComponent = DaggerDragNDropComponent.builder()
                .dragNDropModule(DragNDropModule(this))
                .build()
        dragNDropComponent.inject(this)
    }

}
