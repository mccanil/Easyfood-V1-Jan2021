package com.lexxdigitals.easyfoodvone.create_combo_meals_offer.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigitals.easyfoodvone.create_combo_meals_offer.interactor.CreateComboMealsOfferInteractor;

public final class CreateComboMealsOfferInteractorImpl implements CreateComboMealsOfferInteractor {
    @Override
    public void onCreateComboMealsOfferActivity(String input, onCreateComboMealsOfferActivityListener listener, Context context) {
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