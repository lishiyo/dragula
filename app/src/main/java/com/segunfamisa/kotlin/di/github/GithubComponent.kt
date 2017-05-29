package com.segunfamisa.kotlin.di.github

import com.segunfamisa.kotlin.di.AppModule
import com.segunfamisa.kotlin.github.ui.GithubActivity
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
        NetworkModule::class)
)
interface GithubComponent {

    fun inject(target: GithubActivity)

}