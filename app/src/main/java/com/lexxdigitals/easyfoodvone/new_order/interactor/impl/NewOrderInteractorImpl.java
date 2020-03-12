package com.lexxdigitals.easyfoodvone.new_order.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigitals.easyfoodvone.new_order.interactor.NewOrderInteractor;

public final class NewOrderInteractorImpl implements NewOrderInteractor {
    @Override
    public void onNewOrderActivity(String input, onNewOrderActivityListener listener, Context context) {
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