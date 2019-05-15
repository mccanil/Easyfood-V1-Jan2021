package com.lexxdigital.easyfoodvone.create_combo_meals_offer.interactor;

import android.content.Context;

public interface CreateComboMealsOfferInteractor {
    void onCreateComboMealsOfferActivity(String input, onCreateComboMealsOfferActivityListener listener, Context context);

    interface onCreateComboMealsOfferActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}