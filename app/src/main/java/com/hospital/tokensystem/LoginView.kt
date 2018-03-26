package com.hospital.tokensystem

/**
 * Created by bala on 4/11/17.
 */

interface LoginView {

    fun showProgress()

    fun hideProgress()

    fun userNameError()

    fun passwordError()

    fun goToHomeFragment()
}
