package com.lexxdigital.easyfoodvone.spend_x_get_x_discount.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.spend_x_get_x_discount.interactor.SpendXgetXdiscountInteractor;
import com.lexxdigital.easyfoodvone.spend_x_get_x_discount.interactor.impl.SpendXgetXdiscountInteractorImpl;
import com.lexxdigital.easyfoodvone.spend_x_get_x_discount.presenter.SpendXgetXdiscountPresenter;
import com.lexxdigital.easyfoodvone.spend_x_get_x_discount.view.SpendXgetXdiscountView;

public final class SpendXgetXdiscountPresenterImpl implements SpendXgetXdiscountPresenter, SpendXgetXdiscountInteractor.onSpendXgetXdiscountActivityListener {
    SpendXgetXdiscountView mView;
    SpendXgetXdiscountInteractor mInteractor;

    public SpendXgetXdiscountPresenterImpl(SpendXgetXdiscountView nView) {
        this.mView = nView;
        mInteractor = new SpendXgetXdiscountInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onSpendXgetXdiscountActivity(input, this, context);

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