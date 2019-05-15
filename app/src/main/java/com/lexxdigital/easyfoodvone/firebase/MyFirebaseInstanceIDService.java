package com.lexxdigital.easyfoodvone.firebase;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


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

       /* FirebaseTokenUpdateRequest request = new FirebaseTokenUpdateRequest();
        request.setUserId(loginResponse.getResult().getUserId());
        request.setAccessToken(loginResponse.getResult().getAccessToken());
        request.setToken(refreshedToken);

        if (loginResponse.getResult().getUserId() != null && !loginResponse.getResult().getUserId().equals("")
                && refreshedToken != null && !refreshedToken.equals("")) {
            try {
                String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
                CompositeDisposable disposable = new CompositeDisposable();

                disposable.add(apiService.updateFirebaseToken(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String result) {
                                if (result.getResponseCode().equals("0")) {
                                    Log.e("token update", "Token Updated successfully");
                                    // Toast.makeText(ForgetPassword.this, "Please check your email for your password", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("onError", "onError: " + e.getMessage());
                            }
                        }));
            } catch (Exception ex) {
                Log.e("Token updat problem : ", ex.toString());
            }


        }*/


    }
}
