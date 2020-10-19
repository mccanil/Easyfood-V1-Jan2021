package com.easyfoodvone.menu.interactor;

import android.content.Context;

public interface MenuInteractor {
    void onMenuActivity(String input, onMenuActivityListener listener, Context context);

    interface onMenuActivityListener {

        void onSuccess();

        void failure(String message);

        void onInputError();
    }
}