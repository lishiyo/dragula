package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.content.Context
import android.graphics.Rect
import android.support.v4.math.MathUtils.clamp
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.lishiyo.kotlin.commons.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.POSITION_INVALID
import com.lishiyo.kotlin.commons.extensions.findChildPosition
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.commons.extensions.smootherStep
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.dragula.R
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView

/**
 * Real drag helper.
 *
 * Created by connieli on 7/1/17.
 */
class CanvasDragHelper(context: Context,
                       dragCallback: CanvasDragCallback,
                       @CanvasSpacer spacer: View) {

    private val callback: CanvasDragCallback = dragCallback
    private val trashHelper: TrashHelper = TrashHelper(callback)

    // spacer logic
    private val spacerHeight: Int = context.getPixelSize(R.dimen.canvas_spacer_height)
    private var oldSpacer: View? = spacer
    // top 10% and bottom 10% of current scrollview height (scrollview wraps the blocks layout)
    private var scrollThreshold : Pair<Int, Int> = Pair(0, 0)
    // global visible rect of the scroll view
    private var scrollViewVisibleRect = Rect()

    // Drag listener set on each blockrow
//    val blockRowDragListener = CanvasDragListener()
    // Drag listener set on the spacer
//    val spacerDragListener = SpacerDragListener(callback, callback, spacer)

    val scrollViewDragListener = ScrollViewDragListener()

    companion object {
        val SCROLL_THRESHOLD = 0.15f
        val MAX_DRAG_SCROLL_SPEED = 20
        val INVALID_NEIGHBOR_VIEWS = Pair(POSITION_INVALID, 0)

        // factory constructor
        fun init(context: Context, dragCallback: CanvasDragCallback, spacer: View): CanvasDragHelper {
            return CanvasDragHelper(context, dragCallback, spacer)
        }

        /**
         * @return which block row this dragged view is coming from, if not block row then the parent view
         */
        fun getDragFromBlockRow(draggedView: View, callback: CanvasDragCallback): View {
            // TODO: get it from the clip data
            val draggedFromBlockRow = callback.blockRows.findLast {
                blockRow -> blockRow.indexOfChild(draggedView) != POSITION_INVALID
            }

            return draggedFromBlockRow ?: (draggedView.parent as View)
        }
    }

    init {
        callback.scrollView.setOnDragListener(scrollViewDragListener)
    }

    // Set on the full scroll layout (reblog tree + blockrows)
    inner class ScrollViewDragListener : View.OnDragListener {
        // flag for when the view enters or exits trash area
        // true if this is being dragged on the trash atm
        private var trashMode: Boolean = false
        private val boundaryList: MutableList<Int> = mutableListOf()

        override fun onDrag(view: View, event: DragEvent): Boolean {
            if (event.localState !is BlockView) {
                Log.d(DEBUG_TAG, "onDrag! A drag event using a " + event.localState.javaClass.canonicalName + " was detected")
                return false
            }

            val draggedView = event.localState as View

            return handleDrag(draggedView, view, event)
        }

        // list of top edges of each view
        private fun getBoundaryList(owner: View): MutableList<Int> {
            val boundaryList = (0..callback.contentView.childCount - 1)
                    .map { callback.contentView.getChildAt(it) }
                    .map { it.y.toInt() + callback.contentView.top } // top edge
                    .toMutableList()

            boundaryList.add(owner.scrollY + owner.height) // add bottom edge

            return boundaryList
        }

        private fun getHoverOnView(owner: ViewGroup,
                                   neighborViews: Pair<Int, Int>): View {
            // <view we are hovering on, view just below>
            val hoverOnViewIdx = neighborViews.first
            // could be spacer or block row
            return owner.getChildAt(hoverOnViewIdx)
        }

        private fun getNeighborViews(boundaryList: List<Int>,
                                     dragEvent: DragEvent,
                                     scrollOffset: Int): Pair<Int, Int> {
            var neighborViews: Pair<Int, Int> = INVALID_NEIGHBOR_VIEWS

            val rawEventY = dragEvent.y.toInt() + scrollOffset

            for (i in boundaryList.indices) {
                if (rawEventY < boundaryList[i]) {
                    if (i == 0) { // above the first item
                        neighborViews = INVALID_NEIGHBOR_VIEWS
                        break
                    } else {
                        neighborViews = Pair(i - 1, i) // view we are inside is i-1
                        break
                    }
                }
            }

            return neighborViews
        }

        private fun getDropPosition(neighborViews: Pair<Int, Int>,
                                    boundaryList: List<Int>,
                                    event: DragEvent,
                                    scrollOffset: Int): Int {
            var dropPosition = POSITION_INVALID
            val rawEventY = event.y.toInt() + scrollOffset // actual distance from scroll view top edge

            // which blockrow are we dragging from?
            val hoverOnViewIdx = neighborViews.first
            val hoverOnViewY = boundaryList[hoverOnViewIdx] // hover view's distance from scroll view top
            val justBelowViewIdx = neighborViews.second
            val justBelowViewY = boundaryList[justBelowViewIdx]
            val distFromHoverTop = rawEventY - hoverOnViewY
            val distFromHoverBottom = justBelowViewY - rawEventY

            if (distFromHoverTop < 0 || distFromHoverBottom < 0) {
                return dropPosition
            }
            // are we closer to the top or bottom?
            if (distFromHoverTop <= distFromHoverBottom) {
                dropPosition = hoverOnViewIdx
            } else {
                dropPosition = justBelowViewIdx
            }

            return dropPosition
        }

        // Add or move the current spacer to the drop position
        fun handleExternalSpacer(dropPosition: Int, neighborViews: Pair<Int, Int>) {
            if (oldSpacer != null) {
                val currentSpacerPosition = callback.contentView.findChildPosition(oldSpacer!!)
                val shouldMoveSpacer = (dropPosition != POSITION_INVALID
                        && dropPosition != currentSpacerPosition // already at spacer
                        && dropPosition - 1 != currentSpacerPosition)  // spacer already included above me
                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ old spacer position: $currentSpacerPosition vs dropPosition: $dropPosition ++ " +
                        "shouldMove? $shouldMoveSpacer ")
                if (shouldMoveSpacer) {
//                   dropPosition = clamp(hoverPosition, currentSpacerPosition - 1, currentSpacerPosition + 1)
                    // remove old spacer from layout and add spacer at new position
                    oldSpacer = addSpacer(dropPosition)
                }
            } else {
                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ no spacer yet! add spacer at $dropPosition")
                oldSpacer = addSpacer(dropPosition)
            }
        }

        fun handleDrag(draggedView: View, scrollLayout: View, event: DragEvent): Boolean {
            val action = event.action

            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // TODO: unsubscribe from animation, close keyboard, set up animations
                    trashHelper.showTrash()

                    // store the current threshold with spacer included
                    val height = scrollLayout.height
                    val topTen = (height * SCROLL_THRESHOLD).toInt()
                    val bottomTen = (height - height * SCROLL_THRESHOLD).toInt()
                    scrollThreshold = Pair(topTen, bottomTen)

                    // reset blockrow thresholds list
                    boundaryList.clear()
                    boundaryList.addAll(getBoundaryList(scrollLayout)) // doesn't include spacer yet
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // TODO trash stuff

                    // not scrolling the entire scrollview atm - toggle spacers
                    if (!handleScroll(callback.scrollView, event, scrollThreshold)) {
                        val boundaryListWithSpacer = getBoundaryList(scrollLayout) // top edges of all views including spacer
                        // <idx of view I am hovering on, idx of view below>
                        val neighborViews = getNeighborViews(boundaryListWithSpacer, event, scrollLayout.scrollY)
                        // view I am hovering on - could be either spacer or block row (only handle block rows)
                        val hoverOnView = getHoverOnView(callback.contentView, neighborViews)
                        if (hoverOnView is BlockRow) {
                            val blockRow = hoverOnView

                            // drop at either this block row's top or bottom
                            val dropPosition = getDropPosition(neighborViews, boundaryListWithSpacer, event, callback.scrollView.scrollY)
                            if (!blockRow.canDropIn(draggedView as BlockView)) {
                                // blockrow can't accept draggedView - add external spacer either above or below
//                                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ dropPosition given spacer: $dropPosition")

                                handleExternalSpacer(dropPosition, neighborViews)
                            } else {
                                // blockrow can accept draggedView - spacer might go inside OR out
                                val blockRowDropPosition = blockRow.getDropPosition(event, callback.scrollView)

//                                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ internal blockRow's dropPosition: $blockRowDropPosition " +
//                                        "dropPosition: $dropPosition")
                                when (blockRowDropPosition) {
                                    BlockRow.DROP_POSITION_INVALID -> POSITION_INVALID
                                    BlockRow.DROP_POSITION_TOP -> handleExternalSpacer(neighborViews.first, neighborViews)
                                    BlockRow.DROP_POSITION_BOTTOM -> handleExternalSpacer(neighborViews.second, neighborViews)
                                    else -> {
                                        // drop position is internal - switch to vertical spacer inside the block row
                                        val spacer = oldSpacer ?: callback.spacer
                                        oldSpacer = blockRow.addInnerSpacer(spacer, blockRowDropPosition, callback)
                                    }
                                }
                            }
                        }
                    }
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // view exited without dropping in here
//                    Log.d(DEBUG_TAG, "ACTION_DRAG_EXITED! trashMode? " + trashMode)
                    removeSpacers()
                }
                DragEvent.ACTION_DROP -> {
                    // view was dropped in here
//                    Log.d(DEBUG_TAG, "ACTION_DROP! trashMode? $trashMode")

                    if (trashMode) {
                        val draggedFromBlockRow = getDragFromBlockRow(draggedView, callback)
                        callback.removeDraggedView(draggedFromBlockRow, draggedView as BlockView)
                    } else {
                        val boundaryListWithSpacer = getBoundaryList(scrollLayout) // top edges of all views including spacer
                        val neighborViews = getNeighborViews(boundaryListWithSpacer, event, scrollLayout.scrollY)
                        val hoverOnView = getHoverOnView(callback.contentView, neighborViews)
                        when (hoverOnView) {
                            is BlockRow -> {
                                val blockRow = hoverOnView
                                if (!blockRow.canDropIn(draggedView as BlockView)) {
                                    // drag block to above or below this BlockRow
                                    // need to take into account current spacer position if it's in there
                                    val boundaryListWithSpacer = getBoundaryList(scrollLayout) // top edges of all views including spacer
                                    // figure out what block row we are hovering on, then whether to drop at top or bottom
                                    // <idx of view I am hovering on, idx of view below>
                                    val dropIndex = getDropPosition(neighborViews, boundaryListWithSpacer, event, callback.scrollView.scrollY)
                                    Log.d(DEBUG_TAG, "ACTION_DROP! dropped on BlockRow ++ drop index given spacer: $dropIndex")

                                    // remove the block view from the block row, add a new blockrow at the dropIndex
                                    callback.handleDrop(callback, event, draggedView, dropIndex, oldSpacer)
                                } else {
                                    // drag block INTO this BlockRow
                                    blockRow.handleDrop(callback, event, draggedView, blockRow.getDropPosition(event, callback.scrollView), oldSpacer)
                                }
                            }
                            else -> {
                                // view was dropped in this spacer - add it here
                                val dropToPosition = callback.getSpacerPosition(hoverOnView)
                                Log.d(DEBUG_TAG, "ACTION_DROP! == didn't drop on blockrow -- dropped on spacer at $dropToPosition")

                                if (dropToPosition != POSITION_INVALID) {
                                    callback.handleDrop(callback, event, draggedView, dropToPosition, hoverOnView)
                                }
                            }
                        }
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENDED! event result? ${event.result}")

                    removeSpacers()

                    if (event.result) {
                        // drop was successful
                        draggedView.alpha = 1f
                    } else {
                        // drop didn't succeed
                        draggedView.animate().alpha(1f).start()
                    }

                    // TODO: clearAnimations();

                    trashHelper.selectTrash(false)
                    trashHelper.animateOutTrash()
                }
            }
            return true
        }
    }

    // Set on each blockrow
    // This is the "drop zone" view, listening to the dragged view - x and y are RELATIVE to the blockrow
    inner class CanvasDragListener : View.OnDragListener {
        // flag for when the view enters or exits trash area
        // true if this is being dragged on the trash atm
        private var trashMode: Boolean = false

        override fun onDrag(view: View, event: DragEvent): Boolean {
            if (event.localState !is BlockView) {
                Log.d(DEBUG_TAG, "onDrag! A drag event using a " + event.localState.javaClass.canonicalName + " was detected")
                return false
            }

            if (view !is BlockRow) {
                Log.d(DEBUG_TAG, "trying to drop in non-BlockRow: " + view.javaClass.simpleName)
                return false
            }

            val draggedView = event.localState as View

            return handleDrag(draggedView, view, event)
        }

        fun handleDrag(draggedView: View, blockRow: BlockRow, event: DragEvent): Boolean {
            val action = event.action

            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // TODO: unsubscribe from animation, close keyboard, set up animations

                    trashHelper.showTrash()

                    // store the current threshold with spacer included
                    callback.scrollView.getGlobalVisibleRect(scrollViewVisibleRect)
                    val height = scrollViewVisibleRect.bottom - scrollViewVisibleRect.top // total visible height of scrollview
                    val threshold = height * SCROLL_THRESHOLD // 10%
                    val topCutoff = threshold.toInt() + scrollViewVisibleRect.top // 0-10% of visible (0-200)
                    val bottomCutoff = (scrollViewVisibleRect.bottom - threshold).toInt() // 90-100% of visible (2000-2200)
                    scrollThreshold = Pair(topCutoff, bottomCutoff)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // dragged view entered our view
//                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENTERED! trashMode? " + trashMode)
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // dragged view is moving around in our view area

                    trashHelper.scaleTrash(event, blockRow)
                    if (trashHelper.isOnTrash(event, blockRow) && !trashMode) {
                        // wasn't on trash before but now on trash - remove spacer
                        trashMode = true
                        removeSpacers()
                        trashHelper.selectTrash(true)
                        return true
                    } else if (trashHelper.isOnTrash(event, blockRow)) {
                        // on trash and was on trash before
                        return true
                    } else if (!trashHelper.isOnTrash(event, blockRow) && trashMode) {
                        // not on trash
                        trashMode = false
                        trashHelper.selectTrash(false)
                    }

                    // not scrolling the entire scrollview atm - toggle spacers
                    if (!blockRow.handleScroll(callback.scrollView, scrollViewVisibleRect, blockRow, event, scrollThreshold)) {
                        if (!blockRow.canDropIn(draggedView as BlockView)) {
                            // blockrow can't accept draggedView - add external spacer either above or below
                            var hoverPosition = getExternalDropPositionWithSpacer(blockRow, event) // drop at top or bottom
                            Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ spacer hoverPosition: $hoverPosition")
                            if (oldSpacer != null) {
                                val currentSpacerPosition = callback.contentView.findChildPosition(oldSpacer!!)
                                val shouldMoveSpacer = (hoverPosition != POSITION_INVALID
                                        && hoverPosition != currentSpacerPosition // current spacer is at hover block
                                        && currentSpacerPosition != hoverPosition - 1) // current spacer just above hover block
                                Log.i(DEBUG_TAG, "currentSpacerPosition: $currentSpacerPosition ++ shouldMove? $shouldMoveSpacer")
                                if (shouldMoveSpacer) {
                                    hoverPosition = clamp(hoverPosition, currentSpacerPosition - 1, currentSpacerPosition + 1)
                                    // remove old spacer from layout and add spacer at new position
                                    oldSpacer = addSpacer(hoverPosition)
                                }
                            } else {
                                oldSpacer = addSpacer(hoverPosition)
                            }
                        } else {
                            // blockrow can accept draggedView - spacer can go inside or out
                            val dropPosition = blockRow.getDropPosition(event, callback.scrollView)
                            val currentBlockRowIndex = callback.blockRows.indexOf(blockRow)
                            when (dropPosition) {
                                BlockRow.DROP_POSITION_INVALID -> POSITION_INVALID
                                BlockRow.DROP_POSITION_TOP -> oldSpacer = addSpacer(currentBlockRowIndex)
                                BlockRow.DROP_POSITION_BOTTOM -> oldSpacer = addSpacer(currentBlockRowIndex + 1)
                                else -> {
                                    // drop position is internal - switch to vertical spacer inside the block row
                                    val spacer = oldSpacer ?: callback.spacer
                                    oldSpacer = blockRow.addInnerSpacer(spacer, dropPosition, callback)
                                }
                            }
                        }

                    }

                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // view exited from this block row without dropping in here
//                    Log.d(DEBUG_TAG, "ACTION_DRAG_EXITED! trashMode? " + trashMode + " view: " + blockRow::class.java.simpleName)
                }
                DragEvent.ACTION_DROP -> {
                    // view was dropped in this block row!
//                    Log.d(DEBUG_TAG, "ACTION_DROP! trashMode? $trashMode")

                    if (trashMode) {
                        val draggedFromView = getDragFromBlockRow(draggedView, callback)
                        callback.removeDraggedView(draggedFromView, draggedView as BlockView)
                    } else {
                        if (!blockRow.canDropIn(draggedView as BlockView)) {
                            // drag block to above or below this BlockRow
                            // need to take into account current spacer position if it's in there
                            val dropIndex = getExternalDropPositionWithSpacer(blockRow, event)
                            Log.d(DEBUG_TAG, "ACTION_DROP! drop index: $dropIndex")

                            // remove the block view from the block row, add a new blockrow at the dropIndex
                            callback.handleDrop(callback, event, draggedView, dropIndex, oldSpacer)
                        } else {
                            // drag block INTO this BlockRow
                            blockRow.handleDrop(callback, event, draggedView, blockRow.getDropPosition(event, callback.scrollView), oldSpacer)
                        }
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
//                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENDED! event result? ${event.result}")

                    removeSpacers()

                    if (event.result) {
                        // drop was successful
                        draggedView.alpha = 1f
                    } else {
                        // drop didn't succeed
                        draggedView.animate().alpha(1f).start()
                    }

                    // TODO: clearAnimations();

                    trashHelper.selectTrash(false)
                    trashHelper.animateOutTrash()
                }
            }

            return true
        }

        fun getExternalDropPositionWithSpacer(blockRow: BlockRow, event: DragEvent): Int {
            var dropPosition = POSITION_INVALID
            // current position in view
            val blockRowIndex = callback.contentView.findChildPosition(blockRow)
            if (blockRowIndex == POSITION_INVALID) {
                Log.d(DEBUG_TAG, "getExternalDropPosition ++ blockRow not in layout!")
                return dropPosition
            }

            val cutoff = (blockRow.height / 2.0).toInt()
            if (event.y < cutoff) {
                dropPosition = blockRowIndex // on top of block row
            } else {
                dropPosition = blockRowIndex + 1 // on bottom of block row
            }

            return dropPosition
        }

        // get the drop position taking into account block rows only
        // only for external drops (above or below this block row)
