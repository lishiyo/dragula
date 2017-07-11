package com.lishiyo.kotlin.features.toolkit.dragndrop.drag

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import com.lishiyo.kotlin.commons.extensions.getPixelSize
import com.lishiyo.kotlin.dragula.R

/**
 * Tumblr real canvas shadow.

 * @param view
 * * 		The [View] to add a shadow to
 */
class CanvasImageShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    private val mShadow: Drawable = ContextCompat.getDrawable(view.context, R.drawable.shadow_drag_n_drop)
    private var width: Int = 0
    private var height: Int = 0

    private var mOffsetMedium: Int = 0
    private var mOffsetLarge: Int = 0

    init {
        mOffsetMedium = view.context.getPixelSize(R.dimen.canvas_image_shadow_margin_medium)
        mOffsetMedium = view.context.getPixelSize(R.dimen.canvas_image_shadow_margin_large)
    }

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        width = (view.width * SHADOW_SHRINK_RATIO).toInt() + mOffsetLarge
        height = (view.height * SHADOW_SHRINK_RATIO + mOffsetLarge).toInt()
        mShadow.setBounds(0, 0, width, height)
        size.set(width, height)
        touch.set(width / 2, height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {
        mShadow.draw(canvas)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val smallerCanvas = Canvas(bitmap)
        view.draw(smallerCanvas)
        canvas.scale(SHADOW_SHRINK_RATIO, SHADOW_SHRINK_RATIO)
        canvas.drawBitmap(bitmap, mOffsetMedium.toFloat(), mOffsetMedium.toFloat(), null)
    }

    companion object {
        private val SHADOW_SHRINK_RATIO = 0.9f
    }
}
