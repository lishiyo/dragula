package com.lishiyo.kotlin.di.dragndrop.qualifiers

import com.lishiyo.kotlin.features.toolkit.dragndrop.models.Block
import dagger.MapKey
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.reflect.KClass

/**
 * Created by connieli on 7/1/17.
 */
@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class CanvasSpacer

//@Qualifier
//@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
//annotation class InnerSpacer

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity

/**
 * @return the {@link Class} that is used as the Key for the {@link Block}
 */
@MapKey
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class BlockKey(val value: KClass<out Block>)