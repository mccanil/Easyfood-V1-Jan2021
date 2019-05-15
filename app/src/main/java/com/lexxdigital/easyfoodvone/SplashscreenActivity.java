package com.lexxdigital.easyfoodvone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.lexxdigital.easyfoodvone.login.view.impl.LoginActivity;
import com.lexxdigital.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashscreenActivity extends AppCompatActivity {
    private static int TIME_OUT = 3000;
    UserPreferences userPreferences = UserPreferences.getUserPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (userPreferences.getResponse(SplashscreenActivity.this, Constants.LOGIN_RESPONSE) != null) {
                    Constants.switchActivity(SplashscreenActivity.this, OrdersActivity.class);
                } else
                    Constants.switchActivity(SplashscreenActivity.this, LoginActivity.class);

                finish();
            }
        }, TIME_OUT);
    }
}
