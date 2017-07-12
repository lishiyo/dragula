package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import com.lishiyo.kotlin.commons.DEBUG_TAG
import com.lishiyo.kotlin.commons.extensions.POSITION_INVALID
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView

/**
 * Drag listener for the spacer so we can still detect drops when hovering right on the spacer instead of a blockrow.
 *
 * Created by connieli on 7/10/17.
 */
class SpacerDragListener(val dropOwner: DropOwner,
                         val callback: CanvasDragCallback,
                         @CanvasSpacer spacer: View) : View.OnDragListener {
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
                Log.d(DEBUG_TAG, "ACTION_DRAG_STARTED! == ON SPACER ${dropOwner.getSpacerPosition(spacer)}")
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
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
//                it.setOnDragListener(null)
                (it.parent as ViewGroup).removeView(oldSpacer)
                oldSpacer = null
            }
        }
    }
}