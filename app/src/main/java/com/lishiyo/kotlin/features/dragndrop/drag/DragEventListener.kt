package com.lishiyo.kotlin.features.dragndrop.drag

import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.Toast


/**
 * Created by connieli on 7/2/17.
 */
class DragEventListener(context: Context) : View.OnDragListener {
    private val context = context

    // This is the method that the system calls when it dispatches a drag event to the
    // listener.
    override fun onDrag(v: View, event: DragEvent): Boolean {

        // Defines a variable to store the action type for the incoming event
        val action = event.action

        // Handles each of the expected events
        when (action) {

            DragEvent.ACTION_DRAG_STARTED -> {

                // Determines if this View can accept the dragged data
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                    // As an example of what your application might do,
                    // applies a blue color tint to the View to indicate that it can accept
                    // data.
//                    v.setColorFilter(Color.BLUE)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()

                    // returns true to indicate that the View can accept the dragged data.
                    return true

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false
            }

            DragEvent.ACTION_DRAG_ENTERED -> {

                // Applies a green tint to the View. Return true; the return value is ignored.

//                v.setColorFilter(Color.GREEN)

                // Invalidate the view to force a redraw in the new tint
                v.invalidate()

                return true
            }

            DragEvent.ACTION_DRAG_LOCATION ->

                // Ignore the event
                return true

            DragEvent.ACTION_DRAG_EXITED -> {

                // Re-sets the color tint to blue. Returns true; the return value is ignored.
//                v.setColorFilter(Color.BLUE)

                // Invalidate the view to force a redraw in the new tint
                v.invalidate()

                return true
            }

            DragEvent.ACTION_DROP -> {

                // Gets the item containing the dragged data
                val item = event.clipData.getItemAt(0)

                // Gets the text data from the item.
//                dragData = item.text

                // Displays a message containing the dragged data.
                Toast.makeText(context, "Dragged data is ", Toast.LENGTH_LONG)

                // Turns off any color tints
//                v.clearColorFilter()

                // Invalidates the view to force a redraw
                v.invalidate()

                // Returns true. DragEvent.getResult() will return true.
                return true
            }

            DragEvent.ACTION_DRAG_ENDED -> {

                // Turns off any color tinting
//                v.clearColorFilter()

                // Invalidates the view to force a redraw
                v.invalidate()

                // Does a getResult(), and displays what happened.
                if (event.result) {
                    Toast.makeText(context, "The drop was handled.", Toast.LENGTH_LONG)

                } else {
                    Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_LONG)

                }

                // returns true; the value is ignored.
                return true
            }

            // An unknown action type was received.
            else -> Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
        }

        return false
    }
};