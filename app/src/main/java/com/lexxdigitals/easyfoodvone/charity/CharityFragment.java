package com.lexxdigitals.easyfoodvone.charity;
/*Created by Omnisttechhub Solution*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.charity.webservice.ApiClient;
import com.lexxdigitals.easyfoodvone.charity.webservice.ApiInterface;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CharityInfoBean;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CommonResponseBean;
import com.lexxdigitals.easyfoodvone.databinding.LayoutCancelDialogBinding;
import com.lexxdigitals.easyfoodvone.databinding.LayoutSuccessDialogBinding;
import com.lexxdigitals.easyfoodvone.interfaces.PreviousMealListener;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lexxdigitals.easyfoodvone.utility.Constants.CHARITY_STATUS_INTENT;
import static com.lexxdigitals.easyfoodvone.utility.Helper.isInternetOn;
import static com.lexxdigitals.easyfoodvone.utility.Helper.setFragment;
import static com.lexxdigitals.easyfoodvone.utility.Helper.showSnackBar;

/*Created by Omnisttechhub Solution*/

public class CharityFragment extends Fragment implements View.OnClickListener, PreviousMealListener, SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private Activity mActivity;
    private RecyclerView recyclerView;
    private PreviousCharityAdapter previousCharityAdapter;
    private TextView tv_target, tv_donated, tv_total_donated;
    private CardView cv_donate_meals;
    private LayoutCancelDialogBinding dialogBinding;
    private LayoutSuccessDialogBinding successDialogBinding;
    private AppCompatSeekBar seekBar;
    View view;
    private List<CharityInfoBean.MealDonatedBean.PreviousMealsBean> previousMealsBeans;
    private static CharityFragment instance = null;
    Dialog cancelDialog;
    private SwipeRefreshLayout swipe_refresh;

    @SuppressLint("ValidFragment")

    public CharityFragment(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public CharityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_charity, container, false);
        instance = this;
        IntentFilter intentFilter = new IntentFilter(CHARITY_STATUS_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_previous_donation);
        tv_target = (TextView) view.findViewById(R.id.tv_target);
        tv_donated = (TextView) view.findViewById(R.id.tv_donated);
        tv_total_donated = (TextView) view.findViewById(R.id.tv_total_donated);
        cv_donate_meals = (CardView) view.findViewById(R.id.cv_donate_meals);
        seekBar = (AppCompatSeekBar) view.findViewById(R.id.sb_meal);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        OrdersActivity.getInstance().setBackAction(1);

        init();
        getCharityInfo();
        setListeners();
        return view;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            previousMealsBeans.clear();
            getCharityInfo();

        }
    };

    /**********User Defined Method for initializing *********/
    private void init() {
        previousMealsBeans = new ArrayList<>();
    }

    public static CharityFragment getInstance() {
        return instance;
    }

    /**********User Defined Method to disable seekbar*********/
    private void disableSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int originalProgress;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing here..
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                originalProgress = seekBar.getProgress();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromUser) {
                if (fromUser == true) {
                    seekBar.setProgress(originalProgress);
                }
            }
        });
    }

    /**********User Defined Method to set click actions*********/
    private void setListeners() {
        tv_target.setOnClickListener(this);
        tv_donated.setOnClickListener(this);
        tv_total_donated.setOnClickListener(this);
        cv_donate_meals.setOnClickListener(this);

    }

    /**********User Defined Method to set adapters*********/
    private void setAdapter() {
        previousCharityAdapter = new PreviousCharityAdapter(getActivity(), this, previousMealsBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(previousCharityAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_donate_meals:
                DonateFragment donateFragment = new DonateFragment();
                setFragment(donateFragment, false, getActivity(), R.id.frameLayout);
                break;
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onCancel(int position) {
        CancelFragment cancelFragment = new CancelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("CHARITY_ID", previousMealsBeans.get(position).getId());
        cancelFragment.setArguments(bundle);
        setFragment(cancelFragment, false, getActivity(), R.id.frameLayout);
    }

    @Override
    public void onYes(int position) {
        updateCharityStatus(previousMealsBeans.get(position).getId(), 1);
    }

    @Override
    public void onNo(int position) {
        updateCharityStatus(previousMealsBeans.get(position).getId(), 2);

    }


    /************************* User defined method to call charity info api ******************************/
    public void getCharityInfo() {
        if (isInternetOn(getActivity())) {
            final LoadingDialog dialog = new LoadingDialog(getActivity(), "");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("restaurant_id", ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(getActivity(), Constants.LOGIN_RESPONSE)).getRestaurant_id());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<CharityInfoBean> call = apiInterface.getCharityDetail(jsonObject);

            Log.e("Login Request", "" + jsonObject);
            call.enqueue(new Callback<CharityInfoBean>() {
                @Override
                public void onResponse(@NonNull Call<CharityInfoBean> call, @NonNull Response<CharityInfoBean> response) {
                    try {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            CharityInfoBean charityInfoBean = response.body();
                            if (charityInfoBean.isSuccess()) {
                                tv_target.setText(charityInfoBean.getMeal_donated().getMeal_targeted());
                                tv_donated.setText(charityInfoBean.getMeal_donated().getNo_of_meals());
                                tv_total_donated.setText(charityInfoBean.getMeal_donated().getNo_of_meals());
                                float targetAchived = ((Float.valueOf(charityInfoBean.getMeal_donated().getNo_of_meals()) / Float.valueOf(charityInfoBean.getMeal_donated().getMeal_targeted())) * 100);
                                Log.e("Target Achived", "" + targetAchived);
                                seekBar.setProgress((int) targetAchived);
                                disableSeekBar();
                                if (charityInfoBean.getMeal_donated().getPrevious_meals() != null && charityInfoBean.getMeal_donated().getPrevious_meals().size() > 0) {
                                    previousMealsBeans = charityInfoBean.getMeal_donated().getPrevious_meals();
                                    setAdapter();
                                }


                            } else {
                                showSnackBar(view, charityInfoBean.getMessage());
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CharityInfoBean> call, @NonNull Throwable t) {
                    if (dialog != null)
                        dialog.dismiss();

                    showSnackBar(view, getString(R.string.msg_please_try_later));

                }
            });
        } else {
            showSnackBar(view, getString(R.string.no_internet_available_msg));

        }
    }


    /***************************************************XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX*******************************************************************/


    /***************************************************Update Charity Status APi*******************************************************************/


    private void updateCharityStatus(final String charityId, final int charityStatus) {
        if (isInternetOn(getActivity())) {
            final LoadingDialog dialog = new LoadingDialog(getActivity(), "");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = createUpdateReq(charityId, charityStatus);
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
                                if (charityStatus == 3) {

                                   /* Intent intent = new Intent(getActivity(), SucessActivity.class);
                                    intent.putExtra("IS_CANCEL", true);
                                    startActivity(intent);*/


                                    SuccessFragment successFragment = new SuccessFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("IS_CANCEL", true);
                                    successFragment.setArguments(bundle);
                                    setFragment(successFragment, false, getActivity(), R.id.frameLayout);


                                    // setFragment(donateFragment, false, getActivity(), R.id.frameLayout);


                                    cancelDialog.dismiss();
                                } else {
                                    getCharityInfo();
                                }

                            } else {
                                showSnackBar(view, commonResponseBean.getMessage());
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
                    showSnackBar(view, getString(R.string.msg_please_try_later));
                }
            });
        } else {
            showSnackBar(view, getString(R.string.no_internet_available_msg));

        }
    }


    private JsonObject createUpdateReq(String rowID, int status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("restaurant_id", ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(getActivity(), Constants.LOGIN_RESPONSE)).getRestaurant_id());
        jsonObject.addProperty("rowid", rowID);
        jsonObject.addProperty("status", status);
        return jsonObject;
    }

    @Override
    public void onRefresh() {
        swipe_refresh.setRefreshing(false);
        previousMealsBeans.clear();
        getCharityInfo();
    }
}