//        fun getExternalDropPosition(blockRow: BlockRow, event: DragEvent): Int {
//            var dropPosition = POSITION_INVALID
//            // takes into account the blockrow index
//            val blockRowIndex = callback.blockRows.indexOf(blockRow)
//            if (blockRowIndex == POSITION_INVALID) {
//                Log.d(DEBUG_TAG, "getExternalDropPosition ++ blockRow not in layout!")
//                return dropPosition
//            }
//
//            val cutoff = (blockRow.y + blockRow.height / 2.0).toInt()
//            if (event.y < cutoff) {
//                dropPosition = blockRowIndex // on top of block row
//            } else {
//                dropPosition = blockRowIndex + 1 // on bottom of block row
//            }
//
//            return dropPosition
//        }

    }

    private fun handleScroll(scrollView: ScrollView, event: DragEvent, scrollThreshold: Pair<Int, Int>): Boolean {
        val needToScroll: Boolean
//        val eventYOnScreen = event.y - scrollView.scrollY
        val eventYOnScreen = event.y
        val delta: Int

//        delta = when {
//            (eventYOnScreen < scrollThreshold.first) -> (-MAX_DRAG_SCROLL_SPEED * smootherStep(
//                    scrollThreshold.first.toFloat(), // bottom edge
//                    scrollViewVisibleRect.top.toFloat(), // top edge (where we are moving towards)
//                    eventYOnScreen)).toInt() // current hover position on screen
//            (eventYOnScreen > scrollThreshold.second) -> (MAX_DRAG_SCROLL_SPEED * smootherStep(
//                    scrollThreshold.second.toFloat(), // top edge
//                    scrollViewVisibleRect.bottom.toFloat(), // bottom edge (where we are moving towards)
//                    eventYOnScreen)).toInt() // current hover position on screen
//            else -> 0
//        }

        if (eventYOnScreen < scrollThreshold.first) { // in top threshold
            delta = (-MAX_DRAG_SCROLL_SPEED * smootherStep(scrollThreshold.first.toFloat(), 0f, event.y)).toInt()
        } else if (eventYOnScreen > scrollThreshold.second) {
            delta = (MAX_DRAG_SCROLL_SPEED * smootherStep(scrollThreshold.second.toFloat(), scrollView.height.toFloat(), event.y)).toInt()
        } else {
            delta = 0
        }

        needToScroll = delta < 0 && scrollView.scrollY > 0
                || delta > 0 && (scrollView.height + scrollView.scrollY <= scrollView.getChildAt(0).height)
        Log.d(DEBUG_TAG, "handleScroll! eventY: ${event.y} scrollY: ${scrollView.scrollY} ==> eventYOnScreen: ${eventYOnScreen} " +
                "++ delta: $delta " + "needToScroll?" + " $needToScroll")

        scrollView.smoothScrollBy(0, delta)

        return needToScroll
    }

    // add external spacer (the horizontal one b/t blockrows)
    private fun addSpacer(position: Int): View {
        removeSpacers() // removes either the external or internal one

        val spacer = callback.spacer
        callback.contentView.addView(spacer, position)

        // make it horizontal
        spacer.setBackgroundResource(R.drawable.canvas_spacer_background)
        val lp = spacer.layoutParams
        lp.height = spacerHeight
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        spacer.layoutParams = lp

        // add the drag listener
//        spacer.setOnDragListener(spacerDragListener)

        return spacer
    }

    private fun removeSpacers() {
        oldSpacer?.let {
            if (it.parent is ViewGroup) {
//                it.setOnDragListener(null)
                (it.parent as ViewGroup).removeView(oldSpacer)
                oldSpacer = null
            }
        }
    }
}