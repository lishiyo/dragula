package com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.View
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.BlockRow
import com.lishiyo.kotlin.features.toolkit.dragndrop.ui.OnScrollChangedCallback

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
    private var spacerHeight = 0
    private lateinit var oldSpacer: View
    private var isScrolling = false
    // top 10% and bottom 10% of current scrollview height (scrollview wraps the blocks layout)
    private var scrollThreshold : Pair<Int, Int> = Pair(0, 0)

    private val dragListener = CanvasDragListener()

    companion object {
        // factory constructor
        fun init(context: Context, dragCallback: CanvasDragCallback): CanvasDragHelper {
            return CanvasDragHelper(context, dragCallback)
        }
    }

    init {
//        callback.scrollView.setOnDragListener(dragListener)
        // for each block row, set drag listener
        callback.blockRows.forEach({
            it.setOnDragListener(dragListener)
        })

        callback.scrollView.setOnScrollChangedCallback(object : OnScrollChangedCallback {
            override fun onScroll(l: Int, t: Int) {
                isScrolling = true
            }

            override fun onEndScroll() {
                isScrolling = false
            }

        })
    }

    // this is the "drop zone" view, listening to the dragged view
    inner class CanvasDragListener : View.OnDragListener {
        // flag for when the view enters or exits trash area
        private val trashMode: Boolean = false

        override fun onDrag(view: View, event: DragEvent): Boolean {
            if (event.localState !is View) {
                Log.d(DEBUG_TAG, "A drag event using a " + event.localState.javaClass.canonicalName + " was detected")
                return false
            }
            if (view !is BlockRow) {
                Log.d(DEBUG_TAG, "trying to drop in non-BlockRow")
            }

            val draggedView = event.localState as View
            val action = event.action

            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(DEBUG_TAG, "ACTION_DRAG_STARTED! trashMode? " + trashMode + " view: " + view.id)
                    // unsubscribe from animation

                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // dragged view entered our view
                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENTERED! trashMode? " + trashMode + " view: " + view.id)
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // dragged view is moving around in our view area
                    Log.d(DEBUG_TAG, "ACTION_DRAG_LOCATION! trashMode? " + trashMode + " view: " + view.id)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // dragged view exited from our view area
                    Log.d(DEBUG_TAG, "ACTION_DRAG_EXITED! trashMode? " + trashMode + " view: " + view.id)
                }
                DragEvent.ACTION_DROP -> {
                    // view was dropped in here!
                    Log.d(DEBUG_TAG, "ACTION_DROP! trashMode? " + trashMode + " view: " + view.id)
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    // drag and drop flow finished, didn't drop in here
                    Log.d(DEBUG_TAG, "ACTION_DRAG_ENDED! trashMode? " + trashMode + " view: " + view.id)
                }
                else -> { // Note the block
                    Log.d(DEBUG_TAG, "fell to default: " + trashMode + " view: " + view.id)
                }
            }

            return true
        }

    }
}