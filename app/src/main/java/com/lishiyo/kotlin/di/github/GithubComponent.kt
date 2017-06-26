package com.lishiyo.kotlin.di.github

import com.lishiyo.kotlin.di.AppModule
import com.lishiyo.kotlin.features.scribbles.github.ui.GithubActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Wire up github modules to clients.
 *
 * Created by connieli on 5/28/17.
 */
@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        DataModule::class)
)
interface GithubComponent {
    fun inject(target: GithubActivity)
}