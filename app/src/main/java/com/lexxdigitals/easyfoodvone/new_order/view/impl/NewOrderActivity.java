package com.lexxdigitals.easyfoodvone.new_order.view.impl;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.adapters.menu_product.MenuItemListAdapter;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.helper.PrintEsayFood;
import com.lexxdigitals.easyfoodvone.helper.RecyclerLayoutManager;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.models.menu_response.AllTypeMenuItemModel;
import com.lexxdigitals.easyfoodvone.models.menu_response.OrdersDetailsResponseNew;
import com.lexxdigitals.easyfoodvone.models.menu_response.OrdersDetailsTop;
import com.lexxdigitals.easyfoodvone.models.menu_response.SpecialOffer;
import com.lexxdigitals.easyfoodvone.models.menu_response.UpSells;
import com.lexxdigitals.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.lexxdigitals.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsRequest;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigitals.easyfoodvone.orders.models.OrdersListResponse;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class NewOrderActivity extends AppCompatActivity implements Constants.DialogClickedListener {
    @BindView(R.id.btn_reject)
    LinearLayout btnReject;
    @BindView(R.id.btn_accept)
    LinearLayout btnAccept;
    @BindView(R.id.txt_delivery_option)
    TextView txtDeliveryOption;
    @BindView(R.id.txt_delevery_date)
    TextView txtDeleveryDate;
    @BindView(R.id.txt_total_amount_paid)
    TextView txtTotalAmountPaid;
    @BindView(R.id.txt_payment_method)
    TextView txtPaymentMethod;
    @BindView(R.id.txt_order_id)
    TextView txtOrderId;
    @BindView(R.id.txt_order_date)
    TextView txtOrderDate;
    @BindView(R.id.list_order_details)
    RecyclerView listOrderDetails;
    @BindView(R.id.btn_printer)
    ImageButton btnPrinter;
    @BindView(R.id.txt_delevery_charges)
    TextView txtDeleveryCharges;
    @BindView(R.id.txt_sub_total)
    TextView txtSubTotal;
    @BindView(R.id.txt_discount)
    TextView txtDiscount;
    @BindView(R.id.txt_total)
    TextView txtTotal;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_contact)
    TextView txtContact;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.image_barcode)
    ImageView imageBarcode;
    TextView restaurant_name, reject, accept, txt_notes;
    //        private CartAdapter mAdapter;
    private MenuItemListAdapter mAdapter;
    LoginResponse.Data baseDetails;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout mainLayout;
    CircleImageView resturant_image;
    TextView contact, address;
    LinearLayout actionLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    OrdersListResponse.Orders orderDetail;
    //    OrderDetailsResponse.OrderDetails orderDetailsResponse;
    OrdersDetailsTop orderDetailsResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionLayout = findViewById(R.id.actionLayout);

        restaurant_name = findViewById(R.id.restaurant_name);
        contact = findViewById(R.id.contact);
        resturant_image = findViewById(R.id.resturant_image);
        address = findViewById(R.id.address);

        mainLayout = findViewById(R.id.mainLayout);
        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);
        txt_notes = findViewById(R.id.txt_notes);

        mAdapter = new MenuItemListAdapter(NewOrderActivity.this);
        RecyclerLayoutManager layoutManager = new RecyclerLayoutManager(1, RecyclerLayoutManager.VERTICAL);
        layoutManager.setScrollEnabled(false);
