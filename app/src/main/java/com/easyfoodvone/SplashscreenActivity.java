package com.easyfoodvone;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.easyfoodvone.login.view.impl.LoginActivity;
import com.easyfoodvone.orders.view.impl.OrdersActivity;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.UserPreferences;
import com.newrelic.agent.android.NewRelic;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashscreenActivity extends AppCompatActivity {
    private static int TIME_OUT = 3000;
    UserPreferences userPreferences = UserPreferences.getUserPreferences();
    boolean LoginCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MjQ4ODA5NXxNT0JJTEV8QVBQTElDQVRJT058MTY3MTU3MDQ
        setContentView(R.layout.activity_splashscreen);
        NewRelic.withApplicationToken(
                "eu01xx8e8c3e4790f9795fc7133941ac935ff9a204"
        ).start(this.getApplication());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                try {
                    LoginCheck = UserPreferences.getUserPreferences().getBoolean(SplashscreenActivity.this, "LoginCheck");
                } catch (Exception e) {
                    LoginCheck = false;
                    e.printStackTrace();
                }
                if (LoginCheck) {
                    Constants.switchActivity(SplashscreenActivity.this, OrdersActivity.class);
                } else {
                    Constants.switchActivity(SplashscreenActivity.this, LoginActivity.class);
                }
                finish();
            }
        }, TIME_OUT);
    }
}
