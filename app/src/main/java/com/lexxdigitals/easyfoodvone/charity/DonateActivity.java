
/*Created by Omnisttechhub Solution*/
package com.lexxdigitals.easyfoodvone.charity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.JsonObject;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.charity.webservice.ApiClient;
import com.lexxdigitals.easyfoodvone.charity.webservice.ApiInterface;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CharityInfoBean;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CommonResponseBean;
import com.lexxdigitals.easyfoodvone.databinding.ActivityDonateBinding;
import com.lexxdigitals.easyfoodvone.databinding.LayoutSuccessDialogBinding;
import com.lexxdigitals.easyfoodvone.interfaces.RecyclerItemListener;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.Helper;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lexxdigitals.easyfoodvone.utility.Helper.isInternetOn;
import static com.lexxdigitals.easyfoodvone.utility.Helper.showSnackBar;

public class DonateActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemListener {
    private ActivityDonateBinding binding;
    private DonationTimeAdapter donationTimeAdapter;
    private List<String> donationTimeList;
    private String donationTime = "";
    private LayoutSuccessDialogBinding successDialogBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donate);
        init();
        setAdapter();
        setListeners();
    }
    /**********User Defined Method for initializing *********/
    private void init() {
        donationTimeList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.donation_time)));
    }

    private void setAdapter() {
        donationTimeAdapter = new DonationTimeAdapter(DonateActivity.this, donationTimeList, this);
        binding.rvTime.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvTime.setAdapter(donationTimeAdapter);
    }

    private void setListeners() {
        binding.ivLogo.setOnClickListener(this);
        binding.cvDonateNow.setOnClickListener(this);
        binding.etNoOfMeals.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0 && editable != null) {
                    if (!editable.toString().trim().isEmpty() && Integer.parseInt(editable.toString().trim()) > 0) {
                        binding.tvMealLabel.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvMealLabel.setVisibility(View.GONE);
                    }
                } else {
                    binding.tvMealLabel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_logo:
                startActivity(new Intent(DonateActivity.this, OrdersActivity.class));
                finishAffinity();
                break;
            case R.id.cv_donate_now:
                if (isValid()) {
                    donateMeal();
                }
                break;
        }
    }


    private boolean isValid() {
        if (binding.etNoOfMeals.getText().toString().trim().isEmpty()) {
            clearFocus();
            binding.etNoOfMeals.requestFocus();
            showSnackBar(binding.getRoot(), getString(R.string.please_enter_no_of_meals));
            return false;
        } else if (Double.parseDouble(binding.etNoOfMeals.getText().toString().trim()) < 1) {
            clearFocus();
            binding.etNoOfMeals.requestFocus();
            showSnackBar(binding.getRoot(), getString(R.string.please_enter_valid_no_of_meals));
            return false;
        } else if (donationTime == "") {
            clearFocus();
            binding.rvTime.requestFocus();
            showSnackBar(binding.getRoot(), getString(R.string.please_select_time));
            return false;
        }
        return true;
    }

    private void clearFocus() {
        binding.etNoOfMeals.clearFocus();
    }

    @Override
    public void onItemClick(int position) {
        // Log.e("DonationTime Size", "" + donationTimeList.size());
        //Log.e("Donation Time", "" + donationTimeList.get(position));
        donationTime = donationTimeList.get(position);
        Helper.hideKeyboard(DonateActivity.this);


    }


    /***************************************************Pop Dialog for   sucess*******************************************************************/

    public void sucessDialog() {
        View dialogView = LayoutInflater.from(DonateActivity.this).inflate(R.layout.layout_success_dialog, null);
        successDialogBinding = DataBindingUtil.bind(dialogView);
        final Dialog dialog = new Dialog(DonateActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(successDialogBinding.getRoot());
        dialog.setCancelable(false);
        successDialogBinding.tvThank.setVisibility(View.VISIBLE);
        successDialogBinding.tvSurity.setText(getResources().getString(R.string.we_have_now_alerted_local));
        successDialogBinding.tvSucessMsg.setText(getResources().getString(R.string.doantion_sucess_msg));

        successDialogBinding.cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.seme_transparent)));
    }

    /***************************************************XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX*******************************************************************/


    private void donateMeal() {
        if (isInternetOn(DonateActivity.this)) {
            final LoadingDialog dialog = new LoadingDialog(DonateActivity.this, "");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = createDonateRequest();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<CommonResponseBean> call = apiInterface.donateMeal(jsonObject);

            Log.e("Login Request", "" + jsonObject);
            call.enqueue(new Callback<CommonResponseBean>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull Response<CommonResponseBean> response) {
                    try {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            CommonResponseBean commonResponseBean = response.body();
                            if (commonResponseBean.isSuccess()) {
                                finish();
                                Intent intent = new Intent(DonateActivity.this, SucessActivity.class);
                                intent.putExtra("IS_CANCEL", false);
                                startActivity(intent);

                            } else {
                                showSnackBar(binding.getRoot(), commonResponseBean.getMessage());
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                    if (dialog != null)
                        dialog.dismiss();
                    showSnackBar(binding.getRoot(), getString(R.string.msg_please_try_later));
                }
            });
        } else {
            showSnackBar(binding.getRoot(), getString(R.string.no_internet_available_msg));

        }
    }


    private JsonObject createDonateRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("restaurant_id", ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(DonateActivity.this, Constants.LOGIN_RESPONSE)).getRestaurant_id());
        jsonObject.addProperty("no_of_meals", binding.etNoOfMeals.getText().toString().trim());
        jsonObject.addProperty("ready_to_collect", donationTime);
        jsonObject.addProperty("is_collected", "0");


        return jsonObject;
    }


}
