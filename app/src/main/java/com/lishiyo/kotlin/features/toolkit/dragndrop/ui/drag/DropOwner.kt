package com.lishiyo.kotlin.features.toolkit.dragndrop.ui.drag

import android.view.DragEvent
import android.view.View

/**
 * Interface for the view that will handle drops - the Canvas if the drop is external or the BlockRow if the drop is internal.
 */
interface DropOwner {
    /**
     * Current relative spacer position.
     */
    fun getSpacerPosition(spacer: View): Int

    fun handleDrop(spacer: View?, event: DragEvent, callback: CanvasDragCallback, draggedView: View, dropToPosition: Int): Boolean
}