package com.hospital.tokensystem

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        supportFragmentManager.beginTransaction()
                .replace(R.id.loginFrameLayout, LoginFragment.newInstance("", ""))
                .commit()
    }
}
