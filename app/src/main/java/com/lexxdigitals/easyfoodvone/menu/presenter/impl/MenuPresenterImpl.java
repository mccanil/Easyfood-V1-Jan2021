package com.lexxdigitals.easyfoodvone.menu.presenter.impl;

import android.content.Context;

import com.lexxdigitals.easyfoodvone.menu.interactor.MenuInteractor;
import com.lexxdigitals.easyfoodvone.menu.interactor.impl.MenuInteractorImpl;
import com.lexxdigitals.easyfoodvone.menu.presenter.MenuPresenter;
import com.lexxdigitals.easyfoodvone.menu.view.MenuView;

public final class MenuPresenterImpl implements MenuPresenter, MenuInteractor.onMenuActivityListener {
    MenuView mView;
    MenuInteractor mInteractor;

    public MenuPresenterImpl(MenuView nView) {
        this.mView = nView;
        mInteractor = new MenuInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onMenuActivity(input, this, context);

    }

    @Override
    public void onSuccess() {

        if (mView != null)

            mView.setNavigation();


    }

    @Override
    public void failure(String message) {
        if (mView != null)
            mView.showMessage(message);

    }

    @Override
    public void onInputError() {
        if (mView != null)
            mView.setInputError();

    }
}