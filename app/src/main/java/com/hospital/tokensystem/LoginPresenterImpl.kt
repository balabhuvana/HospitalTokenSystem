package com.hospital.tokensystem


/**
 * Created by bala on 4/11/17.
 */

class LoginPresenterImpl(private val mLoginView: LoginView) : LoginPresenter, LoginInteractor.OnLoginFinishedListener {
    private val mLoginInteractorImpl: LoginInteractorImpl

    init {
        mLoginInteractorImpl = LoginInteractorImpl()
    }

    override fun validateLogin(userName: String, password: String) {
        mLoginView.showProgress()
        mLoginInteractorImpl.login(userName, password, this)
    }


    override fun onUsernameError() {
        mLoginView.userNameError()
        mLoginView.hideProgress()

    }

    override fun onPasswordError() {
        mLoginView.passwordError()
        mLoginView.hideProgress()
    }

    override fun onSuccess() {
        mLoginView.goToHomeFragment()
        mLoginView.hideProgress()
    }
}
