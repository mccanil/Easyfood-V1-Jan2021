package com.lexxdigital.easyfoodvone.single_order_detail.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.single_order_detail.interactor.SingleOrderDetailInteractor;
import com.lexxdigital.easyfoodvone.single_order_detail.interactor.impl.SingleOrderDetailInteractorImpl;
import com.lexxdigital.easyfoodvone.single_order_detail.presenter.SingleOrderDetailPresenter;
import com.lexxdigital.easyfoodvone.single_order_detail.view.SingleOrderDetailView;

public final class SingleOrderDetailPresenterImpl implements SingleOrderDetailPresenter, SingleOrderDetailInteractor.onSingleOrderDetailActivityListener {
    SingleOrderDetailView mView;
    SingleOrderDetailInteractor mInteractor;

    public SingleOrderDetailPresenterImpl(SingleOrderDetailView nView) {
        this.mView = nView;
        mInteractor = new SingleOrderDetailInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onSingleOrderDetailActivity(input, this, context);

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