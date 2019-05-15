package com.lexxdigital.easyfoodvone.orders.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.orders.interactor.OrdersInteractor;
import com.lexxdigital.easyfoodvone.orders.interactor.impl.OrdersInteractorImpl;
import com.lexxdigital.easyfoodvone.orders.presenter.OrdersPresenter;
import com.lexxdigital.easyfoodvone.orders.view.OrdersView;

public final class OrdersPresenterImpl implements OrdersPresenter, OrdersInteractor.onOrdersActivityListener {
    OrdersView mView;
    OrdersInteractor mInteractor;

    public OrdersPresenterImpl(OrdersView nView) {
        this.mView = nView;
        mInteractor = new OrdersInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onOrdersActivity(input, this, context);

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