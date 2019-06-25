package com.lexxdigitals.easyfoodvone.contact_support.presenter.impl;

import android.content.Context;

import com.lexxdigitals.easyfoodvone.contact_support.interactor.ContactSupportInteractor;
import com.lexxdigitals.easyfoodvone.contact_support.interactor.impl.ContactSupportInteractorImpl;
import com.lexxdigitals.easyfoodvone.contact_support.presenter.ContactSupportPresenter;
import com.lexxdigitals.easyfoodvone.contact_support.view.ContactSupportView;

public final class ContactSupportPresenterImpl implements ContactSupportPresenter, ContactSupportInteractor.onContactSupportActivityListener {
    ContactSupportView mView;
    ContactSupportInteractor mInteractor;

    public ContactSupportPresenterImpl(ContactSupportView nView) {
        this.mView = nView;
        mInteractor = new ContactSupportInteractorImpl();
    }

    @Override
    public void method_name(String input, Context context) {

        mInteractor.onContactSupportActivity(input, this, context);

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