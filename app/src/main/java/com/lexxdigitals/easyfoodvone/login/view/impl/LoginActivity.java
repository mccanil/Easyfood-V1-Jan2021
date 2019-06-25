package com.lexxdigitals.easyfoodvone.login.view.impl;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.contact_support.view.impl.ContactSupportActivity;
import com.lexxdigitals.easyfoodvone.forgot_password.view.impl.ForgotPasswordActivity;
import com.lexxdigitals.easyfoodvone.login.models.LoginRequest;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.login.presenter.SigninPresenter;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;


public class LoginActivity extends AppCompatActivity {
    EditText input, edit_username, edit_password;
    Button btn_login, btn_contact_support;
    SigninPresenter presenter;
    TextView txt_forgot_password;
    LoginResponse loginResponse;
    LoadingDialog dialog;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        if (UserPreferences.getUserPreferences().getString(LoginActivity.this, Constants.FIREBASE_TOKEN) == null) {
            UserPreferences.getUserPreferences().setString(this, Constants.FIREBASE_TOKEN, FirebaseInstanceId.getInstance().getToken());
        }
        //TODO: Initialization.......

        edit_username = findViewById(R.id.edit_username);
        edit_password = findViewById(R.id.edit_password);
        btn_login = findViewById(R.id.btn_login);
        btn_contact_support = findViewById(R.id.btn_contact_support);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);

//        edit_username.setText("rahul.saini@lexxdigital.com");
//        edit_password.setText("R@hul6789");

        setOnClickListeners();


        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            String[] perms = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.CALL_PHONE
            };

            if (!EasyPermissions.hasPermissions(this, perms)) {
                EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", 101, perms);
            }
        }

       /* findViewById(R.id.img_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 7) {

                    edit_username.setText("subway@gmail.com");
                    edit_password.setText("123456");
                } else {
                    count++;
                }
            }
        });*/

    }

    private void setOnClickListeners() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edit_username.getText().toString())) {
                    edit_username.setError("Enter user name");
                    return;
                } else if (TextUtils.isEmpty(edit_password.getText().toString())) {
                    edit_password.setError("Enter password");
                    return;
                } else {
                    btn_login.setClickable(false);
                    loginNow();
                }
            }
        });
    }

    //TODO:  Login now....
    public void loginNow() {
        dialog = new LoadingDialog(LoginActivity.this, "Logging you please wait...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            LoginRequest request = new LoginRequest();
            request.setDevice_id(android_id);
            request.setDevice_type("Android");
            request.setEmail(edit_username.getText().toString());
            request.setPassword(edit_password.getText().toString());
            request.setFirebase_id(UserPreferences.getUserPreferences().getString(LoginActivity.this, Constants.FIREBASE_TOKEN) != null ? UserPreferences.getUserPreferences().getString(LoginActivity.this, Constants.FIREBASE_TOKEN) : "");


            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.login(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<LoginResponse>() {
                        @Override
                        public void onSuccess(LoginResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {

                                loginResponse = data;
                                UserPreferences.getUserPreferences().setBoolean(LoginActivity.this,"LoginCheck",true);
                                String LogoUrl=loginResponse.getData().getLogo();
                                UserPreferences.getUserPreferences().setString(LoginActivity.this,"LogoUrl",LogoUrl);
                                Log.e("Login response ", ">>>>>>>>>>  " + data.toString());
                                UserPreferences.getUserPreferences().setResponse(LoginActivity.this, Constants.LOGIN_RESPONSE, data.getData());
                                Constants.switchActivity(LoginActivity.this, OrdersActivity.class);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                                btn_login.setClickable(true);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            btn_login.setClickable(true);
                            Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            btn_login.setClickable(true);
            Log.e("Exception ", e.toString());
            Toast.makeText(LoginActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    public void goForgot(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void goContactSupport(View view) {
        startActivity(new Intent(LoginActivity.this, ContactSupportActivity.class));
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

}
