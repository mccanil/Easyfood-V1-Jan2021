package com.lexxdigitals.easyfoodvone.forgot_password.interactor;

import android.content.Context;

public interface ForgotPasswordInteractor {
    void onForgotPasswordActivity(String input, onForgotPasswordActivityListener listener, Context context);

    interface onForgotPasswordActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}