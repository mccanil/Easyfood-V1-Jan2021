package com.easyfoodvone.firebase;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.UserPreferences;


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
