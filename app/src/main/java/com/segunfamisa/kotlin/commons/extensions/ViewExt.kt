package com.segunfamisa.kotlin.commons.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * UI related extensions.
 *
 * Created by connieli on 5/28/17.
 */

// inflate a layout into this container
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}