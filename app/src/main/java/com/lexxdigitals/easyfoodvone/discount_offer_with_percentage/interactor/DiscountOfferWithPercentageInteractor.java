package com.lexxdigitals.easyfoodvone.discount_offer_with_percentage.interactor;

import android.content.Context;

public interface DiscountOfferWithPercentageInteractor {
    void onDiscountOfferWithPercentageActivity(String input, onDiscountOfferWithPercentageActivityListener listener, Context context);

    interface onDiscountOfferWithPercentageActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}