package com.lexxdigital.easyfoodvone.contact_support.interactor.impl;

import android.content.Context;
import android.text.TextUtils;

import com.lexxdigital.easyfoodvone.contact_support.interactor.ContactSupportInteractor;

public final class ContactSupportInteractorImpl implements ContactSupportInteractor {
    @Override
    public void onContactSupportActivity(String input, onContactSupportActivityListener listener, Context context) {
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