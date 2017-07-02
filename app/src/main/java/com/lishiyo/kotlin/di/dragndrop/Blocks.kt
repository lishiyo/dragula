package com.lishiyo.kotlin.di.dragndrop

import android.content.Context
import com.lishiyo.kotlin.di.dragndrop.qualifiers.BlockKey
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.ImageBlock
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.TextBlock
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.MaxOneBlockView
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.MaxThreeBlockView
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
    @BlockKey(TextBlock::class)
    internal fun provideTextBlockView(context: Context): BlockView {
        return MaxOneBlockView(context)
    }

    @Provides
    internal fun provideTextBlock(): TextBlock {
        return TextBlock()
    }
}

@Module
class ImageBlockModule {
    @Provides
    @IntoMap
    @BlockKey(ImageBlock::class)
    internal fun provideImageBlockView(context: Context): BlockView {
        return MaxThreeBlockView(context)
    }

    @Provides
    internal fun provideImageBlock(): ImageBlock {
        return ImageBlock()
    }
}