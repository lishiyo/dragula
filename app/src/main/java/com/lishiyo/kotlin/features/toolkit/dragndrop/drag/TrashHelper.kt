package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.graphics.Rect
import android.support.v4.math.MathUtils.clamp
import android.view.DragEvent
import android.view.View

/**
 * Created by connieli on 7/10/17.
 */
class TrashHelper(val callback: CanvasDragCallback) {

    private val TRASH_RATIO_MAX = 0.5f
    private val TRASH_DISTANCE_MAX = 5f
    private val scaleRatio = 1 + TRASH_RATIO_MAX / 2

    private val ownerVisibleRect = Rect()

    fun showTrash() {
        callback.trash.visibility = View.VISIBLE
    }

    fun animateOutTrash() {
        callback.trash.animate()
                .scaleX(0f)
                .scaleY(0f)
                .withEndAction({
                    callback.trash.visibility = View.GONE
                    callback.trash.scaleX = 1f
                    callback.trash.scaleY = 1f
                })
                .start()
    }

    fun selectTrash(selected: Boolean) {
        callback.trash.isSelected = selected
    }

    fun isOnTrash(event: DragEvent, ownerView: View): Boolean {
        ownerView.getGlobalVisibleRect(ownerVisibleRect)
        val x = event.x + ownerVisibleRect.left // total distance from global left
        val y = event.y + ownerVisibleRect.top // total distance from global top

        val trashSize = Pair(callback.trash.width, callback.trash.height)
        val trashTargetSize = Pair(trashSize.first * scaleRatio, trashSize.second * scaleRatio)
        val trashPosition = Pair(callback.trash.x, callback.trash.y)

        return x > trashPosition.first && x < trashPosition.first + trashTargetSize.first
                && y > trashPosition.second && y < trashPosition.second + trashTargetSize.second
    }

    fun scaleTrash(event: DragEvent, ownerView: View) {
        val trashSize = Pair(callback.trash.width, callback.trash.height)
        val trashPosition = Pair(callback.trash.x, callback.trash.y)

        ownerView.getGlobalVisibleRect(ownerVisibleRect)
        val x = event.x + ownerVisibleRect.left
        val y = event.y + ownerVisibleRect.top

        val trashCenterX = trashPosition.first + trashSize.first / 2f
        val trashCenterY = trashPosition.second + trashSize.second / 2f

        val trashRadius = trashSize.first / 2f
        val trashMaxDistance = trashRadius * TRASH_DISTANCE_MAX

        // current hover point relative to scrollview
        val distance = clamp(Math.sqrt(Math.pow((x - trashCenterX).toDouble(), 2.0) + Math.pow((y - trashCenterY).toDouble(), 2.0)).toFloat(), trashRadius, trashMaxDistance)

        val ratio = TRASH_RATIO_MAX * ((trashMaxDistance - distance) / (trashMaxDistance - trashRadius)) + 1

        callback.trash.scaleX = ratio
        callback.trash.scaleY = ratio
    }
}