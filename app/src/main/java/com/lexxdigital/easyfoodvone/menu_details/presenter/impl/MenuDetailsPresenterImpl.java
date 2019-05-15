package com.lexxdigital.easyfoodvone.menu_details.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.menu_details.interactor.MenuDetailsInteractor;
import com.lexxdigital.easyfoodvone.menu_details.interactor.impl.MenuDetailsInteractorImpl;
import com.lexxdigital.easyfoodvone.menu_details.presenter.MenuDetailsPresenter;
import com.lexxdigital.easyfoodvone.menu_details.view.MenuDetailsView;

public final class MenuDetailsPresenterImpl implements MenuDetailsPresenter, MenuDetailsInteractor.onMenuDetailsActivityListener {
    MenuDetailsView mView;
    MenuDetailsInteractor mInteractor;

    public MenuDetailsPresenterImpl(MenuDetailsView nView) {
        this.mView = nView;
        mInteractor = new MenuDetailsInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onMenuDetailsActivity(input, this, context);

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