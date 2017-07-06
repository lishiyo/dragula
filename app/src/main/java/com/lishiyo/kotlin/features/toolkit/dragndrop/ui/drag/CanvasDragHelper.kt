package com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag

import android.content.Context
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
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Real drag helper.
 *
 * Created by connieli on 7/1/17.
 */
class CanvasDragHelper(context: Context, dragCallback: CanvasDragCallback) {
    private val INVALID = -1
    private val SCROLL_THRESHOLD = 0.1f
    private val MAX_DRAG_SCROLL_SPEED = 16

    private val callback: CanvasDragCallback = dragCallback

    // spacer logic
    private val spacerHeight: Int = context.getPixelSize(R.dimen.canvas_spacer_height)
    private var oldSpacer: View? = null
    // top 10% and bottom 10% of current scrollview height (scrollview wraps the blocks layout)
    private var scrollThreshold : Pair<Int, Int> = Pair(0, 0)

    val dragListener = CanvasDragListener()

    companion object {
        // factory constructor
        fun init(context: Context, dragCallback: CanvasDragCallback): CanvasDragHelper {
            return CanvasDragHelper(context, dragCallback)
        }
    }

    init {
        // callback.scrollView.setOnDragListener(dragListener)
    }

    // this is the "drop zone" view, listening to the dragged view
    inner class CanvasDragListener : View.OnDragListener {
        // flag for when the view enters or exits trash area
        // true if this is being dragged on the trash atm
        private val trashMode: Boolean = false

        // only add spacer to top or bottom of BlockRow, can't drop internally
        fun handleExternalDrag(draggedView: View, blockRow: BlockRow, event: DragEvent): Boolean {
//            Log.d(DEBUG_TAG, "can't drop in this BlockRow! reached limit, only check top and bottom")

            val action = event.action

            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(DEBUG_TAG, "ACTION_DRAG_STARTED! trashMode? " + trashMode + " view: " + blockRow.id)
                    // unsubscribe from animation, close keyboard
                    // set up animations
                    // make trashcan visible

                    // reset scroll threshold to current full scroll layout height
                    val height = callback.scrollView.height
                    val topTen = (height * SCROLL_THRESHOLD).toInt()
                    val bottomTen = (height - height * SCROLL_THRESHOLD).toInt()
                    scrollThreshold = Pair(topTen, bottomTen)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // dragged view entered our view
                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENTERED! trashMode? " + trashMode + " view: " + blockRow.id)
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // dragged view is moving around in our view area
//                    Log.d(DEBUG_TAG, "ACTION_DRAG_LOCATION! trashMode? " + trashMode + " view: " + blockRow.id)

                    // scale trash, remove spacer and select trash if on it

                    // not scrolling the entire scrollview atm, toggle spacers
                    if (!handleScroll(callback.scrollView, event, scrollThreshold)) {
                        // current hover position in entire layout
                        var hoverPosition = getExternalDropPosition(blockRow, event) // drop at top or bottom
                        Log.i(DEBUG_TAG, "hoverPosition: $hoverPosition")
                        if (oldSpacer != null) {
                            // current spacer position in entire layout
                            val currentSpacerPosition = callback.contentView.findChildPosition(oldSpacer as View)
                            val shouldMoveSpacer = (hoverPosition != POSITION_INVALID && hoverPosition != currentSpacerPosition)
                            Log.i(DEBUG_TAG, "currentSpacerPosition: $currentSpacerPosition ++ shouldMove? $shouldMoveSpacer")
                            if (shouldMoveSpacer) {
//                                hoverPosition = clamp(hoverPosition, currentSpacerPosition - 1, currentSpacerPosition + 1)
                                // remove old spacer from layout and add spacer at new position
                                oldSpacer = addSpacer(hoverPosition)
                            }

                        } else {
                            oldSpacer = addSpacer(hoverPosition)
                        }
                    }

                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // dragged view exited from our view area
                    Log.d(DEBUG_TAG, "ACTION_DRAG_EXITED! trashMode? " + trashMode + " view: " + blockRow.id)

                }
                DragEvent.ACTION_DROP -> {
                    // view was dropped in here!
//                    Log.d(DEBUG_TAG, "ACTION_DROP! trashMode? " + trashMode + " view: " + blockRow.id)

                    if (trashMode) {
                        // TODO: delete the dragged view

                    } else {
                        val draggedFromBlockRowIndex = getDragFromBlockRowPosition(draggedView)
                        val dropIndex = getExternalDropPosition(blockRow, event) // above or below this view we're dropping on
                        Log.d(DEBUG_TAG, "ACTION_DROP! draggedFromBlockRow: $draggedFromBlockRowIndex dropIndex: $dropIndex")

                        callback.onDragBlockOut(draggedView as View, draggedFromBlockRowIndex, dropIndex)
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    // drag and drop flow finished, didn't drop in here
                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENDED! trashMode? " + trashMode + " view: " + blockRow.id)

                    removeSpacer()

                    if (event.result) {
                        // drop was successful
                        draggedView.alpha = 1f
                    } else {
                        // drop didn't succeed
                        draggedView.animate().alpha(1f).start()
                    }
                    // clearAnimations();
                    // animate out the trash
                }
                else -> {
                    Log.d(DEBUG_TAG, "fell to default: " + trashMode + " view: " + blockRow.id)
                }
            }

            return true
        }

