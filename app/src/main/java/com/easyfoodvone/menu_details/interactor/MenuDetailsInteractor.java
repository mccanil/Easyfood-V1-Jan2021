package com.easyfoodvone.menu_details.interactor;

import android.content.Context;

public interface MenuDetailsInteractor {
    void onMenuDetailsActivity(String input, onMenuDetailsActivityListener listener, Context context);

    interface onMenuDetailsActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}