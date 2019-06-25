package com.lexxdigitals.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.models.RevenueReportRequest;
import com.lexxdigitals.easyfoodvone.models.RevenueReportResponse;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RevenueReportFragment extends Fragment {

    @BindView(R.id.btn_today)
    Button btnToday;
    @BindView(R.id.btn_yesterday)
    Button btnYesterday;

    @BindView(R.id.btn_start_date)
    LinearLayout btnStartDate;
    @BindView(R.id.btn_end_date)
    LinearLayout btnEndDate;
    @BindView(R.id.total_orders)
    LinearLayout totalOrders;
    @BindView(R.id.product_sold)
    LinearLayout productSold;
    @BindView(R.id.taxes_applied)
    LinearLayout taxesApplied;
    @BindView(R.id.discount_applied)
    LinearLayout discountApplied;
    @BindView(R.id.revenue_collected)
    LinearLayout revenueCollected;
    @BindView(R.id.gross_profit)
    LinearLayout grossProfit;

    @BindView(R.id.btn_bootom_ok)
    TextView btnBootomOk;
    @BindView(R.id.btn_bootom_print_report)
    TextView btnBootomPrintReport;
    @BindView(R.id.btn_bootom_email_report)
    TextView btnBootomEmailReport;
    Unbinder unbinder;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    private Context mContext;
    private Activity mActivity;
    private View view;
    TextView startDate, endDate, text_total_orders, total_product_sold, total_tax_applied, tot_discount_applied, total_revenue_collected, text_grossProfit;
    Button findReportBetweenDates;


    public RevenueReportFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public RevenueReportFragment(Context mContext, Activity mActivity) {
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

        view = inflater.inflate(R.layout.fragment_revenue_report, container, false);


        total_product_sold = view.findViewById(R.id.total_product_sold);
        text_total_orders = view.findViewById(R.id.text_total_orders);
        endDate = view.findViewById(R.id.endDate);
        startDate = view.findViewById(R.id.startDate);

        total_tax_applied = view.findViewById(R.id.total_tax_applied);
        tot_discount_applied = view.findViewById(R.id.tot_discount_applied);
        total_revenue_collected = view.findViewById(R.id.total_revenue_collected);
        text_grossProfit = view.findViewById(R.id.text_grossProfit);
        findReportBetweenDates = view.findViewById(R.id.findReportBetweenDates);


        unbinder = ButterKnife.bind(this, view);
        alertDialogMPIN();
        clickListeners();


        return view;


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
                Constants.dateSelectorWithFormattedDate(endDate, getActivity());
            }
        });


        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRevenueReport(false, true, false, false);
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
                    getRevenueReport(false, false, false, true);
            }
        });

        btnYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRevenueReport(false, false, true, false);
            }
        });


    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void alertDialogMPIN() {
        final char[] pin = Constants.getStoredData(getActivity()).getPincode().toCharArray();
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_enter_mpin, null);
        final AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
        mDialog.setCancelable(false);
        mDialog.setView(mDialogView);
        final EditText firstMpin = mDialogView.findViewById(R.id.mpin_first);
        final EditText secondMpin = mDialogView.findViewById(R.id.mpin_two);
        final EditText thirdMpin = mDialogView.findViewById(R.id.mpin_three);
        final EditText fourthMpin = mDialogView.findViewById(R.id.mpin_four);
        final TextView error = mDialogView.findViewById(R.id.txt_error_message);
        final TextView msg = mDialogView.findViewById(R.id.txt_message);
        msg.setText("Please enter 4 digit pin code \n" +
                "to view revenue Report");
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
                    secondMpin.requestFocus();

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
                    getRevenueReport(true, false, false, false);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void getRevenueReport(boolean getAllRevenue, boolean todayRevenue, boolean yesterdayRevenue, boolean betweenRevenue) {


        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading revenue report...");
        dialog.setCancelable(false);
        dialog.show();
        try {

            Single<RevenueReportResponse> apiRequest = null;

            RevenueReportRequest request = new RevenueReportRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(getActivity()).getUser_id());
            request.setEnd_date(endDate.getText().toString());
            request.setFrom_date(startDate.getText().toString());
            if (yesterdayRevenue)
                request.setDate(Constants.getYesterdayDateString());
            else if (todayRevenue)
                request.setDate(Constants.getCurrentDateString());

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);

            if (getAllRevenue) {
                apiRequest = apiService.getRevenueReport(request);
            } else if (betweenRevenue) {
                apiRequest = apiService.getRevenueReportBetweenDate(request);
            } else
                apiRequest = apiService.getRevenueReportByDate(request);

            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiRequest
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<RevenueReportResponse>() {
                        @Override
                        public void onSuccess(RevenueReportResponse data) {
                            scrollView.setVisibility(View.VISIBLE);
                            clearTextWhenRefreshed();
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                text_total_orders.setText(data.getTotal_orders().getTotal_orders() == null || data.getTotal_orders().getTotal_orders().equalsIgnoreCase("") ? 0 + "" : data.getTotal_orders().getTotal_orders());
                                total_product_sold.setText(data.getTotal_orders().getTotal_product_sold() == null || data.getTotal_orders().getTotal_product_sold().equalsIgnoreCase("") ? "0" : data.getTotal_orders().getTotal_product_sold());
                                total_tax_applied.setText(data.getTotal_orders().getTotal_taxes_applied() == null || data.getTotal_orders().getTotal_taxes_applied().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getTotal_orders().getTotal_taxes_applied());
                                tot_discount_applied.setText(data.getTotal_orders().getTotal_discount_applied() == null || data.getTotal_orders().getTotal_discount_applied().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getTotal_orders().getTotal_discount_applied());
                                total_revenue_collected.setText(data.getTotal_orders().getTotal_revenue_collected() == null || data.getTotal_orders().getTotal_revenue_collected().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getTotal_orders().getTotal_revenue_collected());
                                text_grossProfit.setText(data.getTotal_orders().getGross_profit() == null || data.getTotal_orders().getGross_profit().equalsIgnoreCase("") ? Constants.POUND + "0" : Constants.POUND + data.getTotal_orders().getGross_profit());

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


    void clearTextWhenRefreshed() {
        text_total_orders.setText("");
        total_product_sold.setText("");
        total_tax_applied.setText("");
        tot_discount_applied.setText("");
        total_revenue_collected.setText("");
        text_grossProfit.setText("");
    }


}
