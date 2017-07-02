package com.lishiyo.kotlin.features.toolkit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import com.lishiyo.kotlin.commons.ui.RxBaseFragment
import com.lishiyo.kotlin.features.toolkit.dragndrop.DragNDropActivity
import com.lishiyo.kotlin.features.toolkit.wifi_fencer.WifiFencerActivity
import com.lishiyo.kotlin.samples.retrofit.R

/**
 * Created by connieli on 6/24/17.
 */
class ToolkitFragment: RxBaseFragment() {

    // UI
    @BindView(R.id.wifi_fencer_btn) lateinit var wifiFencerBtn: Button
    @BindView(R.id.dragndrop_btn) lateinit var dragDropBtn: Button

    companion object {
        fun newInstance(bundle: Bundle?): ToolkitFragment {
            val f = ToolkitFragment()
            f.arguments = bundle
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_toolkit, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    fun initClickListeners() {
        // open wifi fencer activity
        wifiFencerBtn.setOnClickListener {
            val intent = WifiFencerActivity.createIntent(context, null)
            activity.startActivity(intent)
        }

        dragDropBtn.setOnClickListener {
            val intent = DragNDropActivity.createIntent(context, null)
            activity.startActivity(intent)
        }
    }
}