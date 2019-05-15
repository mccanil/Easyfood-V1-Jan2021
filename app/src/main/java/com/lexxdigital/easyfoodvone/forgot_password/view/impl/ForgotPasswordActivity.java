package com.lexxdigital.easyfoodvone.forgot_password.view.impl;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.forgot_password.view.ForgotPasswordView;
import com.lexxdigital.easyfoodvone.forgot_password.presenter.ForgotPasswordPresenter;
import com.lexxdigital.easyfoodvone.login.models.LoginRequest;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.login.view.impl.LoginActivity;
import com.lexxdigital.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class ForgotPasswordActivity extends AppCompatActivity {
    EditText userEmail;
    Button resetPassword, tryAgain;
    ForgotPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        userEmail = findViewById(R.id.userEmail);
        tryAgain = findViewById(R.id.tryAgain);
        resetPassword = findViewById(R.id.resetPassword);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userEmail.getText().toString())) {
                    userEmail.setError("Enter email.");
                } else  if (!isValidEmailAddress(userEmail.getText().toString()))
                {
                    userEmail.setError("Please enter valid email address");
                }else
                    forgotPassword();
            }
        });

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    //TODO:  Login now....
    public void forgotPassword() {
        final LoadingDialog dialog = new LoadingDialog(this, "Requesting please wait...");
        dialog.setCancelable(false);
        dialog.show();
        try {


            LoginRequest request = new LoginRequest();
            request.setEmail(userEmail.getText().toString());

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.forgotPassword(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<LoginResponse>() {
                        @Override
                        public void onSuccess(LoginResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                alertDialog("If your email is correct, You will get a reset password link on your e-mail. \n Thank you");
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, data.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this,"Invalid Email!", Toast.LENGTH_LONG).show();
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(ForgotPasswordActivity.this, "Server not responding.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                finish();
            }
        });

        mDialog.show();
    }


}
