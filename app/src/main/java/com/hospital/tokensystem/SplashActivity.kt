package com.hospital.tokensystem

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import net.hockeyapp.android.UpdateManager
import net.hockeyapp.android.CrashManager


class SplashActivity : AppCompatActivity() {

    // Splash screen timer
    private val SPLASH_TIME_OUT = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(Runnable /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
        {
            // This method will be executed once the timer is over
            // Start your app main activity
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)

            // close this activity
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    public override fun onResume() {
        super.onResume()
        // ... your own onResume implementation
        checkForCrashes()
    }

    public override fun onPause() {
        super.onPause()
        unregisterManagers()
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterManagers()
    }

    private fun checkForCrashes() {
        CrashManager.register(this)
    }

    private fun checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this)
    }

    private fun unregisterManagers() {
        UpdateManager.unregister()
    }

}
