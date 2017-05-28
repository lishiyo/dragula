package com.segunfamisa.kotlin.commons.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by connieli on 5/28/17.
 */
operator fun CompositeDisposable.plus(disposable: Disposable): CompositeDisposable {
    add(disposable)
    return this
}
