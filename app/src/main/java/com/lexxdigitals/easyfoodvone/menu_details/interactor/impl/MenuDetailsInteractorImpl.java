package com.lexxdigitals.easyfoodvone.menu_details.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigitals.easyfoodvone.menu_details.interactor.MenuDetailsInteractor;

public final class MenuDetailsInteractorImpl implements MenuDetailsInteractor {
    @Override
    public void onMenuDetailsActivity(String input, onMenuDetailsActivityListener listener, Context context) {
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