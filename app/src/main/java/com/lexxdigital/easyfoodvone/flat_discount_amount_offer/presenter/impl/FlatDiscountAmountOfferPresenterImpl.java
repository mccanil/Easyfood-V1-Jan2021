package com.lexxdigital.easyfoodvone.flat_discount_amount_offer.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.flat_discount_amount_offer.interactor.FlatDiscountAmountOfferInteractor;
import com.lexxdigital.easyfoodvone.flat_discount_amount_offer.interactor.impl.FlatDiscountAmountOfferInteractorImpl;
import com.lexxdigital.easyfoodvone.flat_discount_amount_offer.presenter.FlatDiscountAmountOfferPresenter;
import com.lexxdigital.easyfoodvone.flat_discount_amount_offer.view.FlatDiscountAmountOfferView;

public final class FlatDiscountAmountOfferPresenterImpl implements FlatDiscountAmountOfferPresenter, FlatDiscountAmountOfferInteractor.onFlatDiscountAmountOfferActivityListener {
    FlatDiscountAmountOfferView mView;
    FlatDiscountAmountOfferInteractor mInteractor;

    public FlatDiscountAmountOfferPresenterImpl(FlatDiscountAmountOfferView nView) {
        this.mView = nView;
        mInteractor = new FlatDiscountAmountOfferInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onFlatDiscountAmountOfferActivity(input, this, context);

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