package com.lishiyo.kotlin.commons.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.IOException

/**
 * UI related extensions.
 *
 * Created by connieli on 5/28/17.
 */

// inflate a layout into this container
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
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