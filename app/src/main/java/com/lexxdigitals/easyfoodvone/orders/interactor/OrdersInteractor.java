package com.lexxdigitals.easyfoodvone.orders.interactor;

import android.content.Context;

public interface OrdersInteractor {
    void onOrdersActivity(String input, onOrdersActivityListener listener, Context context);

    interface onOrdersActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}