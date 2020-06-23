package com.autosoft.androidwebapp

import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.ProgressBar

abstract class BaseActivity : AppCompatActivity() {

    private var mUserPresent = false

    protected val userPresent
        get() = mUserPresent

    private lateinit var loader: ProgressBar

    protected var loading = false
        set(value) {
            field = value
            loader.visibility = if (value)
                View.VISIBLE
            else
                View.GONE
        }

    override fun setContentView(layoutResID: Int) {

        val frameLayout = FrameLayout(this)

        frameLayout.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        val contentView = layoutInflater.inflate(layoutResID, null)

        loader = ProgressBar(this)
        loader.visibility = View.GONE

        val loaderLayoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        loaderLayoutParams.gravity = Gravity.CENTER

        loader.layoutParams = loaderLayoutParams

        frameLayout.addView(contentView)
        frameLayout.addView(loader)

        super.setContentView(frameLayout)
    }

    override fun onResume() {
        super.onResume()
        mUserPresent = true
    }

    override fun onPause() {
        super.onPause()
        mUserPresent = false
    }

}