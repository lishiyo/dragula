package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.content.Context
import android.graphics.Rect
import android.support.v4.math.MathUtils.clamp
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.POSITION_INVALID
import com.lishiyo.kotlin.commons.extensions.findChildPosition
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.commons.extensions.smootherStep
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Real drag helper.
 *
 * Created by connieli on 7/1/17.
 */
class CanvasDragHelper(context: Context,
                       dragCallback: CanvasDragCallback,
                       @CanvasSpacer spacer: View) {

    private val SCROLL_THRESHOLD = 0.15f
    private val MAX_DRAG_SCROLL_SPEED = 20

    private val callback: CanvasDragCallback = dragCallback
    private val trashHelper: TrashHelper = TrashHelper(callback)

    // spacer logic
    private val spacerHeight: Int = context.getPixelSize(R.dimen.canvas_spacer_height)
    private var oldSpacer: View? = spacer
    // top 10% and bottom 10% of current scrollview height (scrollview wraps the blocks layout)
    private var scrollThreshold : Pair<Int, Int> = Pair(0, 0)

    // Drag listener set on each blockrow
    val blockRowDragListener = CanvasDragListener()
    // Drag listener set on the spacer
    val spacerDragListener = SpacerDragListener(callback, callback, spacer)

    companion object {
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
                    val scrollViewVisibleRect = Rect()
                    callback.scrollView.getGlobalVisibleRect(scrollViewVisibleRect)
                    val height = scrollViewVisibleRect.bottom - scrollViewVisibleRect.top // total visible height of scrollview
                    val threshold = height * SCROLL_THRESHOLD // 10%
                    val topCutoff = threshold.toInt() // 0-10% of visible (0-200)
                    val bottomCutoff = (height - threshold).toInt() // 90-100% of visible (2000-2200)
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
                    if (!handleScroll(callback.scrollView, blockRow, event, scrollThreshold)) {
                        if (!blockRow.canDropIn(draggedView as BlockView)) {
                            // blockrow can't accept draggedView - add external spacer either above or below
                            var hoverPosition = getExternalDropPositionWithSpacer(blockRow, event, oldSpacer) // drop at top or bottom
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
                            val dropPosition = blockRow.getDropPosition(event)
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
                            val dropIndex = getExternalDropPositionWithSpacer(blockRow, event, oldSpacer)
                            Log.d(DEBUG_TAG, "ACTION_DROP! drop index: $dropIndex")

                            // remove the block view from the block row, add a new blockrow at the dropIndex
                            callback.handleDrop(callback, event, draggedView, dropIndex, oldSpacer)
                        } else {
                            // drag block INTO this BlockRow
                            blockRow.handleDrop(callback, event, draggedView, blockRow.getDropPosition(event), oldSpacer)
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

        fun getExternalDropPositionWithSpacer(blockRow: BlockRow, event: DragEvent, currentSpacer: View?): Int {
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
            spacer.setOnDragListener(spacerDragListener)

            return spacer
        }

        // Scroll the full content layout
        private fun handleScroll(scrollView: ScrollView, blockRow: BlockRow, event: DragEvent, threshold: Pair<Int, Int>): Boolean {
            val blockRowVisibleRect = Rect()
            blockRow.getGlobalVisibleRect(blockRowVisibleRect)

            val needToScroll: Boolean // whether we need to scroll this scrollview
            val delta: Int

            val locationInWindow = IntArray(2)
            blockRow.getLocationInWindow(locationInWindow)
            val eventY = event.y // relative y within blockRow
            val blockRowY = locationInWindow[1] // y of blockrow's top edge, negative if above the top line
            val eventYOnScreen: Float = event.y + blockRowY // actual y of event from top line

            delta = when {
                (eventYOnScreen < threshold.first) -> (-MAX_DRAG_SCROLL_SPEED * smootherStep(threshold.first.toFloat(), 0f,
                        eventYOnScreen)).toInt()
                (eventYOnScreen > threshold.second) -> (MAX_DRAG_SCROLL_SPEED * smootherStep(
                        threshold.second.toFloat(), scrollView.height.toFloat(), eventYOnScreen)).toInt()
                else -> 0
            }

            needToScroll = delta < 0 && scrollView.scrollY > 0 // at top, and we've scroll down => scroll back up
                    || delta > 0 && (scrollView.height + scrollView.scrollY <= scrollView.getChildAt(0).height) // at bottom

//            Log.d(DEBUG_TAG, "handleScroll! eventY $eventY ++ blockRowLocationInWindow: $blockRowY for total eventYOnScreen $eventYOnScreen")
//            Log.d(DEBUG_TAG, "handleScroll! BLOCKROW globalRect: <" + blockRowVisibleRect.top + ", " + blockRowVisibleRect.bottom + ">" +
//                    " vs top: " + blockRow.top + " scrollY: " + blockRow.scrollY)
//            Log.d(DEBUG_TAG, "handleScroll finish ===== delta: $delta ==== needToScroll? $needToScroll")

            scrollView.smoothScrollBy(0, delta)

            return needToScroll
        }
    }

    private fun removeSpacers() {
        oldSpacer?.let {
            if (it.parent is ViewGroup) {
                it.setOnDragListener(null)
                (it.parent as ViewGroup).removeView(oldSpacer)
                oldSpacer = null
            }
        }
    }
}