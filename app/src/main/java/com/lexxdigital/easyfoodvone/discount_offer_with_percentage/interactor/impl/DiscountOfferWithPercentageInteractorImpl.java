package com.lexxdigital.easyfoodvone.discount_offer_with_percentage.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigital.easyfoodvone.discount_offer_with_percentage.interactor.DiscountOfferWithPercentageInteractor;

public final class DiscountOfferWithPercentageInteractorImpl implements DiscountOfferWithPercentageInteractor {
    @Override
    public void onDiscountOfferWithPercentageActivity(String input, onDiscountOfferWithPercentageActivityListener listener, Context context) {
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