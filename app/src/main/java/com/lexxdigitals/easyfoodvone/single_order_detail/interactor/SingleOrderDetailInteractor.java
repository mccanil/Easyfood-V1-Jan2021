package com.lexxdigitals.easyfoodvone.single_order_detail.interactor;

import android.content.Context;

public interface SingleOrderDetailInteractor {
    void onSingleOrderDetailActivity(String input, onSingleOrderDetailActivityListener listener, Context context);

    interface onSingleOrderDetailActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}