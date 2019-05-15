package com.lexxdigital.easyfoodvone.create_combo_meals_offer.presenter.impl;

import android.content.Context;

import com.lexxdigital.easyfoodvone.create_combo_meals_offer.interactor.CreateComboMealsOfferInteractor;
import com.lexxdigital.easyfoodvone.create_combo_meals_offer.interactor.impl.CreateComboMealsOfferInteractorImpl;
import com.lexxdigital.easyfoodvone.create_combo_meals_offer.presenter.CreateComboMealsOfferPresenter;
import com.lexxdigital.easyfoodvone.create_combo_meals_offer.view.CreateComboMealsOfferView;

public final class CreateComboMealsOfferPresenterImpl implements CreateComboMealsOfferPresenter, CreateComboMealsOfferInteractor.onCreateComboMealsOfferActivityListener {
    CreateComboMealsOfferView mView;
    CreateComboMealsOfferInteractor mInteractor;

    public CreateComboMealsOfferPresenterImpl(CreateComboMealsOfferView nView) {
        this.mView = nView;
        mInteractor = new CreateComboMealsOfferInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onCreateComboMealsOfferActivity(input, this, context);

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