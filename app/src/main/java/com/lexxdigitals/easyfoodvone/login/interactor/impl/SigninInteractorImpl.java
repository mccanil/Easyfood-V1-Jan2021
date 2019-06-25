package com.lexxdigitals.easyfoodvone.login.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigitals.easyfoodvone.login.interactor.SigninInteractor;

public final class SigninInteractorImpl implements SigninInteractor {
    @Override
    public void onSigninActivity(String input, onSigninActivityListener listener, Context context) {
        //Do Your Logics Stubs

        if (TextUtils.isEmpty(input)) {

            listener.onInputError();
        } else if (true) {

            listener.onSuccess();
        } else {

            listener.failure("Something went wrong");
        }
    }
}