package com.lexxdigitals.easyfoodvone.charity;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.charity.webservice.ApiClient;
import com.lexxdigitals.easyfoodvone.charity.webservice.ApiInterface;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CommonResponseBean;
import com.lexxdigitals.easyfoodvone.databinding.FragmentCancelBinding;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lexxdigitals.easyfoodvone.utility.Helper.isInternetOn;
import static com.lexxdigitals.easyfoodvone.utility.Helper.setFragment;
import static com.lexxdigitals.easyfoodvone.utility.Helper.showSnackBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class CancelFragment extends Fragment implements View.OnClickListener {
    private FragmentCancelBinding binding;
    private Context context;
    private String charityId;

    public CancelFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cancel, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OrdersActivity.getInstance().setBackAction(2);
        setListeners();
        getDataFromIntent();
        setListeners();

    }

    /**********User Defined Method for getting data through Argument/Bundle *********/
    private void getDataFromIntent() {
        charityId = getArguments().getString("CHARITY_ID");
    }

    /**********User Defined Method for setting click listensers*********/
    private void setListeners() {
        binding.cvNo.setOnClickListener(this);
        binding.cvYes.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_yes:
                updateCharityStatus();
                break;
            case R.id.cv_no:
                CharityFragment charityFragment = new CharityFragment();
                setFragment(charityFragment, false, getActivity(), R.id.frameLayout);
                break;
        }

    }


    /***************************************************Update Charity Status APi*******************************************************************/


    private void updateCharityStatus() {
        if (isInternetOn(getActivity())) {
            final LoadingDialog dialog = new LoadingDialog(getActivity(), "");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = createUpdateReq();
            jsonObject.addProperty("restaurant_id", ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(getActivity(), Constants.LOGIN_RESPONSE)).getRestaurant_id());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<CommonResponseBean> call = apiInterface.updateMealStatus(jsonObject);

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
                                bundle.putBoolean("IS_CANCEL", true);
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


    private JsonObject createUpdateReq() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("restaurant_id", ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(getActivity(), Constants.LOGIN_RESPONSE)).getRestaurant_id());
        jsonObject.addProperty("rowid", charityId);
        jsonObject.addProperty("status", 3);
        return jsonObject;
    }


}