        // index of block row we are dragging a view from
        // does NOT include spacer
        fun getDragFromBlockRowPosition(draggedView: View): Int {
            // find which block row this drag is coming from
            // TODO: get it from the clip data
            val draggedFromBlockRow = callback.blockRows.findLast {
                blockRow -> blockRow.indexOfChild(draggedView) != POSITION_INVALID
            }
            return if (draggedFromBlockRow == null) POSITION_INVALID else callback.contentView.findChildPosition(draggedFromBlockRow)
        }

        // get the drop position within the full layout
        // only for external drops (above or below this block row)
        fun getExternalDropPosition(blockRow: BlockRow, event: DragEvent): Int {
            var dropPosition = POSITION_INVALID
            // takes into account the blockrow index
            val blockRowIndex = callback.contentView.findChildPosition(blockRow)
            if (blockRowIndex == POSITION_INVALID) {
                Log.d(DEBUG_TAG, "getExternalDropPosition ++ blockRow not in layout!")
                return dropPosition
            }

            val cutoff = (blockRow.y + blockRow.height / 2.0).toInt() + callback.contentView.top
            if (event.y < cutoff) {
                dropPosition = blockRowIndex // on top of block row
            } else {
                dropPosition = blockRowIndex + 1 // on bottom of block row
            }

            return dropPosition
        }

        /**
         *
         */
        fun handleInternalDrag(draggedView: BlockView, blockRow: BlockRow, event: DragEvent): Boolean {
//            Log.i(DEBUG_TAG, "handleInternalDrag ++ ")
            val action = event.action

            return true
        }

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
            if (!view.canDropIn(draggedView as BlockView)) {
                return handleExternalDrag(draggedView, view, event)
            } else {
                return handleInternalDrag(draggedView, view, event)
            }
        }

        private fun addSpacer(position: Int): View {
            callback.contentView.removeView(oldSpacer)

            val spacer = callback.spacer
            callback.contentView.addView(spacer, position)

            val lp = spacer.layoutParams
            lp.height = spacerHeight
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            spacer.layoutParams = lp

            return spacer
        }

        private fun removeSpacer() {
            oldSpacer?.let {
                it.parent?.let {
                    callback.contentView.removeView(oldSpacer)
                    oldSpacer = null
                }
            }
        }

        // Scroll the full content layout
        private fun handleScroll(scrollView: ScrollView, event: DragEvent, threshold: Pair<Int, Int>): Boolean {
            val needToScroll: Boolean // whether we need to scroll this scrollview
            val eventY = event.y.toInt() // current event y-coord
            val delta: Int

            delta = when {
                (eventY < threshold.first) -> (-MAX_DRAG_SCROLL_SPEED * smootherStep(threshold.first.toFloat(), 0f, event.y)).toInt()
                (eventY > threshold.second) -> (MAX_DRAG_SCROLL_SPEED * smootherStep(
                        threshold.second.toFloat(), scrollView.height.toFloat(), event.y)).toInt()
                else -> 0
            }

            needToScroll = delta < 0 && scrollView.scrollY > 0
                    || delta > 0 && (scrollView.height + scrollView.scrollY <= scrollView.getChildAt(0).height)

//            Log.d(DEBUG_TAG, "handleScroll! delta: $delta needToScroll? $needToScroll")

            scrollView.smoothScrollBy(0, delta)

            return needToScroll
        }
    }

    /**
     * By Ken Perlin. See [Smoothstep - Wikipedia](http://en.wikipedia.org/wiki/Smoothstep).
     */
    private fun smootherStep(edge0: Float, edge1: Float, value: Float): Float {
        val clippedVal = clamp((value - edge0) / (edge1 - edge0), 0f, 1f)
        return clippedVal * clippedVal * clippedVal * (clippedVal * (clippedVal * 6 - 15) + 10)
    }
}