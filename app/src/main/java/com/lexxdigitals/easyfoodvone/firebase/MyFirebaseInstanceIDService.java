package com.lexxdigitals.easyfoodvone.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e("RefreshedToken :", refreshedToken);
        UserPreferences.getUserPreferences().setString(this,Constants.FIREBASE_TOKEN,refreshedToken);


        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    public void updateToken(final Context context, final String refreshedToken){

        Log.e("Token : ", UserPreferences.getUserPreferences().getString(this, Constants.FIREBASE_TOKEN));

    }
}
