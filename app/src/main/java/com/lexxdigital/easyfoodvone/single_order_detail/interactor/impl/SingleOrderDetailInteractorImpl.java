package com.lexxdigital.easyfoodvone.single_order_detail.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigital.easyfoodvone.single_order_detail.interactor.SingleOrderDetailInteractor;

public final class SingleOrderDetailInteractorImpl implements SingleOrderDetailInteractor {
    @Override
    public void onSingleOrderDetailActivity(String input, onSingleOrderDetailActivityListener listener, Context context) {
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