package com.easyfoodvone.charity;


import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfoodvone.utility.PrefManager;
import com.google.gson.JsonObject;
import com.easyfoodvone.R;
import com.easyfoodvone.charity.webservice.ApiClient;
import com.easyfoodvone.charity.webservice.ApiInterface;
import com.easyfoodvone.charity.webservice.responsebean.CommonResponseBean;
import com.easyfoodvone.databinding.FragmentDonateBinding;
import com.easyfoodvone.databinding.LayoutSuccessDialogBinding;
import com.easyfoodvone.interfaces.RecyclerItemListener;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.orders.view.impl.OrdersActivity;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.Helper;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.UserPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.easyfoodvone.utility.Helper.isInternetOn;
import static com.easyfoodvone.utility.Helper.setFragment;
import static com.easyfoodvone.utility.Helper.showSnackBar;
import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class DonateFragment extends Fragment implements View.OnClickListener, RecyclerItemListener {

    private FragmentDonateBinding binding;
    private Context context;
    private DonationTimeAdapter donationTimeAdapter;
    private List<String> donationTimeList;
    private String donationTime = "";
    private LayoutSuccessDialogBinding successDialogBinding;
    PrefManager prefManager;

    public DonateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_donate, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager=PrefManager.getInstance(getActivity());
        OrdersActivity.getInstance().setBackAction(2);
        init();
        setAdapter();
        setListeners();


    }

    /**********User Defined Method for initializing *********/
    private void init() {
        donationTimeList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.donation_time)));
    }

    /**********User Defined Method for setting Adapter to recyclerview**********/
    private void setAdapter() {
        donationTimeAdapter = new DonationTimeAdapter((Activity) context, donationTimeList, this);
        binding.rvTime.setLayoutManager(new GridLayoutManager((Activity) context, 3));
        binding.rvTime.setAdapter(donationTimeAdapter);
    }


    /**********User Defined Method for setting listeners**********/
    private void setListeners() {
        binding.cvDonateNow.setOnClickListener(this);
        /***********No Of meals Edittext TextWatcher***********************/
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
            case R.id.cv_donate_now:
                if (isValid()) {
                    donateMeal();
                }
                break;
        }
    }

    /**********User Defined Method for checking validations**********/
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

    /**********User Defined Method for clearing Focus**********/
    private void clearFocus() {
        binding.etNoOfMeals.clearFocus();
    }

    @Override
    public void onItemClick(int position) {
        donationTime = donationTimeList.get(position);
        Helper.hideKeyboard((Activity) context);


    }

    /**********User Defined Method for Calling Api **********/
    private void donateMeal() {
        if (isInternetOn((Activity) context)) {
            final LoadingDialog dialog = new LoadingDialog((Activity) context, "");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = createDonateRequest();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<CommonResponseBean> call = apiInterface.donateMeal(prefManager.getPreference(AUTH_TOKEN,""),jsonObject);

            Log.e("Login Request", "" + jsonObject);
            call.enqueue(new Callback<CommonResponseBean>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull Response<CommonResponseBean> response) {
                    try {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            CommonResponseBean commonResponseBean = response.body();
                            if (commonResponseBean.isSuccess()) {
                                SuccessFragment successFragment = new SuccessFragment();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("IS_CANCEL", false);
                                successFragment.setArguments(bundle);
                                setFragment(successFragment, false, getActivity(), R.id.frameLayout);

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
        jsonObject.addProperty("restaurant_id", ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse((Activity) context, Constants.LOGIN_RESPONSE)).getRestaurant_id());
        jsonObject.addProperty("no_of_meals", binding.etNoOfMeals.getText().toString().trim());
        jsonObject.addProperty("ready_to_collect", donationTime);
        jsonObject.addProperty("is_collected", "0");
        return jsonObject;
    }
}
