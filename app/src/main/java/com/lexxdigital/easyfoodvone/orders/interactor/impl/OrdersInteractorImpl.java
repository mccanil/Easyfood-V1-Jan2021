package com.lexxdigital.easyfoodvone.orders.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigital.easyfoodvone.orders.interactor.OrdersInteractor;

public final class OrdersInteractorImpl implements OrdersInteractor {
    @Override
    public void onOrdersActivity(String input, onOrdersActivityListener listener, Context context) {
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