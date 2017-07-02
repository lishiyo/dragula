package com.lishiyo.kotlin.features.toolkit.dragndrop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.ButterKnife
import com.lishiyo.kotlin.commons.ui.RxBaseActivity
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Created by connieli on 7/1/17.
 */
class DragDropActivity : RxBaseActivity() {

    companion object {
        fun createIntent(context: Context, bundle: Bundle?): Intent {
            val intent = Intent(context, DragDropActivity::class.java)
            bundle?.let { intent.putExtras(it) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dragndrop)
        ButterKnife.bind(this)
    }
}
