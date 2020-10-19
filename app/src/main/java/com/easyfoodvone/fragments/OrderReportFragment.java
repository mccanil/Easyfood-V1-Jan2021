package com.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterReportOrderList;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.helper.PrintEsayFood;
import com.easyfoodvone.models.OrderReportRequest;
import com.easyfoodvone.models.OrderReportResponse;
import com.easyfoodvone.orders.view.impl.OrdersActivity;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;
import com.easyfoodvone.utility.UserPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;

public class OrderReportFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.btn_today)
    Button btnToday;
    @BindView(R.id.btn_yesterday)
    Button btnYesterday;
    @BindView(R.id.btn_start_date)
    LinearLayout btnStartDate;
    @BindView(R.id.btn_end_date)
    LinearLayout btnEndDate;

    RecyclerView totalOrdersList;
    @BindView(R.id.btn_bootom_ok)
    TextView btnBootomOk;
    @BindView(R.id.btn_bootom_print_report)
    TextView btnBootomPrintReport;
    @BindView(R.id.btn_bootom_email_report)
    TextView btnBootomEmailReport;
    Unbinder unbinder;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    TextView total_orders, total_revenue, total_items, total_discount, wallet_balance, credit_amt, credit_count, accepted_per, accepted_count, accepted_amt, declined_amt,
            declined_count, declined_per, cash_per, cash_amt, cash_count, endDate, startDate;
    Button findReportBetweenDates;
    private Context mContext;
    private Activity mActivity;
    List<OrderReportResponse.OrdersList> print;
    private View view;
    String fromDate = "";
    String toDate = "";
    String date = "";
    private boolean isToday = true;

    private AdapterReportOrderList mAdapter;
    private boolean allRevenue = false;
    private boolean byDate = false;
    private boolean betweenDate = false;
    PrefManager prefManager;

    public OrderReportFragment() {
        // Required empty public constructor
    }

    OrderReportResponse reportData;

    @SuppressLint("ValidFragment")
    public OrderReportFragment(Context mContext, Activity mActivity) {
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


        view = inflater.inflate(R.layout.fragment_order_report, container, false);
        prefManager=PrefManager.getInstance(getActivity());
        total_orders = view.findViewById(R.id.total_orders);
        total_revenue = view.findViewById(R.id.total_revenue);
        total_items = view.findViewById(R.id.total_items);
        total_discount = view.findViewById(R.id.total_discount);
        wallet_balance = view.findViewById(R.id.wallet_balance);
        credit_amt = view.findViewById(R.id.credit_amt);
        credit_count = view.findViewById(R.id.credit_count);
        accepted_per = view.findViewById(R.id.accepted_per);
        accepted_count = view.findViewById(R.id.accepted_count);
        accepted_amt = view.findViewById(R.id.accepted_amt);
        declined_amt = view.findViewById(R.id.declined_amt);
        declined_count = view.findViewById(R.id.declined_count);
        declined_per = view.findViewById(R.id.declined_per);
        cash_per = view.findViewById(R.id.cash_per);
        cash_amt = view.findViewById(R.id.cash_amt);
        cash_count = view.findViewById(R.id.cash_count);
        endDate = view.findViewById(R.id.endDate);
        startDate = view.findViewById(R.id.startDate);
        findReportBetweenDates = view.findViewById(R.id.findReportBetweenDates);
        totalOrdersList = view.findViewById(R.id.total_orders_list);


        unbinder = ButterKnife.bind(this, view);

        alertDialogMPIN();
        clickListeners();
        view.findViewById(R.id.btn_printReport).setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void clickListeners() {


        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.dateSelectorWithFormattedDateForReport(startDate, getActivity());
            }
        });
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.dateSelectorWithFormattedDateForReport(endDate, getActivity());
            }
        });


        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderReport(false, true, false, false);
            }
        });
        findReportBetweenDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(startDate.getText().toString()) || startDate.getText().toString().equalsIgnoreCase("Start Date")) {
                    Toast.makeText(mContext, "Enter correct start date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(endDate.getText().toString()) || endDate.getText().toString().equalsIgnoreCase("End Date")) {
                    Toast.makeText(mContext, "Enter correct end date", Toast.LENGTH_SHORT).show();
                    return;
                } else
                    getOrderReport(false, false, false, true);
            }
        });

        btnYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderReport(false, false, true, false);
            }
        });

        btnBootomEmailReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentReport();
            }
        });

    }

    private void sentReport() {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "");
        dialog.setCancelable(false);
        dialog.show();
        try {
            Single<OrderReportResponse> apiRequest = null;

            OrderReportRequest request = new OrderReportRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(getActivity()).getUser_id());
            request.setEnd_date(toDate);
            request.setFrom_date(fromDate);
            request.setDate(date);
            request.setEmail("1");
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);

            if (allRevenue) {
                apiRequest = apiService.getOrderReport(prefManager.getPreference(AUTH_TOKEN,""),request);
            } else if (betweenDate) {
                apiRequest = apiService.getOrderReportBetweenDate(prefManager.getPreference(AUTH_TOKEN,""),request);
            } else {
                apiRequest = apiService.getOrderReportByDate(prefManager.getPreference(AUTH_TOKEN,""),request);
            }
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiRequest
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrderReportResponse>() {
                        @Override
                        public void onSuccess(OrderReportResponse data) {
                            dialog.dismiss();
                            Toast.makeText(mContext, "Report sent on " + Constants.getStoredData(getActivity()).getEmail(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Server connection failed", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (
                Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    public void alertDialogMPIN() {
        final char[] pin = Constants.getStoredData(getActivity()).getPincode().toCharArray();
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_enter_mpin, null);
        final AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
        mDialog.setCancelable(false);
        mDialog.setView(mDialogView);
        final EditText firstMpin = mDialogView.findViewById(R.id.mpin_first);
        final TextView mesage = mDialogView.findViewById(R.id.txt_message);

        mesage.setText("Please enter 4 digit pin code\nto view Order Report");

        final EditText secondMpin = mDialogView.findViewById(R.id.mpin_two);
        final EditText thirdMpin = mDialogView.findViewById(R.id.mpin_three);
        final EditText fourthMpin = mDialogView.findViewById(R.id.mpin_four);
        final TextView error = mDialogView.findViewById(R.id.txt_error_message);
        firstMpin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0)
                    secondMpin.requestFocus();


            }
        });
        secondMpin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0)
                    thirdMpin.requestFocus();
                else
                    firstMpin.requestFocus();

            }
        });
        thirdMpin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0)
                    fourthMpin.requestFocus();
                else
                    thirdMpin.requestFocus();

            }
        });

        fourthMpin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {

                } else
                    thirdMpin.requestFocus();

            }
        });


        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                if (firstMpin.getText().toString().trim().equalsIgnoreCase(String.valueOf(pin[0]))
                        && secondMpin.getText().toString().trim().equalsIgnoreCase(String.valueOf(pin[1]))
                        && thirdMpin.getText().toString().trim().equalsIgnoreCase(String.valueOf(pin[2]))
                        && fourthMpin.getText().toString().trim().equalsIgnoreCase(String.valueOf(pin[3]))) {
                    mDialog.dismiss();
                    error.setVisibility(View.GONE);


                    getOrderReport(true, false, false, false);


                } else {
                    error.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }

            }
        });
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mActivity.finish();
                startActivity(new Intent(mContext, OrdersActivity.class));
            }
        });

        mDialog.show();
    }

    public void getOrderReport(boolean getAllRevenue, boolean todayRevenue, boolean yesterdayRevenue, boolean betweenRevenue) {


        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading orders report...");
        dialog.setCancelable(false);
        dialog.show();
        try {

            Single<OrderReportResponse> apiRequest = null;

            OrderReportRequest request = new OrderReportRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(getActivity()).getUser_id());
            request.setEnd_date(endDate.getText().toString());
            request.setFrom_date(startDate.getText().toString());
            if (yesterdayRevenue) {
                date = Constants.getYesterdayDateString1();
                request.setDate(Constants.getYesterdayDateString1());
            } else if (todayRevenue) {
                date = Constants.getCurrentDateString1();
                request.setDate(Constants.getCurrentDateString1());
            }

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);

            if (getAllRevenue) {
                allRevenue = true;
                betweenDate = false;
                byDate = false;
                apiRequest = apiService.getOrderReport(prefManager.getPreference(AUTH_TOKEN,""),request);
            } else if (betweenRevenue) {
                betweenDate = true;
                byDate = false;
                allRevenue = false;
                apiRequest = apiService.getOrderReportBetweenDate(prefManager.getPreference(AUTH_TOKEN,""),request);
            } else {
                apiRequest = apiService.getOrderReportByDate(prefManager.getPreference(AUTH_TOKEN,""),request);
                byDate = true;
                allRevenue = false;
                betweenDate = false;
            }

            fromDate = startDate.getText().toString();
            toDate = endDate.getText().toString();

            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiRequest
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrderReportResponse>() {
                        @Override
                        public void onSuccess(OrderReportResponse data) {
                            scrollView.setVisibility(View.VISIBLE);
                            clearTextWhenRefreshed();
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                reportData = data;
                                total_orders.setText(data.getData().getTotal_orders() == null || data.getData().getTotal_orders().equalsIgnoreCase("") ? "0" : data.getData().getTotal_orders());
                                total_revenue.setText(data.getData().getTotal_revenue() == null || data.getData().getTotal_revenue().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getTotal_revenue());
                                total_items.setText(data.getData().getTotal_items() == null || data.getData().getTotal_items().equalsIgnoreCase("") ? "0" : data.getData().getTotal_items());
                                total_discount.setText(data.getData().getTotal_discount() == null || data.getData().getTotal_discount().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getTotal_discount());
                                wallet_balance.setText(data.getData().getWallet_balance() == null || data.getData().getWallet_balance().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getWallet_balance());
                                credit_count.setText(data.getData().getTotal_orders_by_credit_card() == null || data.getData().getTotal_orders_by_credit_card().equalsIgnoreCase("") ? "Credit(0)" : "Credit(" + data.getData().getTotal_orders_by_credit_card() + ")");
                                credit_amt.setText(data.getData().getTotal_orders_by_credit_card_amount() == null || data.getData().getTotal_orders_by_credit_card_amount().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getTotal_orders_by_credit_card_amount());
                                accepted_per.setText(data.getData().getTotal_orders_accepted_per() == null || data.getData().getTotal_orders_accepted_per().equalsIgnoreCase("") ? Constants.POUND + "0%" : data.getData().getTotal_orders_accepted_per() + "%");
                                accepted_amt.setText(data.getData().getTotal_orders_accepted_amount() == null || data.getData().getTotal_orders_accepted_amount().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getTotal_orders_accepted_amount());
                                accepted_count.setText(data.getData().getTotal_orders_accepted() == null || data.getData().getTotal_orders_accepted().equalsIgnoreCase("") ? "Accepted(0)" : "Accepted(" + data.getData().getTotal_orders_accepted() + ")");
                                declined_amt.setText(data.getData().getTotal_orders_declined_amount() == null || data.getData().getTotal_orders_declined_amount().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getTotal_orders_declined_amount());
                                declined_count.setText(data.getData().getTotal_orders_declined() == null || data.getData().getTotal_orders_declined().equalsIgnoreCase("") ? "Declined(0)" : "Declined(" + data.getData().getTotal_orders_declined() + ")");
                                declined_per.setText(data.getData().getTotal_orders_declined_per() == null || data.getData().getTotal_orders_declined_per().equalsIgnoreCase("") ? "0%" : data.getData().getTotal_orders_declined_per() + "%");
                                cash_per.setText(data.getData().getTotal_orders_by_cash_per() == null || data.getData().getTotal_orders_by_cash_per().equalsIgnoreCase("") ? "0%" : data.getData().getTotal_orders_by_cash_per() + "%");
                                cash_amt.setText(data.getData().getTotal_orders_by_cash_amount() == null || data.getData().getTotal_orders_by_cash_amount().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getData().getTotal_orders_by_cash_amount());
                                cash_count.setText(data.getData().getTotal_orders_by_cash() == null || data.getData().getTotal_orders_by_cash().equalsIgnoreCase("") ? "Cash(0)" : "Cash(" + data.getData().getTotal_orders_by_cash() + ")");


                                print = data.getData().getOrder_list();
                                totalOrdersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                                mAdapter = new AdapterReportOrderList(mContext, mActivity, data.getData().getOrder_list());
                                totalOrdersList.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            clearTextWhenRefreshed();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            clearTextWhenRefreshed();
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void clearTextWhenRefreshed() {
        total_orders.setText("");
        total_revenue.setText("");
        total_items.setText("");
        total_discount.setText("");
        wallet_balance.setText("");
        credit_amt.setText("");
        credit_count.setText("");
        accepted_per.setText("");
        accepted_count.setText("");
        accepted_amt.setText("");
        declined_amt.setText("");
        declined_count.setText("");
        declined_per.setText("");
        cash_per.setText("");
        cash_amt.setText("");
        cash_count.setText("");
        endDate.setText("");
        startDate.setText("");

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_printReport:
                printReport();
                break;
        }
    }

    private void printReport() {
        if (reportData == null) {
            return;
        }
        byte[] logoByte = UserPreferences.getUserPreferences().getByteArray(mContext, Constants.RESTAURANT_LOGO);
        Bitmap logo = null;
        if (logoByte != null) {
            logo = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
//                    logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), 80, false);
        }


        PrintEsayFood.printReport(mContext, logo, Constants.getStoredData(mContext), reportData.getData());

    }
}
