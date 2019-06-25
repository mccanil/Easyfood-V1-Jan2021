package com.lexxdigitals.easyfoodvone.spend_x_get_x_discount.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigitals.easyfoodvone.spend_x_get_x_discount.interactor.SpendXgetXdiscountInteractor;

public final class SpendXgetXdiscountInteractorImpl implements SpendXgetXdiscountInteractor {
    @Override
    public void onSpendXgetXdiscountActivity(String input, onSpendXgetXdiscountActivityListener listener, Context context) {
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