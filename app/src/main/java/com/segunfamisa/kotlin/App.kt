package com.segunfamisa.kotlin

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by connieli on 5/28/17.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this);
    }
}