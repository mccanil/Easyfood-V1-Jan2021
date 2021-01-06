package com.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.models.RestaurantClosingTimeByDataModel;
import com.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.new_order.models.TimeSlotRequest;
import com.easyfoodvone.orders.adapter.AdapterOrderList;
import com.easyfoodvone.orders.models.OrdersListResponse;
import com.easyfoodvone.orders.models.OrdersRequest;
import com.easyfoodvone.orders.view.impl.OrdersActivity;
import com.easyfoodvone.utility.ApplicationContext;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.Helper;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;
import com.easyfoodvone.utility.UserPreferences;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.Constants.NOTIFICATION_TYPE_ACCEPTED;
import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class OrderListFragment extends Fragment implements AdapterOrderList.OnItemClickListener, Constants.DialogClickedListener {
    View view;
    @BindView(R.id.tab_new)
    TextView tabNew;
    @BindView(R.id.tab_new_count)
    TextView tabNewCount;
    TextView tabAccepted;
    TextView tabAcceptedCount;
    @BindView(R.id.tab_rejected)
    TextView tabRejected;
    @BindView(R.id.tab_rejected_count)
    TextView tabRejectedCount;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    LinearLayout newTab, acceptedTab, rejectedTab;

    @BindView(R.id.recyclerview_orders)
    RecyclerView recyclerviewOrders;
    Unbinder unbinder;
    private AdapterOrderList mAdapter;
    private Context mContext;
    Context context;
    public static String limitFrom = "0";
    public static String limitTo = "100";
    SwipeRefreshLayout swipeRefreshLayout;

    private static String[] toDayList;
    private static List<String> toDayDataList;
    private static List<String> dateTimeDataList;
    String toDay;
    Spinner todaySpinner;
    ProgressBar progressBar;
    String deliveryDateTime = null;

    TextView btnRemove;
    TextView btnAdd;
    int mMinutes = 0;
    int mHour = 0;
    int minCHange = 0;

    int itemPosition = 0;
    static OrderListFragment orderListFragment;

    long restaurantCloseMin = -1;

    //TODO: 1 = new orders , 2= accepted orders, 3 = rejected orders
    int whichTabActive = 1;


    //TODO Loading first time ==
    boolean firstTimeLoading = true;
    PrefManager prefManager;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public OrderListFragment() {
    }

    @SuppressLint("ValidFragment")
    public OrderListFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderListFragment = this;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        prefManager = PrefManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter(NOTIFICATION_TYPE_ACCEPTED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        newTab = view.findViewById(R.id.newTab);
        acceptedTab = view.findViewById(R.id.acceptedTab);
        rejectedTab = view.findViewById(R.id.rejectedTab);

        tabAcceptedCount = view.findViewById(R.id.tab_accepted_count);
        tabAccepted = view.findViewById(R.id.tab_accepted);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() != null)
                    handleTabs(whichTabActive);
            }
        });
        mAdapter = new AdapterOrderList(mContext, this);

        unbinder = ButterKnife.bind(this, view);
        recyclerviewOrders.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerviewOrders.setAdapter(mAdapter);
        onTabClickEvents();

        return view;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() != null)
                handleTabs(whichTabActive);
            //  if (whichTabActive == 1) {
            //  mAdapter.removeAll();
            //getAllOrders("new", whichTabActive);
            // }

        }
    };

    private void getAllOrders(final String orders, final int whichTabActive) {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "");
        dialog.setCancelable(false);
        dialog.show();

        try {
            firstTimeLoading = false;
            OrdersRequest request = new OrdersRequest();
            request.setRestaurant_id(((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(context, Constants.LOGIN_RESPONSE)).getRestaurant_id());
            request.setLimit(limitTo);
            request.setOffset(limitFrom);
            request.setStatus(orders);
            Log.e("prinToken", "" + ((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(context, Constants.LOGIN_RESPONSE)).getToken());
            ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrders(((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(context, Constants.LOGIN_RESPONSE)).getToken(), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrdersListResponse>() {
                        @Override
                        public void onSuccess(OrdersListResponse data) {

                            dialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            if (data.isSuccess()) {

                                try {
                                    if (!data.getData().getAndroid_version().equals(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName)) {
                                        updateDialog();

                                    } else {
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                mAdapter.clearData();
                                tabAcceptedCount.setText(data.getData().getTotal_accepted_order() + "");
                                tabRejectedCount.setText(data.getData().getTotal_rejected_order() + "");
                                tabNewCount.setText(data.getData().getTotal_new_order() + "");
                                mAdapter.addAllItems(data.getData().getOrders(), orders);
                                mAdapter.setWhichTabActive(whichTabActive);

                                OrdersActivity.getInstance().updateRestaurantStatus(data.getData().isIs_open());

                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(context, "Server connection failed", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            dialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            Log.e("Exception ", e.toString());
            Toast.makeText(context, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onTabClickEvents() {

        tabNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whichTabActive = 1;
                if (getActivity() != null)
                    handleTabs(1);
            }
        });

        tabAccepted.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whichTabActive = 2;
                if (getActivity() != null)
                    handleTabs(2);
            }
        });

        tabRejected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whichTabActive = 3;
                if (getActivity() != null)
                    handleTabs(3);
            }
        });


    }


    private void handleTabs(int whichTabActive) {
        switch (whichTabActive) {
            case 1:
                mAdapter.removeAll();
                getAllOrders("new", whichTabActive);

                this.whichTabActive = 1;

                newTab.setBackground(getResources().getDrawable(R.color.bg_login_end));
                rejectedTab.setBackground(getResources().getDrawable(R.color.white));
                acceptedTab.setBackground(getResources().getDrawable(R.color.white));

                break;
            case 2:
                mAdapter.removeAll();
                getAllOrders("accepted", whichTabActive);

                this.whichTabActive = 2;
                newTab.setBackground(getResources().getDrawable(R.color.white));
                rejectedTab.setBackground(getResources().getDrawable(R.color.white));
                acceptedTab.setBackground(getResources().getDrawable(R.color.bg_login_end));

                break;

            case 3:
                mAdapter.removeAll();
                getAllOrders("rejected", whichTabActive);
                this.whichTabActive = 3;
                newTab.setBackground(getResources().getDrawable(R.color.white));
                rejectedTab.setBackground(getResources().getDrawable(R.color.bg_login_end));
                acceptedTab.setBackground(getResources().getDrawable(R.color.white));

                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (firstTimeLoading) {
            if (getActivity() != null)
                getAllOrders("new", 1);
        } else {
            if (getActivity() != null)
                handleTabs(whichTabActive);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            ApplicationContext.getInstance().stopNotificationSound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ApplicationContext.getInstance().stopNotificationSound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAcceptClick(final OrdersListResponse.Orders orderDetail, int position) {

        final int mPosition = position;
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View mDialogView = factory.inflate(R.layout.change_delevery_time_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(mDialogView);
        final LinearLayout layoutTime = (LinearLayout) mDialogView.findViewById(R.id.set_time);
        final LinearLayout layoutNotes = (LinearLayout) mDialogView.findViewById(R.id.notes);
        btnRemove = mDialogView.findViewById(R.id.txt_remove);
        btnAdd = mDialogView.findViewById(R.id.btn_add);
        final TextView txtResult = mDialogView.findViewById(R.id.txt_result);
        final TextView tvMessage = mDialogView.findViewById(R.id.message);
        btnRemove.setEnabled(false);
        btnAdd.setEnabled(false);
        tvMessage.setText(Helper.formatDate(orderDetail.getDelivery_date_time()));
        ProgressBar progressBar = mDialogView.findViewById(R.id.progressBar);
        getRestaurantClosingTimeByDate(progressBar, orderDetail.getRestaurant_id(), orderDetail.getDelivery_date_time());
        txtResult.setText(Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "yyyy-MM-dd hh:mm a", "mm"));
        txtResult.setText("0");
        String dt = orderDetail.getDelivery_date_time();
        String hr = "", min = "";

        for (int i = 0; i < dt.length(); i++) {
            char c = dt.charAt(i);
            if (c == ' ') {
                hr = dt.substring(i + 1, i + 3);
            }
            if (c == ':') {
                min = dt.substring(i + 1, i + 3);
                break;
            }
        }
        Log.e("dt", hr + " " + min);


        String dddate = Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "yyyy-MM-dd hh:mm a", "dd MMMM, yyyy") + "\n";


        tvMessage.setText(Helper.formatDate(orderDetail.getDelivery_date_time()));
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantCloseMin > 0) {
                    if ((Integer.parseInt(txtResult.getText().toString())) > 0) {
                        minCHange = minCHange - 5;
                        txtResult.setText(String.valueOf(Integer.parseInt(txtResult.getText().toString()) - 5));
                        mMinutes = mMinutes - 5;
                        if (mMinutes < 5) {
                            mHour = mHour - 1;
                            mMinutes = mMinutes + 60;
                            tvMessage.setText(Helper.formatDate(Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "yyyy-MM-dd hh:mm a", "dd MMMM, yyyy") )+ "\n" + new DecimalFormat("00").format(mHour) + ":" + new DecimalFormat("00").format(mMinutes));
                        } else {
                            tvMessage.setText(Helper.formatDate(Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "yyyy-MM-dd hh:mm a", "dd MMMM, yyyy")) + "\n" + new DecimalFormat("00").format(mHour) + ":" + new DecimalFormat("00").format(mMinutes));
                        }

                    }
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (restaurantCloseMin > 0) {

                    String checkTime = txtResult.getText().toString();
                    if (restaurantCloseMin > (Integer.parseInt(checkTime) + 5)) {
                        minCHange = minCHange + 5;
                        txtResult.setText(String.valueOf(Integer.parseInt(txtResult.getText().toString()) + 5));
                        mMinutes = mMinutes + 5;
                        if (mMinutes >= 60) {
                            mHour = mHour + (mMinutes / 60);
                            mMinutes = mMinutes % 60;
                            String setTime = Helper.formatDate(Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "yyyy-MM-dd hh:mm a", "dd MMMM, yyyy")) + "\n" + new DecimalFormat("00").format(mHour) + ":" + new DecimalFormat("00").format(mMinutes);
                            tvMessage.setText(setTime);
                        } else {
                            String setTime2 = Helper.formatDate(Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "yyyy-MM-dd hh:mm a", "dd MMMM, yyyy")) + "\n" + new DecimalFormat("00").format(mHour) + ":" + new DecimalFormat("00").format(mMinutes);
                            tvMessage.setText(setTime2);
                        }

                    }
                }


            }
        });

        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();


                try {
                    ApplicationContext.getInstance().stopNotificationSound();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                acceptRejectOrder("accepted", orderDetail, orderDetail.getDelivery_date_time(), "", mPosition);

            }
        });
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minCHange = 0;
                mDialog.dismiss();

            }
        });

        mDialog.show();
    }

    int selectPos = 0;

    @Override
    public void onRejectClick(final OrdersListResponse.Orders orderDetail, final int position) {
        {

            final String[] reasons = new String[]{"Select reason", "Too busy to fulfill order", "Items not in stock", "Delivery driver unavailable ", "Closing soon", "collection only", "Other"};
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View mDialogView = factory.inflate(R.layout.reject_confirmation_dialog, null);


            final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
            mDialog.setView(mDialogView);
            final EditText notes = (EditText) mDialogView.findViewById(R.id.notes);
            final Spinner spinnerMainItem = mDialogView.findViewById(R.id.spinner_main_item);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, reasons);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMainItem.setAdapter(spinnerArrayAdapter);

            spinnerMainItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectPos = position;
                    if (position == 0) {
                        notes.setVisibility(View.GONE);
                        notes.setText("");
                    } else if (position == 6) {
                        notes.setText("");
                        notes.setVisibility(View.VISIBLE);
                    } else {
                        notes.setVisibility(View.GONE);
                        notes.setText(reasons[position]);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectPos == 0) {
                        Toast.makeText(mContext, "Please select rejection reason", Toast.LENGTH_SHORT).show();
                    } else {
                        acceptRejectOrder("rejected", orderDetail, orderDetail.getDelivery_date_time(), notes.getText().toString(), position);
                        mDialog.dismiss();

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
    }

    @Override
    public void onSpinnerStatus(String statusName, OrdersListResponse.Orders orderDetail, int position) {
        orderStatus(statusName.trim(), orderDetail);

    }

    public void acceptRejectOrder(final String acceptReject, OrdersListResponse.Orders orderDetail, String deliveryTime, String notes, final int position) {
        itemPosition = position;
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            minCHange = 0;
            AcceptRejectOrderRequest request = new AcceptRejectOrderRequest();
            request.setCustomer_id(orderDetail.getCustomer_id());
            request.setOrder_number(orderDetail.getOrder_num());
            request.setOrder_response(acceptReject);
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setDelivey_date_time(deliveryTime);
            request.setNotes(notes);

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.acceptRejectOrders(PrefManager.getInstance(getActivity()).getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                try {
                                    ApplicationContext.getInstance().stopNotificationSound();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (acceptReject.equals("accepted")) {
                                    Toast.makeText(mContext, "Order Accepted", Toast.LENGTH_SHORT).show();
                                    mAdapter.deleteItem(position);
                                    if (Integer.parseInt(tabNewCount.getText().toString()) > 0) {
                                        tabNewCount.setText((Integer.parseInt(tabNewCount.getText().toString()) - 1) + "");
                                    }
                                    if (!TextUtils.isEmpty(tabAcceptedCount.getText().toString())) {
                                        tabAcceptedCount.setText((Integer.parseInt(tabAcceptedCount.getText().toString()) + 1) + "");
                                    }


                                } else {
                                    Constants.alertDialogReject("You have rejected this order. \n" +
                                            " Customer has been \n" +
                                            "notified", getActivity(), OrderListFragment.this);

                                    if (!TextUtils.isEmpty(tabRejectedCount.getText().toString())) {
                                        tabRejectedCount.setText((Integer.parseInt(tabRejectedCount.getText().toString()) + 1) + "");
                                    }
                                    if (Integer.parseInt(tabNewCount.getText().toString()) > 0) {
                                        tabNewCount.setText((Integer.parseInt(tabNewCount.getText().toString()) - 1) + "");
                                    }


                                }


                            } else {
                                alertDialog(data.getMessage());
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


    public void orderStatus(String status, OrdersListResponse.Orders orderDetail) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            AcceptRejectOrderRequest request = new AcceptRejectOrderRequest();
            request.setOrderStatus(status);
            request.setOrder_number(orderDetail.getOrder_num());

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.orderStatus(PrefManager.getInstance(getActivity()).getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                getAllOrders("accepted", whichTabActive);
                                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_LONG).show();

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

    private void deliveryTimeSlotinitView() {

        if (toDayList == null) {
            toDayList = new String[1];
            toDayList[0] = "Select Delivery Time";
        }

        ArrayAdapter<String> adapterToday = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, toDayList);
        adapterToday.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        todaySpinner.setAdapter(adapterToday);


        todaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toDay = (String) parent.getItemAtPosition(position);

                if (position > 0) {
                    String time;
                    String date = toDayDataList.get(position - 1).substring(0, 10);
                    time = toDayDataList.get(position - 1).substring(11, toDayDataList.get(position - 1).length());
                    deliveryDateTime = date + " " + time.substring(6) + ":00";
                    Log.e("item", toDay + ", " + time + "," + toDayDataList.get(position) + ", " + deliveryDateTime);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void getRestaurantClosingTimeByDate(final ProgressBar progressBar, String restaurantId, final String delivery_date_time) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            TimeSlotRequest request = new TimeSlotRequest(restaurantId, delivery_date_time);
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getRestaurantClosingTimeByDate(PrefManager.getInstance(getActivity()).getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<RestaurantClosingTimeByDataModel>() {
                        @Override
                        public void onSuccess(RestaurantClosingTimeByDataModel data) {
                            progressBar.setVisibility(View.GONE);
//                            2019-06-06 09:41:00
                            if (data.getSuccess()) {
                                if (data != null && data.getData().getOpeningEndTime() != null && !data.getData().getOpeningEndTime().equalsIgnoreCase("")) {
                                    try {
                                        restaurantCloseMin = ApplicationContext.getInstance().getDifferenceMin(Constants.getDateFromString(delivery_date_time, "yyyy-MM-dd HH:mm:ss"), Constants.getDateFromString(data.getData().getOpeningEndTime(), "yyyy-MM-dd HH:mm:ss"));
                                        btnRemove.setEnabled(true);
                                        btnAdd.setEnabled(true);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    public void alertDialog(String msg) {
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


    @Override
    public void onDialogClicked() {
        mAdapter.deleteItem(itemPosition);
        if (Integer.parseInt(tabNewCount.getText().toString()) != 0) {
            tabNewCount.setText(String.valueOf(Integer.parseInt(tabNewCount.getText().toString()) - 1));
        }
    }

    @Override
    public void onDialogRejectClicked() {
        mAdapter.deleteItem(itemPosition);
        if (Integer.parseInt(tabNewCount.getText().toString()) != 0) {
            tabNewCount.setText(String.valueOf(Integer.parseInt(tabNewCount.getText().toString()) - 1));
        }
    }


    public void updateDialog() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_update_dialog, null);
        //  final LayoutReportDialogBinding dialogBinding = DataBindingUtil.bind(dialogView);
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);
        TextView tvOk = (TextView) dialogView.findViewById(R.id.tv_ok);


        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.seme_transparent)));
    }


}