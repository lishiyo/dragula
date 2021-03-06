package com.lishiyo.kotlin.commons.extensions

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DimenRes
import android.support.v4.math.MathUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import java.io.IOException

/**
 * UI related extensions on View, Context etc.
 *
 * Created by connieli on 5/28/17.
 */

val POSITION_INVALID = -1

fun checkRemoveParent(view: View?) {
    view?.let {
        if (view.parent is ViewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
    }
}

fun smootherStep(edge0: Float, edge1: Float, value: Float): Float {
    val clippedVal = MathUtils.clamp((value - edge0) / (edge1 - edge0), 0f, 1f)
    return clippedVal * clippedVal * clippedVal * (clippedVal * (clippedVal * 6 - 15) + 10)
}

// viewgroups' direct child views ( NOT recursive)
val ViewGroup.childViews: List<View>
    get() = (0..childCount - 1).map({ getChildAt(it) })

// inflate a layout into this container
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

// given a child view, find it in this viewgroup
fun ViewGroup.findChildPosition(viewToFind: View): Int {
    return childViews.indexOf(viewToFind)
}

// make Fresco image full-width, keeping aspect ratio
fun Drawable.matchScreenWidth(activity: Activity, layoutParams: LinearLayout.LayoutParams): LinearLayout.LayoutParams {
    val imageViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    imageViewParams.width = activity.getScreenWidth()
    val imageRatio = intrinsicWidth.toFloat() / intrinsicHeight.toFloat()

    layoutParams.height = (layoutParams.width / imageRatio).toInt()
    return layoutParams
}

fun View.setDragStart(dragData: ClipData, shadowBuilder: View.DragShadowBuilder): Boolean {
    // Start the drag
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.startDragAndDrop(dragData, // data to be dragged
                shadowBuilder, // drag shadow builder
                this, // send info from dragged to target views in same activity (via `getLocalState`)
                0 // flags
        )
    } else {
        this.startDrag(dragData, shadowBuilder, this, 0)
    }

    return true
}

fun Context.getPixelSize(@DimenRes resId: Int): Int {
    return this.resources.getDimensionPixelSize(resId)
}

fun Activity.getScreenWidth(): Int {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)

    return size.x
}

// read a file from assets folder
fun Context.loadJsonFromFile(filename: String) : String? {
    val jsonStr: String?

    try {
        val inputStream = assets.open(filename)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        jsonStr = String(buffer)
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }

    return jsonStr
}
