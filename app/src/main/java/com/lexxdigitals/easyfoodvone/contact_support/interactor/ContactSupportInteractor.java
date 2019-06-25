package com.lexxdigitals.easyfoodvone.contact_support.interactor;

import android.content.Context;

public interface ContactSupportInteractor {
    void onContactSupportActivity(String input, onContactSupportActivityListener listener, Context context);

    interface onContactSupportActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}