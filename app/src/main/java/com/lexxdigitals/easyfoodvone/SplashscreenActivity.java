package com.lexxdigitals.easyfoodvone;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.lexxdigitals.easyfoodvone.login.view.impl.LoginActivity;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashscreenActivity extends AppCompatActivity {
    private static int TIME_OUT = 3000;
    UserPreferences userPreferences = UserPreferences.getUserPreferences();
    boolean LoginCheck=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


//                if (userPreferences.getResponse(SplashscreenActivity.this, Constants.LOGIN_RESPONSE) != null) {
//                    Constants.switchActivity(SplashscreenActivity.this, OrdersActivity.class);
//                } else {
//                    Constants.switchActivity(SplashscreenActivity.this, LoginActivity.class);
//                }

                try{
                    LoginCheck=UserPreferences.getUserPreferences().getBoolean(SplashscreenActivity.this,"LoginCheck");
                }catch (Exception e){
                    LoginCheck=false;
                    e.printStackTrace();
                }
                if (LoginCheck){
                    Constants.switchActivity(SplashscreenActivity.this, OrdersActivity.class);
                } else {
                    Constants.switchActivity(SplashscreenActivity.this, LoginActivity.class);
                }
                finish();
            }
        }, TIME_OUT);
    }
}
