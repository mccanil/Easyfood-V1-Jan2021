package com.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.utility.PrefManager;
import com.google.gson.JsonObject;
import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterDeleveryCharges;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.helper.DecimalDigitsInputFilter;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.models.DeletePostCodeDeliveryTimeRequest;
import com.easyfoodvone.models.DeleverySetting;
import com.easyfoodvone.models.DeliverySettingRequest;
import com.easyfoodvone.models.UpdatePostCodeDeliveryTimeRequest;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.new_order.models.DeletePostCodeDeliveryTimeResponse;
import com.easyfoodvone.new_order.models.DeleverySettingResponse;
import com.easyfoodvone.new_order.models.DeliveryPostCodeBean;
import com.easyfoodvone.new_order.models.DeliverySettingResponse;
import com.easyfoodvone.new_order.models.UpdatePostCodeDeliveryTimeResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.easyfoodvone.utility.Helper.isInternetOn;
import static com.easyfoodvone.utility.Helper.showSnackBar;
import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;

public class DeleverySettingFragment extends Fragment implements AdapterDeleveryCharges.OnAdapterItemClickListener {
    View view;
    @BindView(R.id.distance)
    EditText distance;
    @BindView(R.id.delevery_time)
    EditText deleveryTime;
    @BindView(R.id.chechbox_set_for_all)
    CheckBox chechboxSetForAll;
    @BindView(R.id.recycler_time_list)
    RecyclerView recyclerTimeList;
    Unbinder unbinder;
    LoginResponse.Data baseData;
    LinearLayout liUpdateBtn;
    EditText etDeliveryCharge, etMinimumOrder, etFreeDelivery, etAvgPrepTime;

    PrefManager prefManager;
    private Context mContext;
    private AdapterDeleveryCharges mAdapter;

    private Activity mActivity;

    public DeleverySettingFragment() {
    }

    @SuppressLint("ValidFragment")
    public DeleverySettingFragment(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delevery_setting, container, false);
        prefManager = PrefManager.getInstance(getActivity());
        unbinder = ButterKnife.bind(this, view);
        liUpdateBtn = (LinearLayout) view.findViewById(R.id.liUpdateBtn);
        etMinimumOrder = (EditText) view.findViewById(R.id.edtMinOrder);
        etDeliveryCharge = (EditText) view.findViewById(R.id.edtDeliveryCharge);
        etAvgPrepTime = (EditText) view.findViewById(R.id.edtAvgTime);
        etFreeDelivery = (EditText) view.findViewById(R.id.edtFreeDelivery);

