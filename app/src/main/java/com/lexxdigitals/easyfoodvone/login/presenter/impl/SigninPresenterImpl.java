package com.lexxdigitals.easyfoodvone.login.presenter.impl;

import android.content.Context;

import com.lexxdigitals.easyfoodvone.login.interactor.SigninInteractor;
import com.lexxdigitals.easyfoodvone.login.interactor.impl.SigninInteractorImpl;
import com.lexxdigitals.easyfoodvone.login.presenter.SigninPresenter;
import com.lexxdigitals.easyfoodvone.login.view.SigninView;

public final class SigninPresenterImpl implements SigninPresenter, SigninInteractor.onSigninActivityListener {
    SigninView mView;
    SigninInteractor mInteractor;

    public SigninPresenterImpl(SigninView nView) {
        this.mView = nView;
        mInteractor = new SigninInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onSigninActivity(input, this, context);

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