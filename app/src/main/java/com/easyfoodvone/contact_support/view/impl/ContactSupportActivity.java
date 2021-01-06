package com.easyfoodvone.contact_support.view.impl;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easyfoodvone.R;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.contact_support.models.SupportRequest;
import com.easyfoodvone.contact_support.models.SupportResponse;
import com.easyfoodvone.contact_support.view.ContactSupportView;
import com.easyfoodvone.contact_support.presenter.ContactSupportPresenter;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class ContactSupportActivity extends AppCompatActivity implements ContactSupportView {
    EditText input;
    Button btn_send_msg;
    EditText email, message;
    ContactSupportPresenter presenter;
    LoadingDialog loadingDialog;
    TextView contctSupport;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        prefManager = PrefManager.getInstance(ContactSupportActivity.this);

        email = findViewById(R.id.edit_email);
        message = findViewById(R.id.message);
        btn_send_msg = findViewById(R.id.btn_send_msg);
        contctSupport = findViewById(R.id.contctSupport);


        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString()))
                {
                    email.setError("Please enter email");
                    return;
                }else if (TextUtils.isEmpty(message.getText().toString()))
                {
                    message.setError("Please write your message");
                    return;
                }else if (!isValidEmailAddress(email.getText().toString()))
                {
                    email.setError("Please enter valid email");
                    return;
                }else
                sendToSupport();
            }
        });
        contctSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void setInputError() {
        input.setError("Username Empty");
    }

    @Override
    public void setNavigation() {
        // startActivity(new Intent(ContactSupportActivity.this, MainActivity.class));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View mDialogView = factory.inflate(R.layout.contact_support_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                finish();
//                Intent intent = new Intent(getApplicationContext(), ContactSupportActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

        mDialog.show();
    }

    public void showAlertDialog(View view) {
        alertDialog("Your message has been sent successfully.");
    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    public void goBack(View view) {
        finish();
    }

    //TODO:  Login now....
    public void sendToSupport() {
        loadingDialog = new LoadingDialog(this, "Processing your request...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        try {
            String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            SupportRequest request = new SupportRequest();
            request.setEmail(email.getText().toString());
            request.setMessage(message.getText().toString());

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.contactSupport(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<SupportResponse>()
                    {
                        @Override
                        public void onSuccess(SupportResponse data)
                        {
                            loadingDialog.dismiss();
                            if (data.isSuccess()) {
                                String request_id = data.getData().getRequest_id();
                                alertDialog("Your message has been sent successfully. \n Please note " + request_id + " request id for reference.");
                            } else {
                                Toast.makeText(ContactSupportActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingDialog.dismiss();
                            Toast.makeText(ContactSupportActivity.this, "Failed! try again.", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            loadingDialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(ContactSupportActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
