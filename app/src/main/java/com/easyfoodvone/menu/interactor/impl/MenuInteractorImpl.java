package com.easyfoodvone.menu.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.easyfoodvone.menu.interactor.MenuInteractor;

public final class MenuInteractorImpl implements MenuInteractor {
    @Override
    public void onMenuActivity(String input, onMenuActivityListener listener, Context context) {
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