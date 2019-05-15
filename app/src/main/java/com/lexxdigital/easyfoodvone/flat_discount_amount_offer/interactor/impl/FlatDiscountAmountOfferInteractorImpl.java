package com.lexxdigital.easyfoodvone.flat_discount_amount_offer.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigital.easyfoodvone.flat_discount_amount_offer.interactor.FlatDiscountAmountOfferInteractor;

public final class FlatDiscountAmountOfferInteractorImpl implements FlatDiscountAmountOfferInteractor {
    @Override
    public void onFlatDiscountAmountOfferActivity(String input, onFlatDiscountAmountOfferActivityListener listener, Context context) {
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