package com.lexxdigital.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.login.view.impl.LoginActivity;
import com.lexxdigital.easyfoodvone.models.ChangePasswordRequest;
import com.lexxdigital.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.lexxdigital.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;

import javax.microedition.khronos.egl.EGLDisplay;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private Activity mActivity;
    private EditText edt_new_password, edt_confirm_paswword,edit_current_password;
    private Button btnReset, btnCancel;
    private View view;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ChangePasswordFragment(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        findId();
        return view;
    }

    public void findId() {
        edt_new_password = (EditText) view.findViewById(R.id.edit_new_password);
        edt_confirm_paswword = (EditText) view.findViewById(R.id.edit_confirm_password);
        btnCancel = (Button) view.findViewById(R.id.btn_contact_support);
        btnReset = (Button) view.findViewById(R.id.btn_reset);
        edit_current_password =  view.findViewById(R.id.edit_current_password);

        //****set listener********
        btnReset.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_reset: {

                String newPassword = edt_new_password.getText().toString();
                String confirmPassword = edt_confirm_paswword.getText().toString();
                String currentPass = edit_current_password.getText().toString();

                if (TextUtils.isEmpty(currentPass))
                {
                    edit_current_password.setError("Enter current password");
                    return;
                } else if (TextUtils.isEmpty(newPassword))
                {
                    edt_new_password.setError("Enter new password");
                    return;
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    edt_confirm_paswword.setError("Enter confirm password");
                    return;
                } else if (!newPassword.equals(confirmPassword)) {
                    edt_confirm_paswword.setError("Password not matching");
                    return;
                } else {
                    changePassword();
                }
                break;
            }
            case R.id.btn_contact_support: {

                Intent intent = new Intent(getActivity(), OrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
    }

    private void changePassword() {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Changing please wait...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setUser_id(Constants.getStoredData(getActivity()).getUser_id());
            request.setCurrent_password(edit_current_password.getText().toString());
            request.setPassword(edt_new_password.getText().toString());
            request.setPassword_confirmation(edt_confirm_paswword.getText().toString());

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.changePassword(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>()
                    {
                        @Override
                        public void onSuccess(CommonResponse data)
                        {
                            dialog.dismiss();
                            if (data.isSuccess())
                            {
                                Toast.makeText(mContext, data.getMessage(), Toast.LENGTH_SHORT).show();
                                edit_current_password.setText("");
                                edt_new_password.setText("");
                                edt_confirm_paswword.setText("");
                            }else {
                                Toast.makeText(mContext, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));


        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
