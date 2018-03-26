package com.hospital.tokensystem

import android.os.Handler
import android.text.TextUtils

/**
 * Created by bala on 4/11/17.
 */

class LoginInteractorImpl : LoginInteractor {

    override fun login(username: String, password: String, listener: LoginInteractor.OnLoginFinishedListener) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds
        Handler().postDelayed(Runnable {
            var error = false
            if (TextUtils.isEmpty(username)) {
                listener.onUsernameError()
                error = true
                return@Runnable
            }
            if (TextUtils.isEmpty(password)) {
                listener.onPasswordError()
                error = true
                return@Runnable
            }
            if (!error) {
                listener.onSuccess()
            }
        }, 2000)
    }
}
