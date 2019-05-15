package com.lexxdigital.easyfoodvone.forgot_password.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.forgot_password.interactor.ForgotPasswordInteractor;
import com.lexxdigital.easyfoodvone.forgot_password.interactor.impl.ForgotPasswordInteractorImpl;
import com.lexxdigital.easyfoodvone.forgot_password.presenter.ForgotPasswordPresenter;
import com.lexxdigital.easyfoodvone.forgot_password.view.ForgotPasswordView;

public final class ForgotPasswordPresenterImpl implements ForgotPasswordPresenter, ForgotPasswordInteractor.onForgotPasswordActivityListener {
    ForgotPasswordView mView;
    ForgotPasswordInteractor mInteractor;

    public ForgotPasswordPresenterImpl(ForgotPasswordView nView) {
        this.mView = nView;
        mInteractor = new ForgotPasswordInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onForgotPasswordActivity(input, this, context);

    }

    @Override
    public void onSuccess() {

        if (mView != null)

            mView.setNavigation();


    }

    @Override
    public void failure(String message) {
        if (mView != null)
            mView.showMessage(message);

    }

    @Override
    public void onInputError() {
        if (mView != null)
            mView.setInputError();

    }
}