//        listOrderDetails.setLayoutManager(new LinearLayoutManager(NewOrderActivity.this, LinearLayoutManager.VERTICAL, false));
        listOrderDetails.setLayoutManager(layoutManager);
        listOrderDetails.setAdapter(mAdapter);
        listOrderDetails.setNestedScrollingEnabled(false);

        if (getIntent().hasExtra(Constants.ORDER_DETAIL)) {
            orderDetail = (OrdersListResponse.Orders) getIntent().getExtras().get(Constants.ORDER_DETAIL);
            if (orderDetail == null) {
                if (getIntent().hasExtra("order_number")) {
                    orderDetailsNew(getIntent().getExtras().getString("order_number"));
                } else {
                    Toast.makeText(this, "Order Not Found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                orderDetailsNew(orderDetail.getOrder_num());

            }
        } else {

            if (getIntent().hasExtra("order_number")) {
                orderDetailsNew(getIntent().getExtras().getString("order_number"));
            } else {
                Toast.makeText(this, "Order Not Found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderDetailsNew(orderDetail.getOrder_num());
            }
        });


        baseDetails = (LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(this, Constants.LOGIN_RESPONSE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRestaurantDetails();
            }
        });

        Log.e("Base Detail", baseDetails.toString());

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                    if (intent.hasExtra("message")) {
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(NewOrderActivity.this, message);

                    }

                }
            }
        };
        onClickEvents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_TYPE_ACCEPTED));
    }

    private void setRestaurantDetails() {
        restaurant_name.setText(Constants.getStoredData(NewOrderActivity.this).getRestaurant_name());
        contact.setText(Constants.getStoredData(NewOrderActivity.this).getLandline_number());
        address.setText(Constants.getStoredData(NewOrderActivity.this).getAddress());
        Picasso.get().load(Constants.getStoredData(NewOrderActivity.this).getLogo())
                .placeholder(R.drawable.restaurant_icon)
                .error(R.drawable.restaurant_icon).into(resturant_image);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewOrderActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void setData(OrdersDetailsTop orderDetail) {

        txtOrderId.setText(orderDetail.getOrder_num());
        txtAddress.setText(orderDetail.getDeliveryDetails().getCustomer_location());
        txtTotalAmountPaid.setText(Constants.POUND + orderDetail.getOrder_total());
        txtContact.setText(orderDetail.getDeliveryDetails().getPhone_number());
        txtName.setText(orderDetail.getDeliveryDetails().getCustomer_name());
        txtOrderDate.setText(orderDetail.getOrder_date_time());
        txtDeliveryOption.setText(orderDetail.getDelivery_option());
        txtDeleveryDate.setText(orderDetail.getDelivery_date_time());
        txtTotalAmountPaid.setText(Constants.POUND + orderDetail.getOrder_total());
        txtPaymentMethod.setText(orderDetail.getPayment_mode());
        txtOrderId.setText(orderDetail.getOrder_num());
        txtOrderDate.setText(orderDetail.getOrder_date_time());
        txtDeleveryCharges.setText(Constants.POUND + orderDetail.getDelivery_charge());
        txtSubTotal.setText(Constants.POUND + orderDetail.getSub_total());
        txtDiscount.setText(Constants.POUND + orderDetail.getDiscount_amount());
        txtTotal.setText(Constants.POUND + orderDetail.getOrder_total());
        txt_notes.setText(orderDetail.getOrder_notes());
        try {
            imageBarcode.setImageBitmap(Constants.encodeAsBitmap(orderDetail.getOrder_num(), BarcodeFormat.CODABAR, 100, 60));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (getIntent().hasExtra(Constants.ORDER_DETAIL)) {
            handleActions(this.orderDetail.getOrder_status());
        }

        //TODO: Open dialer on click the phone icon...
    }

    private void handleActions(String order_status) {
        switch (order_status) {
            case "new":
                actionLayout.setVisibility(View.VISIBLE);
                break;
            case "accepted":
                // actionLayout.setVisibility(View.GONE);
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
            case "rejected":
                actionLayout.setVisibility(View.GONE);
                // btnChangeDeleveryTime.setVisibility(View.GONE);
                break;
        }
    }

    int selectPos = 0;

    public void onClickEvents() {


        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] reasons = new String[]{"Select reason", "Too busy to fulfill order", "Items not in stock", "Delivery driver unavailable ", "Closing soon", "collection only", "Other"};
                LayoutInflater factory = LayoutInflater.from(NewOrderActivity.this);
                final View mDialogView = factory.inflate(R.layout.reject_confirmation_dialog, null);


                final AlertDialog mDialog = new AlertDialog.Builder(NewOrderActivity.this).create();
                mDialog.setView(mDialogView);
                final EditText notes = (EditText) mDialogView.findViewById(R.id.notes);
                final Spinner spinnerMainItem = mDialogView.findViewById(R.id.spinner_main_item);


                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(NewOrderActivity.this, android.R.layout.simple_spinner_item, reasons);
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
                            Toast.makeText(NewOrderActivity.this, "Please select rejection reason", Toast.LENGTH_SHORT).show();
                        } else {
                            acceptRejectOrder("rejected", orderDetail, "", notes.getText().toString());
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
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater factory = LayoutInflater.from(NewOrderActivity.this);
                final View mDialogView = factory.inflate(R.layout.change_delevery_time_dialog, null);
                final AlertDialog mDialog = new AlertDialog.Builder(NewOrderActivity.this).create();
                mDialog.setView(mDialogView);
                final LinearLayout layoutTime = (LinearLayout) mDialogView.findViewById(R.id.set_time);
                final LinearLayout layoutNotes = (LinearLayout) mDialogView.findViewById(R.id.notes);
                TextView btnRemove = mDialogView.findViewById(R.id.txt_remove);
                TextView btnAdd = mDialogView.findViewById(R.id.btn_add);
                final TextView txtResult = mDialogView.findViewById(R.id.txt_result);
                txtResult.setText(orderDetailsResponse.getAverage_delivery_time() + "");

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(txtResult.getText().toString()) > Integer.parseInt(orderDetailsResponse.getAverage_delivery_time()))
                            txtResult.setText(String.valueOf(Integer.parseInt(txtResult.getText().toString()) - 5));
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(txtResult.getText().toString()) < 90)
                            txtResult.setText(String.valueOf(Integer.parseInt(txtResult.getText().toString()) + 5));
                    }
                });

                mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        acceptRejectOrder("accepted", orderDetail, "", txtResult.getText().toString());

                    }
                });
                mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                    }
                });

                mDialog.show();






                /* alertDialog();*/
            }
        });


        btnPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] logoByte = UserPreferences.getUserPreferences().getByteArray(NewOrderActivity.this, Constants.RESTAURANT_LOGO);
                Bitmap logo = null;
                if (logoByte != null) {
                    logo = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
//                    logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), 80, false);
                }

                if (orderDetailsResponse != null) {
//                    Print ANAND
                    PrintEsayFood.printOrderNew(NewOrderActivity.this, logo, Constants.getStoredData(NewOrderActivity.this), orderDetailsResponse);
                }
            }
        });
    }


    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(NewOrderActivity.this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(NewOrderActivity.this).create();
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

    private void getOrderDetail(String ordersNumber) {
        final LoadingDialog dialog = new LoadingDialog(NewOrderActivity.this, "Loading details...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(NewOrderActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrderDetails(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrderDetailsResponse>() {
                        @Override
                        public void onSuccess(OrderDetailsResponse data) {

//                            orderDetailsResponse = data.getOrders_details();

                            mainLayout.setVisibility(View.VISIBLE);

                            btnAccept.setEnabled(true);
                            btnReject.setEnabled(true);
                            swipeRefresh.setRefreshing(false);
                            dialog.dismiss();
                            Log.e("Order details ", data.toString());
//                            setData(data.getOrders_details());
//                            mAdapter = new CartAdapter(NewOrderActivity.this, data.getOrders_details().getCart());
                            listOrderDetails.setLayoutManager(new LinearLayoutManager(NewOrderActivity.this, LinearLayoutManager.VERTICAL, false));
                            listOrderDetails.setAdapter(mAdapter);

                        }

                        @Override
                        public void onError(Throwable e) {
                            btnAccept.setEnabled(false);
                            btnReject.setEnabled(false);
                            mainLayout.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                            swipeRefresh.setRefreshing(false);

                            Toast.makeText(NewOrderActivity.this, "Loading failed Swipe down to try again!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            btnAccept.setEnabled(false);
            btnReject.setEnabled(false);
            mainLayout.setVisibility(View.INVISIBLE);
            swipeRefresh.setRefreshing(false);
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(NewOrderActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void orderDetailsNew(String ordersNumber) {
        final LoadingDialog dialog = new LoadingDialog(NewOrderActivity.this, "Loading details...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(NewOrderActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.orderDetailsNew(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrdersDetailsResponseNew>() {
                        @Override
                        public void onSuccess(OrdersDetailsResponseNew data) {
                            orderDetailsResponse = data.getOrdersDetails();
                            mainLayout.setVisibility(View.VISIBLE);

                            btnAccept.setEnabled(true);
                            btnReject.setEnabled(true);
                            swipeRefresh.setRefreshing(false);
                            dialog.dismiss();
                            Log.e("Order details ", data.toString());
                            setData(data.getOrdersDetails());

                            mAdapter.clearData();
                            List<AllTypeMenuItemModel> menuProducts = new ArrayList<>();
                            if (data.getOrdersDetails().getCart().getMenu().getMenuCategory() != null && data.getOrdersDetails().getCart().getMenu().getMenuCategory().size() > 0) {
                                for (int i = 0; i < data.getOrdersDetails().getCart().getMenu().getMenuCategory().size(); i++) {
                                    for (int j = 0; j < data.getOrdersDetails().getCart().getMenu().getMenuCategory().get(i).getMenuProducts().size(); j++) {
                                        AllTypeMenuItemModel allTypeMenuItemModel = new AllTypeMenuItemModel(data.getOrdersDetails().getCart().getMenu().getMenuCategory().get(i).getMenuProducts().get(j), null, null, MenuItemListAdapter.MENU_CATEGORY_ITEM_VIEW_TYPE);
                                        menuProducts.add(allTypeMenuItemModel);
                                    }
                                    for (int j = 0; j < data.getOrdersDetails().getCart().getMenu().getMenuCategory().get(i).getMenuSubCategory().size(); j++) {
                                        for (int k = 0; k < data.getOrdersDetails().getCart().getMenu().getMenuCategory().get(i).getMenuSubCategory().get(j).getMenuProducts().size(); k++) {
                                            AllTypeMenuItemModel allTypeMenuItemModel = new AllTypeMenuItemModel(data.getOrdersDetails().getCart().getMenu().getMenuCategory().get(i).getMenuSubCategory().get(j).getMenuProducts().get(k), null, null, MenuItemListAdapter.MENU_CATEGORY_ITEM_VIEW_TYPE);
                                            menuProducts.add(allTypeMenuItemModel);
                                        }
                                    }
                                }
                            }

                            if (data.getOrdersDetails().

                                    getCart().

                                    getMenu().

                                    getSpecialOffers() != null) {
                                for (SpecialOffer specialOffers : data.getOrdersDetails().getCart().getMenu().getSpecialOffers()) {
                                    AllTypeMenuItemModel allTypeMenuItemModel = new AllTypeMenuItemModel(null, specialOffers, null, MenuItemListAdapter.SPECIAL_OFFER_ITEM_VIEW_TYPE);
                                    menuProducts.add(allTypeMenuItemModel);
                                }
                            }

                            if (data.getOrdersDetails().

                                    getCart().

                                    getMenu().

                                    getUpSellProducts() != null) {
                                for (UpSells upSells : data.getOrdersDetails().getCart().getMenu().getUpSellProducts()) {
                                    AllTypeMenuItemModel allTypeMenuItemModel = new AllTypeMenuItemModel(null, null, upSells, MenuItemListAdapter.UP_SELLS_ITEM_VIEW_TYPE);
                                    menuProducts.add(allTypeMenuItemModel);
                                }
                            }
                            mAdapter.addItem(menuProducts);
                        }

                        @Override
                        public void onError(Throwable e) {
                            btnAccept.setEnabled(false);
                            btnReject.setEnabled(false);
                            mainLayout.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                            swipeRefresh.setRefreshing(false);

                            Toast.makeText(NewOrderActivity.this, "Loading failed Swipe down to try again!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (
                Exception e) {
            btnAccept.setEnabled(false);
            btnReject.setEnabled(false);
            mainLayout.setVisibility(View.INVISIBLE);
            swipeRefresh.setRefreshing(false);
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(NewOrderActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void acceptRejectOrder(final String acceptReject, OrdersListResponse.Orders orderDetail, String notes, String s) {
        final LoadingDialog dialog = new LoadingDialog(NewOrderActivity.this, "");
        dialog.setCancelable(false);
        dialog.show();
        try {
            AcceptRejectOrderRequest request = new AcceptRejectOrderRequest();
            request.setCustomer_id(orderDetail.getCustomer_id());
            request.setOrder_number(orderDetail.getOrder_num());
            request.setOrder_response(acceptReject);
            request.setRestaurant_id(Constants.getStoredData(NewOrderActivity.this).getRestaurant_id());
            request.setDelivey_time(s);
            request.setNotes(notes);

            ApiInterface apiService = ApiClient.getClient(NewOrderActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.acceptRejectOrders(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {

                                if (acceptReject.equals("accepted")) {
                                    Constants.alertDialog("You have accepted this order \n" +
                                            "and customer has been \n" +
                                            "notified", NewOrderActivity.this, NewOrderActivity.this);

                                    byte[] logoByte = UserPreferences.getUserPreferences().getByteArray(NewOrderActivity.this, Constants.RESTAURANT_LOGO);
                                    Bitmap logo = null;
                                    if (logoByte != null) {
                                        logo = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
                                        //logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), 80, false);
                                    }
//                                    Print ANAND
                                    PrintEsayFood.printOrderNew(NewOrderActivity.this, logo, Constants.getStoredData(NewOrderActivity.this), orderDetailsResponse);

                                } else {
                                    Constants.alertDialogReject("You have rejected this order. \n" +
                                            " Customer has been \n" +
                                            "notified", NewOrderActivity.this, NewOrderActivity.this);


                                }

                            } else {

                                alertDialog(data.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(NewOrderActivity.this, "Failed! try again.", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            //  Toast.makeText(NewOrderActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogClicked() {
        // btnAccept.setVisibility(View.INVISIBLE);
        accept.setText("Accpted");
        btnAccept.setClickable(false);
        btnReject.setVisibility(View.INVISIBLE);
        finish();
    }

    @Override
    public void onDialogRejectClicked() {
        reject.setText("Rejected");
        btnReject.setClickable(false);
        btnAccept.setVisibility(View.INVISIBLE);
        finish();
        // btnReject.setVisibility(View.INVISIBLE);
    }
}
