package com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View

/**
 * From https://developer.android.com/guide/topics/ui/drag-drop.html
 *
 * Created by connieli on 7/2/17.
 */
private class DragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    companion object {
        // The drag shadow image, to be filled in as ColorDrawable
        lateinit private var shadow: Drawable
    }

    init {
        // Store the View parameter passed to myDragShadowBuilder.
        // Create a draggable image that will fill the Canvas provided by the system.
        shadow = ColorDrawable(Color.LTGRAY)
    }

    // Callback that sends the drag shadow dimensions and touch point back to the system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Set the width of the shadow to half the width of the original View
        val width: Int = view.width / 2
        // Set the height of the shadow to half the height of the original View
        val height: Int = view.height / 2

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide => the drag shadow will fill the Canvas.
        shadow.setBounds(0, 0, width, height)

        // Set the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Callback that draws the drag shadow in a Canvas that the system constructs from the dimensions
    // passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draw the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}