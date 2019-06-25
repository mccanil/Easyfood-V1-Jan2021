package com.lexxdigitals.easyfoodvone.single_order_report_detail.presenter.impl;

import android.content.Context;

import com.lexxdigitals.easyfoodvone.single_order_report_detail.interactor.OrderRepostDetailInteractor;
import com.lexxdigitals.easyfoodvone.single_order_report_detail.interactor.impl.OrderRepostDetailInteractorImpl;
import com.lexxdigitals.easyfoodvone.single_order_report_detail.presenter.OrderRepostDetailPresenter;
import com.lexxdigitals.easyfoodvone.single_order_report_detail.view.OrderRepostDetailView;

public final class OrderRepostDetailPresenterImpl implements OrderRepostDetailPresenter, OrderRepostDetailInteractor.onOrderRepostDetailActivityListener {
    OrderRepostDetailView mView;
    OrderRepostDetailInteractor mInteractor;

    public OrderRepostDetailPresenterImpl(OrderRepostDetailView nView) {
        this.mView = nView;
        mInteractor = new OrderRepostDetailInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onOrderRepostDetailActivity(input, this, context);

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