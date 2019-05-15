package com.lexxdigital.easyfoodvone.new_order.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.new_order.interactor.NewOrderInteractor;
import com.lexxdigital.easyfoodvone.new_order.interactor.impl.NewOrderInteractorImpl;
import com.lexxdigital.easyfoodvone.new_order.presenter.NewOrderPresenter;
import com.lexxdigital.easyfoodvone.new_order.view.NewOrderView;

public final class NewOrderPresenterImpl implements NewOrderPresenter, NewOrderInteractor.onNewOrderActivityListener {
    NewOrderView mView;
    NewOrderInteractor mInteractor;

    public NewOrderPresenterImpl(NewOrderView nView) {
        this.mView = nView;
        mInteractor = new NewOrderInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onNewOrderActivity(input, this, context);

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