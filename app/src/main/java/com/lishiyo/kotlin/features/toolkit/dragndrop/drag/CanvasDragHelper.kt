package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.content.Context
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

    // set on the full scroll view (reblog tree + blockrows)
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

        override fun onDrag(ownerView: View, event: DragEvent): Boolean {
            if (event.localState !is BlockView) {
                Log.d(DEBUG_TAG, "onDrag! A drag event using a " + event.localState.javaClass.canonicalName + " was detected")
                return false
            }
            if (ownerView !is ScrollView) {
                Log.d(DEBUG_TAG, "onDrag! Not dragging on a ScrollView, this should be set on callback.scrollView")
                return false
            }

            val draggedView = event.localState as View

            return handleDrag(draggedView, ownerView, event)
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
            val rawEventY = dragEvent.y.toInt() + scrollOffset

            val hoverOnViewIdx = boundaryList.indexOfFirst({ rawEventY < it})
            val neighborViews = when (hoverOnViewIdx) {
                0, -1 -> INVALID_NEIGHBOR_VIEWS
                else -> Pair(hoverOnViewIdx - 1, hoverOnViewIdx)
            }

            return neighborViews
        }

        private fun getDropPosition(neighborViews: Pair<Int, Int>,
                                    boundaryList: List<Int>,
                                    event: DragEvent,
                                    scrollOffset: Int): Int {
            var dropPosition = POSITION_INVALID
            val rawEventY = event.y.toInt() + scrollOffset // actual distance from scroll view top edge

            val hoverOnViewIdx = neighborViews.first // view we are hovering in
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
        fun handleExternalSpacer(dropPosition: Int) {
            if (oldSpacer != null) {
                val currentSpacerPosition = callback.contentView.findChildPosition(oldSpacer!!)
                val shouldMoveSpacer = (dropPosition != POSITION_INVALID
                        && dropPosition != currentSpacerPosition // already at spacer
                        && dropPosition - 1 != currentSpacerPosition)  // spacer already included above me
//                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ old spacer position: $currentSpacerPosition vs dropPosition: $dropPosition ++ " +
//                        "shouldMove? $shouldMoveSpacer")
                if (shouldMoveSpacer) {
                    // remove old spacer from layout and add spacer at new position
                    oldSpacer = addExternalSpacer(clamp(dropPosition, currentSpacerPosition - 1, currentSpacerPosition + 1))
                }
            } else {
                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ no spacer yet! add spacer at $dropPosition")
                oldSpacer = addExternalSpacer(dropPosition)
            }
        }

        fun handleDrag(draggedView: View, ownerView: ScrollView, event: DragEvent): Boolean {
            val action = event.action

            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // TODO: unsubscribe from animation, close keyboard, set up animations
                    trashHelper.showTrash()

                    // store the current threshold with spacer included
                    val height = ownerView.height
                    val topTen = (height * SCROLL_THRESHOLD).toInt()
                    val bottomTen = (height - height * SCROLL_THRESHOLD).toInt()
                    scrollThreshold = Pair(topTen, bottomTen)

                    // reset blockrow thresholds list
                    boundaryList.clear()
                    boundaryList.addAll(getBoundaryList(ownerView)) // doesn't include spacer yet
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // TODO trash stuff
                    trashHelper.scaleTrash(event, ownerView)
                    if (trashHelper.isOnTrash(event, ownerView) && !trashMode) {
                        // wasn't on trash before but now on trash - remove spacer
                        trashMode = true
                        removeSpacers()
                        trashHelper.selectTrash(true)
                        return true
                    } else if (trashHelper.isOnTrash(event, ownerView)) {
                        // on trash and was on trash before
                        return true
                    } else if (!trashHelper.isOnTrash(event, ownerView) && trashMode) {
                        // not on trash
                        trashMode = false
                        trashHelper.selectTrash(false)
                    }

                    // not scrolling the entire scrollview atm - toggle spacers
                    if (!handleScroll(ownerView, event, scrollThreshold)) {
                        val boundaryListWithSpacer = getBoundaryList(ownerView) // top edges of all views including spacer
                        // <idx of view I am hovering on, idx of view below>
                        val neighborViews = getNeighborViews(boundaryListWithSpacer, event, ownerView.scrollY)
                        // view I am hovering on - could be either spacer or block row (only handle block rows)
                        val hoverOnView = getHoverOnView(callback.contentView, neighborViews)
                        if (hoverOnView is BlockRow) {
                            val blockRow = hoverOnView

                            // drop at either this block row's top or bottom
                            val dropPosition = getDropPosition(neighborViews, boundaryListWithSpacer, event, ownerView.scrollY)
                            if (!blockRow.canDropIn(draggedView as BlockView)) {
                                // blockrow can't accept draggedView - add external spacer either above or below

//                                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ dropPosition given spacer: $dropPosition")
                                handleExternalSpacer(dropPosition)
                            } else {
                                // blockrow can accept draggedView - spacer might go inside OR out

//                                Log.i(DEBUG_TAG, "ACTION_DRAG_LOCATION ++ internal blockRow's dropPosition: $blockRowDropPosition " +
//                                        "dropPosition: $dropPosition")
                                val blockRowDropPosition = blockRow.getDropPosition(event, callback.scrollView)
                                when (blockRowDropPosition) {
                                    BlockRow.DROP_POSITION_INVALID -> POSITION_INVALID
                                    BlockRow.DROP_POSITION_TOP -> handleExternalSpacer(neighborViews.first)
                                    BlockRow.DROP_POSITION_BOTTOM -> handleExternalSpacer(neighborViews.second)
                                    else -> {
                                        // drop position is internal - switch to vertical spacer inside the block row
                                        val spacer = oldSpacer ?: callback.spacer
                                        oldSpacer = blockRow.addInnerSpacer(spacer, blockRowDropPosition)
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
                        val boundaryListWithSpacer = getBoundaryList(ownerView) // top edges of all views including spacer
                        val neighborViews = getNeighborViews(boundaryListWithSpacer, event, ownerView.scrollY)
                        val hoverOnView = getHoverOnView(callback.contentView, neighborViews)
                        when (hoverOnView) {
                            is BlockRow -> {
                                val blockRow = hoverOnView
                                if (!blockRow.canDropIn(draggedView as BlockView)) {
                                    // drag block to above or below this BlockRow
                                    // need to take into account current spacer position if it's in there
                                    val boundaryListWithSpacer = getBoundaryList(ownerView) // top edges of all views including spacer
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

    private fun handleScroll(scrollView: ScrollView, event: DragEvent, scrollThreshold: Pair<Int, Int>): Boolean {
        val needToScroll: Boolean
        val eventYOnScreen = event.y
        val delta = when {
            (eventYOnScreen < scrollThreshold.first) -> (-MAX_DRAG_SCROLL_SPEED * smootherStep(scrollThreshold.first.toFloat(), 0f, event.y)).toInt()
            (eventYOnScreen > scrollThreshold.second) -> (MAX_DRAG_SCROLL_SPEED * smootherStep(scrollThreshold.second.toFloat(), scrollView.height.toFloat(), event.y)).toInt()
            else -> {
                0
            }
        }

        needToScroll = delta < 0 && scrollView.scrollY > 0
                || delta > 0 && (scrollView.height + scrollView.scrollY <= scrollView.getChildAt(0).height)
//        Log.d(DEBUG_TAG, "handleScroll! eventY: ${event.y} scrollY: ${scrollView.scrollY} ==> eventYOnScreen: $eventYOnScreen " +
//                "++ delta: $delta " + "needToScroll?" + " $needToScroll")

        scrollView.smoothScrollBy(0, delta)

        return needToScroll
    }

    // add external spacer (the horizontal one b/t blockrows)
    private fun addExternalSpacer(position: Int): View {
        removeSpacers() // removes either the external or internal one

        val spacer = callback.spacer
        val finalPosition = clamp(position, 0, callback.blockRows.size)
        callback.contentView.addView(spacer, finalPosition)

        // make it horizontal
        spacer.setBackgroundResource(R.drawable.canvas_spacer_background)
        val lp = spacer.layoutParams
        lp.height = spacerHeight
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        spacer.layoutParams = lp

        return spacer
    }

    private fun removeSpacers() {
        oldSpacer?.let {
            if (it.parent is ViewGroup) {
                (it.parent as ViewGroup).removeView(oldSpacer)
                oldSpacer = null
            }
        }
    }
}