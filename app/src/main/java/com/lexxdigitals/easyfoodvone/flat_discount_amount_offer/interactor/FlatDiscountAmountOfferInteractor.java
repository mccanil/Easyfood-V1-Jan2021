package com.lexxdigitals.easyfoodvone.flat_discount_amount_offer.interactor;

import android.content.Context;

public interface FlatDiscountAmountOfferInteractor {
    void onFlatDiscountAmountOfferActivity(String input, onFlatDiscountAmountOfferActivityListener listener, Context context);

    interface onFlatDiscountAmountOfferActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}