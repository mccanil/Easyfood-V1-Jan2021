package com.lexxdigital.easyfoodvone.login.view.impl;

import android.Manifest;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.contact_support.view.impl.ContactSupportActivity;
import com.lexxdigital.easyfoodvone.forgot_password.view.impl.ForgotPasswordActivity;
import com.lexxdigital.easyfoodvone.login.models.LoginRequest;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.login.presenter.SigninPresenter;
import com.lexxdigital.easyfoodvone.menu.CommonRequest;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        //TODO: Initialization.......

        edit_username = findViewById(R.id.edit_username);
        edit_password = findViewById(R.id.edit_password);
        btn_login = findViewById(R.id.btn_login);
        btn_contact_support = findViewById(R.id.btn_contact_support);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);

//        edit_username.setText("rahul.saini@lexxdigital.com");
//        edit_password.setText("R@hul6789");
        edit_username.setText("raj.dua74@gmail.com");
        edit_password.setText("secret");
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
