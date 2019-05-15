package com.lexxdigital.easyfoodvone.discount_offer_with_percentage.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.discount_offer_with_percentage.interactor.DiscountOfferWithPercentageInteractor;
import com.lexxdigital.easyfoodvone.discount_offer_with_percentage.interactor.impl.DiscountOfferWithPercentageInteractorImpl;
import com.lexxdigital.easyfoodvone.discount_offer_with_percentage.presenter.DiscountOfferWithPercentagePresenter;
import com.lexxdigital.easyfoodvone.discount_offer_with_percentage.view.DiscountOfferWithPercentageView;

public final class DiscountOfferWithPercentagePresenterImpl implements DiscountOfferWithPercentagePresenter, DiscountOfferWithPercentageInteractor.onDiscountOfferWithPercentageActivityListener {
    DiscountOfferWithPercentageView mView;
    DiscountOfferWithPercentageInteractor mInteractor;

    public DiscountOfferWithPercentagePresenterImpl(DiscountOfferWithPercentageView nView) {
        this.mView = nView;
        mInteractor = new DiscountOfferWithPercentageInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onDiscountOfferWithPercentageActivity(input, this, context);

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