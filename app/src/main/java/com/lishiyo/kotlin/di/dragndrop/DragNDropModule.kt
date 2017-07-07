package com.lishiyo.kotlin.di.dragndrop

import android.content.Context
import android.view.View
import com.lishiyo.kotlin.di.dragndrop.qualifiers.CanvasSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.InnerSpacer
import com.lishiyo.kotlin.di.dragndrop.qualifiers.PerActivity
import com.lishiyo.kotlin.features.toolkit.dragndrop.CanvasLayoutHelper
import com.lishiyo.kotlin.features.toolkit.dragndrop.DragNDropActivity
import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import com.lishiyo.kotlin.features.toolkit.dragndrop.viewmodels.BlockView
import com.lishiyo.kotlin.samples.retrofit.R
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Provider

/**
 * Created by connieli on 7/1/17.
 */
@Module
class DragNDropModule(val activity: DragNDropActivity) {

    @Provides
    @PerActivity
    @Named("CanvasLayoutHelper")
    internal fun provideCanvasLayoutHelper(activity: DragNDropActivity,
                                           blockViewProviderMap: Map<Class<out Block>, @JvmSuppressWildcards Provider<BlockView>>,
                                           @CanvasSpacer spacerProvider: Provider<View>,
                                           @InnerSpacer innerSpacerProvider: Provider<View>): CanvasLayoutHelper {
        return CanvasLayoutHelper(activity, blockViewProviderMap, spacerProvider, innerSpacerProvider)
    }

    @Provides
    @CanvasSpacer
    internal fun provideSpacer(context: Context): View {
        val spacer = View(context)
        spacer.setBackgroundResource(R.drawable.canvas_spacer_background)
        return spacer
    }

    @Provides
    @InnerSpacer
    internal fun provideInnerSpacer(context: Context): View {
        val spacer = View(context)
        spacer.setBackgroundResource(R.drawable.inner_spacer_background)
        return spacer
    }

    @Provides
    internal fun provideActivity(): DragNDropActivity {
        return activity
    }

    @Provides
    internal fun provideContext(): Context {
        return activity
    }
}