        etDeliveryCharge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        etMinimumOrder.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        etFreeDelivery.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});


        liUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getValid();

            }
        });

        chechboxSetForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CompoundButton) view).isChecked()) {
                    alertDialog("Enter the delivery charges for all the postcodes within " + distance.getText().toString() + " miles of radius for deliveries");
                } else {

                }
            }
        });


        distance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0)
                    if (Integer.parseInt(s.toString()) > 6 || Integer.parseInt(s.toString()) == 0) {
                        distance.setText("");
                        distance.setError(" Please Enter Distance between 0-6");
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        deleveryTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0)
                    if (Integer.parseInt(s.toString()) > 90) {
                        deleveryTime.setText("");
                        deleveryTime.setError("Please Enter Time between 0-90");
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAvgPrepTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0)
                    if (Integer.parseInt(s.toString()) > 90) {
                        etAvgPrepTime.setText("");
                        etAvgPrepTime.setError("Please Enter Time between 0-90");
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setAdapter();
        setRecyclerView();
        getDeleverySetting(Constants.getStoredData(getActivity()).getRestaurant_id());
        getPostCodeInfo(Constants.getStoredData(getActivity()).getRestaurant_id());
        return view;
    }

    private void getValid() {
        if (TextUtils.isEmpty(etDeliveryCharge.getText().toString())) {
            etDeliveryCharge.setError("Enter delivery charge");
            return;
        } else if (TextUtils.isEmpty(etMinimumOrder.getText().toString())) {
            etMinimumOrder.setError("Enter minimum order value");
            return;
        } else if (TextUtils.isEmpty(etFreeDelivery.getText().toString())) {
            etFreeDelivery.setError("Enter amount");
            return;
        } else {
            deliverySetting(Constants.getStoredData(getActivity()).getRestaurant_id(), deleveryTime.getText().toString(), distance.getText().toString(), etDeliveryCharge.getText().toString(),
                    etMinimumOrder.getText().toString(), etFreeDelivery.getText().toString(), etAvgPrepTime.getText().toString());

        }

    }


    public void deliverySetting(String restaurant_id, String deliveryTime, String distance, String deliveryCharge, String minimumOrder, String freeDelivery, String avgPreparationTime) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading Delivery Settings...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            DeliverySettingRequest request = new DeliverySettingRequest();
            request.setRestaurant_id(restaurant_id);
            request.setAverage_delivery_time(deliveryTime);
            request.setDistance(distance);
            request.setDelivery_charges(deliveryCharge);
            request.setMin_order_value(minimumOrder);
            request.setFree_delivery(freeDelivery);
            request.setAvg_preparation_time(avgPreparationTime);
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            final CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.deliverySettingResponse(prefManager.getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<DeliverySettingResponse>() {
                        @Override
                        public void onSuccess(DeliverySettingResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                alertDialoug(data.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
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

    //TODO:  Get Delevery Setting ....
    public void getDeleverySetting(String restaurentId) {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading Delivery Settings...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            DeleverySetting request = new DeleverySetting();
            request.setRestaurant_id(restaurentId);
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            final CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getDeleverySetting(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<DeleverySettingResponse>() {
                        @Override
                        public void onSuccess(DeleverySettingResponse data) {
                            dialog.dismiss();

                            if (data.isSuccess()) {
                               /* mAdapter = new AdapterDeleveryCharges(mContext, mActivity, DeleverySettingFragment.this, data.getData());
                                recyclerTimeList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                                recyclerTimeList.setAdapter(mAdapter);*/


                                distance.setText(data.getDistance() + "");
                                deleveryTime.setText(data.getAverage_delivery_time() + "");
                                etFreeDelivery.setText(data.getFree_delivery() + "");
                                etDeliveryCharge.setText(data.getDelivery_charges() + "");
                                etMinimumOrder.setText(data.getMin_order_value() + "");
                                etAvgPrepTime.setText(data.getAvg_preparation_time() + "");
                                chechboxSetForAll.setChecked(data.isSet_one_amount());

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
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


    private void getPostCodeInfo(String restaurentId) {
        if (isInternetOn(mContext)) {

            final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading Delivery Settings...");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("restaurant_id", restaurentId);
            ApiInterface apiInterface = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            Call<DeliveryPostCodeBean> call = apiInterface.getDeliveryPostCode(prefManager.getPreference(AUTH_TOKEN,""),jsonObject);

            call.enqueue(new Callback<DeliveryPostCodeBean>() {
                @Override
                public void onResponse(@NonNull Call<DeliveryPostCodeBean> call, @NonNull Response<DeliveryPostCodeBean> response) {
                    try {

                        if (dialog != null)
                            dialog.dismiss();
                        if (response.isSuccessful()) {
                            DeliveryPostCodeBean deliveryPostCodeBean = response.body();
                            if (deliveryPostCodeBean.isSuccess()) {
                                mAdapter = new AdapterDeleveryCharges(mContext, mActivity, DeleverySettingFragment.this, deliveryPostCodeBean.getData());
                                recyclerTimeList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                                recyclerTimeList.setAdapter(mAdapter);

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DeliveryPostCodeBean> call, @NonNull Throwable throwable) {
                    Log.e("prit", "" + throwable.getMessage());
                    if (dialog != null)
                        dialog.dismiss();
                    showSnackBar(view, getString(R.string.msg_please_try_later));
                }
            });
        } else {
            showSnackBar(view, getString(R.string.no_internet_available_msg));

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setAdapter() {


    }

    public void setRecyclerView() {

    }

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_set_delevery_charges, null);
        final AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);

        msgText.setText(msg);
        final EditText etMinOrder = mDialogView.findViewById(R.id.edtMinOrder);
        final EditText etDeliveryCharge = mDialogView.findViewById(R.id.edtDeliveryCharge);
        final EditText etFreeDelivery = mDialogView.findViewById(R.id.edtFreeDelivery);

        etMinOrder.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        etDeliveryCharge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        etFreeDelivery.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic

                if (TextUtils.isEmpty(etMinOrder.getText().toString())) {
                    etMinOrder.setError("Enter minimum order");
                    return;
                } else if (TextUtils.isEmpty(etDeliveryCharge.getText().toString())) {
                    etDeliveryCharge.setError("Enter delivery charge");
                    return;
                } else if (TextUtils.isEmpty(etFreeDelivery.getText().toString())) {
                    etFreeDelivery.setError(" Enter free delivery");
                    return;
                } else {
                    mDialog.dismiss();
                    updateAllPostCode(Constants.getStoredData(getActivity()).getRestaurant_id(), Constants.getStoredData(getActivity()).getPost_code(),
                            Double.valueOf(etMinOrder.getText().toString()), Double.valueOf(etDeliveryCharge.getText().toString()), Double.valueOf(etFreeDelivery.getText().toString()));
                }


            }
        });


        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onEditClick(int position, final DeliveryPostCodeBean.DataBean data, final AdapterDeleveryCharges.MyViewHolder holder) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_set_delevery_charges, null);
        final AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        final EditText etMinOrder = mDialogView.findViewById(R.id.edtMinOrder);
        final EditText etDeliveryCharge = mDialogView.findViewById(R.id.edtDeliveryCharge);
        final EditText etFreeDelivery = mDialogView.findViewById(R.id.edtFreeDelivery);
        etMinOrder.setText(data.getDelivery_min_value());
        etDeliveryCharge.setText(data.getShip_cost());
        etFreeDelivery.setText(data.getFree_delivery_over());

        msgText.setText("Update PostCode ");
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic

                if (TextUtils.isEmpty(etMinOrder.getText().toString())) {
                    etMinOrder.setError("Enter minimum order");
                    return;
                } else if (TextUtils.isEmpty(etDeliveryCharge.getText().toString())) {
                    etDeliveryCharge.setError("Enter delivery charge");
                    return;
                } else if (TextUtils.isEmpty(etFreeDelivery.getText().toString())) {
                    etFreeDelivery.setError(" Enter free delivery");
                    return;
                } else {
                    mDialog.dismiss();
                    updatePostCode(Constants.getStoredData(getActivity()).getRestaurant_id(), data.getPostcode(),
                            Double.valueOf(etMinOrder.getText().toString()), Double.valueOf(etDeliveryCharge.getText().toString()), Double.valueOf(etFreeDelivery.getText().toString()), holder);
                }


            }
        });

        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }


    @Override
    public void ondeleteClick(final int position, final DeliveryPostCodeBean.DataBean data, AdapterDeleveryCharges.MyViewHolder holder) {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View mDialogView = factory.inflate(R.layout.conf_deialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText("Are you sure, you want to delete " + data.getPostcode() + " postcode");
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();

                deletePostCode(Constants.getStoredData(getActivity()).getRestaurant_id(), data.getPostcode(), position);
            }
        });
        mDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

  /*  private void deletePostCode(String restaurant_id, String post_code, final int position) {


        final LoadingDialog dialog = new LoadingDialog(mActivity, "Deleting post-code...");
        dialog.setCancelable(false);
        dialog.show();
        try {

            DeletePostCodeDeliveryTimeRequest request = new DeletePostCodeDeliveryTimeRequest();
            request.setRestaurant_id(restaurant_id);
            request.setPost_code(post_code);
            ApiInterface apiService = ApiClient.getClient(mActivity).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.deletePostCodeDeliveryTime(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<DeletePostCodeDeliveryTimeResponse>() {
                        @Override
                        public void onSuccess(DeletePostCodeDeliveryTimeResponse data) {

                            dialog.dismiss();

                            if (data.isSuccess()) {
                                alertDialoug(data.getMessage());
                                mAdapter.remove(position);
                                mAdapter.notifyDataSetChanged();


                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(mActivity, "Loading failed ", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(mActivity, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }*/


    public void deletePostCode(String restaurant_id, String post_code, final int position) {

        if (isInternetOn(mContext)) {

            final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading Delivery Settings...");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("restaurant_id", restaurant_id);
            jsonObject.addProperty("postcode", post_code);
            ApiInterface apiInterface = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            Call<CommonResponse> call = apiInterface.deletDeliveryPostCode(prefManager.getPreference(AUTH_TOKEN,""),jsonObject);

            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    try {

                        if (dialog != null)
                            dialog.dismiss();
                        if (response.isSuccessful()) {
                            CommonResponse commonResponse = response.body();
                            if (commonResponse.isSuccess()) {
                                alertDialoug(commonResponse.getMessage());
                                mAdapter.remove(position);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(mContext, "" + commonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable throwable) {
                    Log.e("prit", "" + throwable.getMessage());
                    if (dialog != null)
                        dialog.dismiss();
                    showSnackBar(view, getString(R.string.msg_please_try_later));
                }
            });
        } else {
            showSnackBar(view, getString(R.string.no_internet_available_msg));

        }

    }


    public void updatePostCode(String restaurant_id, final String post_code, final double minOrder, final double deliveryCharge, final double freeDelivery, final AdapterDeleveryCharges.MyViewHolder holder) {

        if (isInternetOn(mContext)) {

            final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading Delivery Settings...");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("free_over", String.valueOf(freeDelivery));
            jsonObject.addProperty("min_delivery", String.valueOf(minOrder));
            jsonObject.addProperty("restaurant_id", restaurant_id);
            jsonObject.addProperty("ship_cost", String.valueOf(deliveryCharge));
            jsonObject.addProperty("postcode", post_code);
            ApiInterface apiInterface = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            Call<CommonResponse> call = apiInterface.updatePostCodeDelivery(prefManager.getPreference(AUTH_TOKEN,""),jsonObject);

            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    try {

                        if (dialog != null)
                            dialog.dismiss();
                        if (response.isSuccessful()) {
                            CommonResponse commonResponse = response.body();
                            if (commonResponse.isSuccess()) {
                                holder.txtMinOrderVal.setText("£" + minOrder + "");
                                holder.txtDeleveryCharge.setText("£" + deliveryCharge + "");
                                holder.txtFreeDelivery.setText("£" + freeDelivery + "");
                                alertDialoug(commonResponse.getMessage());
                            } else {
                                Toast.makeText(mContext, "" + commonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable throwable) {
                    Log.e("prit", "" + throwable.getMessage());
                    if (dialog != null)
                        dialog.dismiss();
                    showSnackBar(view, getString(R.string.msg_please_try_later));
                }
            });
        } else {
            showSnackBar(view, getString(R.string.no_internet_available_msg));

        }

    }

    public void alertDialouge(String msg) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                getDeleverySetting(Constants.getStoredData(getActivity()).getRestaurant_id());
                getPostCodeInfo(Constants.getStoredData(getActivity()).getRestaurant_id());
            }
        });

        mDialog.show();
    }


    public void alertDialoug(String msg) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();

            }
        });

        mDialog.show();
    }


    public void updateAllPostCode(String restaurant_id, final String post_code, final double minOrder, final double deliveryCharge, final double freeDelivery) {
        final LoadingDialog dialog = new LoadingDialog(mActivity, "Loading Delivery Settings...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            UpdatePostCodeDeliveryTimeRequest request = new UpdatePostCodeDeliveryTimeRequest();
            request.setRestaurant_id(restaurant_id);
            request.setPost_code(post_code);
            request.setMin_order_value(minOrder);
            request.setDelivery_charges(deliveryCharge);
            request.setFree_delivery_amount(freeDelivery);
            ApiInterface apiService = ApiClient.getClient(mActivity).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.updatAllPostCodeDelivery(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdatePostCodeDeliveryTimeResponse>() {

                        @Override
                        public void onSuccess(UpdatePostCodeDeliveryTimeResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                alertDialouge(data.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(mActivity, "Loading failed ", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
