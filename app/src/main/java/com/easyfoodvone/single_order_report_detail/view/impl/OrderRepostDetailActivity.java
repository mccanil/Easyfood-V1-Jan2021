package com.easyfoodvone.single_order_report_detail.view.impl;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterProductList;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.helper.PrintEsayFood;
import com.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.new_order.models.OrderDetailsRequest;
import com.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class OrderRepostDetailActivity extends AppCompatActivity {
    EditText input;
    @BindView(R.id.txt_order_id)
    TextView txtOrderId;
    @BindView(R.id.customer_name)
    TextView customerName;
    @BindView(R.id.contact_no)
    TextView contactNo;
    @BindView(R.id.email_id)
    TextView emailId;
    @BindView(R.id.product_list)
    RecyclerView productList;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    @BindView(R.id.total_discount)
    TextView totalDiscount;
    @BindView(R.id.net_amount)
    TextView netAmount;
    @BindView(R.id.bottom_ok)
    TextView bottomOk;
    @BindView(R.id.bottom_print_report)
    TextView bottomPrintReport;
    @BindView(R.id.bottom_email_report)
    TextView bottomEmailReport;
    String order_num = "";

    private AdapterProductList mAdapter;
    private boolean isOpen = true;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    OrderDetailsResponse.OrderDetails printdata;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_repost_detail);
        prefManager = PrefManager.getInstance(OrderRepostDetailActivity.this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("key")) {
            order_num = getIntent().getStringExtra("key");
            if (order_num.equalsIgnoreCase("")) {
                Toast.makeText(this, "Order detail not found", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                getOrderDetail(order_num);
            }
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                    if (intent.hasExtra("message")) {
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(OrderRepostDetailActivity.this, message);

                    }

                }
            }
        };


        bottomPrintReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (printdata != null)
                    PrintEsayFood.printOrderr(printdata);
                else {
                    Toast.makeText(OrderRepostDetailActivity.this, "Print report data not available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bottomEmailReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(printdata);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_TYPE_ACCEPTED));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
        }
        return true;
    }

    void alertDialog(String msg) {

        LayoutInflater factory = LayoutInflater.from(OrderRepostDetailActivity.this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(OrderRepostDetailActivity.this).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void getOrderDetail(String ordersNumber) {
        final LoadingDialog dialog = new LoadingDialog(OrderRepostDetailActivity.this, "Loading details...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(OrderRepostDetailActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrderDetails(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrderDetailsResponse>() {
                        @Override
                        public void onSuccess(OrderDetailsResponse data) {

                            dialog.dismiss();

                            customerName.setText(data.getOrders_details().getDelivery_address().getCustomer_name());
                            contactNo.setText(data.getOrders_details().getDelivery_address().getPhone_number());
                            emailId.setText(data.getOrders_details().getDelivery_address().getEmail());

                            Log.e("Order details ", data.toString());
                            mAdapter = new AdapterProductList(OrderRepostDetailActivity.this, data.getOrders_details());
                            productList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                            productList.setAdapter(mAdapter);
                            txtOrderId.setText("Order id - " + data.getOrders_details().getOrder_num());
                            totalAmount.setText(data.getOrders_details().getOrder_total());
                            totalDiscount.setText(data.getOrders_details().getDiscount_amount());
                            netAmount.setText(data.getOrders_details().getSub_total());

                            printdata = data.getOrders_details();

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(OrderRepostDetailActivity.this, "Loading failed Swipe down to try again!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(OrderRepostDetailActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void sendEmail(final OrderDetailsResponse.OrderDetails printdata) {
        final LoadingDialog dialog = new LoadingDialog(OrderRepostDetailActivity.this, "Sending email...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            AcceptRejectOrderRequest request = new AcceptRejectOrderRequest();
            request.setCustomer_id(printdata.getCustomer_id());
            request.setOrder_number(printdata.getOrder_num());
            request.setRestaurant_id(Constants.getStoredData(OrderRepostDetailActivity.this).getRestaurant_id());

            ApiInterface apiService = ApiClient.getClient(OrderRepostDetailActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.sendEmail(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                alertDialog("Email sent to " + printdata.getCustomer_name() + "'s  email");
                            } else {
                                alertDialog("Email Sending Failed!");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(OrderRepostDetailActivity.this, "Email Sending Failed! Try again.", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(OrderRepostDetailActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


}
