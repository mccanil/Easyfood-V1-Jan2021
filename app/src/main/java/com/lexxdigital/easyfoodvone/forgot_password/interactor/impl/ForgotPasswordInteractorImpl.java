package com.lexxdigital.easyfoodvone.forgot_password.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigital.easyfoodvone.forgot_password.interactor.ForgotPasswordInteractor;

public final class ForgotPasswordInteractorImpl implements ForgotPasswordInteractor {
    @Override
    public void onForgotPasswordActivity(String input, onForgotPasswordActivityListener listener, Context context) {
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