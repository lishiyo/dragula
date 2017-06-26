package com.lishiyo.kotlin.features.toolkit.wifi_fencer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.commons.ui.RxBaseActivity
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Created by connieli on 6/24/17.
 */
class WifiFencerActivity : RxBaseActivity() {

    // UI
    @BindView(R.id.google_now_btn) lateinit var googleNowBtn: Button

    companion object {
        fun createIntent(context: Context, bundle: Bundle?): Intent {
            val intent = Intent(context, WifiFencerActivity::class.java)
            bundle?.let { intent.putExtras(it) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_fencer)
        ButterKnife.bind(this)

        initClickListeners()
    }

    fun initClickListeners() {
        googleNowBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VOICE_COMMAND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}