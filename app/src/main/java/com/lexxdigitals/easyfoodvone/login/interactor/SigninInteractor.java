package com.lexxdigitals.easyfoodvone.login.interactor;

import android.content.Context;

public interface SigninInteractor {
    void onSigninActivity(String input, onSigninActivityListener listener, Context context);

    interface onSigninActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}