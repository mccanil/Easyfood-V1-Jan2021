package com.lexxdigitals.easyfoodvone.new_order.interactor;

import android.content.Context;

public interface NewOrderInteractor {
    void onNewOrderActivity(String input, onNewOrderActivityListener listener, Context context);

    interface onNewOrderActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}