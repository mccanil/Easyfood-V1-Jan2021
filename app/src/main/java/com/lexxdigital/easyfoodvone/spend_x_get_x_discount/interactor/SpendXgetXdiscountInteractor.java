package com.lexxdigital.easyfoodvone.spend_x_get_x_discount.interactor;

import android.content.Context;

public interface SpendXgetXdiscountInteractor {
    void onSpendXgetXdiscountActivity(String input, onSpendXgetXdiscountActivityListener listener, Context context);

    interface onSpendXgetXdiscountActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}