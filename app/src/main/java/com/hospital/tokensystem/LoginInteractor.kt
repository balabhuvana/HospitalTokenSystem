package com.hospital.tokensystem


/**
 * Created by bala on 4/11/17.
 */

interface LoginInteractor {

    interface OnLoginFinishedListener {
        fun onUsernameError()

        fun onPasswordError()

        fun onSuccess()
    }

    fun login(username: String, password: String, onLoginFinishedListener: OnLoginFinishedListener)
}
