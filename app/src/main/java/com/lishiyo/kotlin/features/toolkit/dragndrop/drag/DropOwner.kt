package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.view.DragEvent
import android.view.View

/**
 * Interface for the view that will handle drops - the Canvas if the drop is external or the BlockRow if the drop is internal.
 */
interface DropOwner {
    /**
     * Current child position of the spacer inside the {@link DropOwner}.
     */
    fun getSpacerPosition(spacer: View): Int

    /**
     * Handle a DROP inside this DropOwner.
     *
     * @param callback
     *      the canvas that contains all blockrows
     * @param event
     *      the drag event
     *
     */
    fun handleDrop(callback: CanvasDragCallback, event: DragEvent, draggedView: View, dropToPosition: Int, spacer: View?): Boolean
}