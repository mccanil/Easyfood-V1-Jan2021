package com.lexxdigitals.easyfoodvone.single_order_report_detail.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigitals.easyfoodvone.single_order_report_detail.interactor.OrderRepostDetailInteractor;

public final class OrderRepostDetailInteractorImpl implements OrderRepostDetailInteractor {
    @Override
    public void onOrderRepostDetailActivity(String input, onOrderRepostDetailActivityListener listener, Context context) {
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