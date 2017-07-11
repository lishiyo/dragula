package com.lishiyo.kotlin.di.dragndrop

import android.content.Context
import com.lishiyo.kotlin.di.dragndrop.qualifiers.BlockKey
import com.lishiyo.kotlin.features.dragndrop.models.MaxThreeBlock
import com.lishiyo.kotlin.features.dragndrop.models.MaxOneBlock
import com.lishiyo.kotlin.features.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.features.dragndrop.viewmodels.MaxOneBlockView
import com.lishiyo.kotlin.features.dragndrop.viewmodels.MaxThreeBlockView
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Created by connieli on 7/1/17.
 */
@Module
class TextBlockModule {

    @Provides
    @IntoMap
    @BlockKey(MaxOneBlock::class)
    internal fun provideTextBlockView(context: Context): BlockView {
        return MaxOneBlockView(context)
    }

    @Provides
    internal fun provideTextBlock(): MaxOneBlock {
        return MaxOneBlock()
    }
}

@Module
class ImageBlockModule {
    @Provides
    @IntoMap
    @BlockKey(MaxThreeBlock::class)
    internal fun provideImageBlockView(context: Context): BlockView {
        return MaxThreeBlockView(context)
    }

    @Provides
    internal fun provideImageBlock(): MaxThreeBlock {
        return MaxThreeBlock()
    }
}