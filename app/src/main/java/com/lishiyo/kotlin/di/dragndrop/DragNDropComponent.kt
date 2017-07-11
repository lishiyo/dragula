package com.lishiyo.kotlin.di.dragndrop

import com.lishiyo.kotlin.di.dragndrop.qualifiers.PerActivity
import com.lishiyo.kotlin.features.dragndrop.DragNDropActivity
import dagger.Component

/**
 * Created by connieli on 7/1/17.
 */
@PerActivity
@Component(modules = arrayOf(
        TextBlockModule::class,
        ImageBlockModule::class,
        DragNDropModule::class)
) interface DragNDropComponent {
    fun inject(target: DragNDropActivity)
}