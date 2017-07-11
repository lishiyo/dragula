package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import com.lishiyo.kotlin.commons.adapter.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.POSITION_INVALID
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView

/**
 * Created by connieli on 7/10/17.
 */

// Drag listener for the spacer so it can accept drops
class SpacerDragListener(dropOwner: DropOwner,
                         callback: CanvasDragCallback,
                         @CanvasSpacer spacer: View) : View.OnDragListener {
    val dropOwner = dropOwner
    val callback = callback
    private var oldSpacer: View? = spacer

    override fun onDrag(view: View, event: DragEvent): Boolean {
        if (event.localState !is BlockView) {
            Log.d(DEBUG_TAG, "onDrag! A drag event using a " + event.localState.javaClass.canonicalName + " was detected")
            return false
        }

        val draggedView = event.localState as View

        return handleDrag(draggedView, view, event)
    }

    fun handleDrag(draggedView: View, spacer: View, event: DragEvent): Boolean {
        val action = event.action

        when (action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                // TODO: set scroll threshold
                Log.d(DEBUG_TAG, "ACTION_DRAG_STARTED! == ON SPACER ${dropOwner.getSpacerPosition(spacer)}")
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                // TODO: scroll if necessary
                Log.d(DEBUG_TAG, "ACTION_DRAG_LOCATION! == ON SPACER ${dropOwner.getSpacerPosition(spacer)}")
            }
            DragEvent.ACTION_DROP -> {
                // view was dropped in this spacer - add it here
                val dropToPosition = dropOwner.getSpacerPosition(spacer)
                Log.d(DEBUG_TAG, "ACTION_DROP! == ON SPACER $dropToPosition")

                // TODO handle trashmode?
                if (dropToPosition != POSITION_INVALID) {
                    dropOwner.handleDrop(callback, event, draggedView, dropToPosition, spacer)
                }

                // remove self if not already removed
                removeSpacers()
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d(DEBUG_TAG, "ACTION_DRAG_EXITED! == ON SPACER")
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                // TODO: this doesn't seem to be getting hit?
                Log.d(DEBUG_TAG, "ACTION_DRAG_ENDED! == ON SPACER")

                removeSpacers()
            }
        }

        return true
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

    // index of block row we are dragging a view from
    // does NOT include spacer
    fun getDragFromBlockRow(draggedView: View): View {
        // find which block row this drag is coming from
        // TODO: get it from the clip data
        val draggedFromBlockRow = callback.blockRows.findLast {
            blockRow -> blockRow.indexOfChild(draggedView) != POSITION_INVALID
        }

        return draggedFromBlockRow ?: (draggedView.parent as View)
    }
}