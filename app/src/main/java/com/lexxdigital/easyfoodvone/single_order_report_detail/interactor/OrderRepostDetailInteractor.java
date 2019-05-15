package com.lexxdigital.easyfoodvone.single_order_report_detail.interactor;

import android.content.Context;

public interface OrderRepostDetailInteractor {
    void onOrderRepostDetailActivity(String input, onOrderRepostDetailActivityListener listener, Context context);

    interface onOrderRepostDetailActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}