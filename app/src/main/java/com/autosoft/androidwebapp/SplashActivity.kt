package com.autosoft.androidwebapp

import android.content.Intent
import android.os.Bundle

class SplashActivity : BaseActivity() {

    private var splashComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Thread {
            Thread.sleep(SPLASH_DURATION)
            splashComplete = true
            launch()
        }.start()

    }

    override fun onResume() {
        super.onResume()
        launch()
    }

    private fun launch() {
        if (splashComplete && userPresent) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
