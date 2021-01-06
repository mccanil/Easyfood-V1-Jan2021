package com.easyfoodvone.new_order.view.impl;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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

import com.easyfoodvone.utility.PrefManager;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.easyfoodvone.R;
import com.easyfoodvone.adapters.menu_product.MenuItemListAdapter;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.charity.webservice.responsebean.NewDetailBean;
import com.easyfoodvone.helper.PrintEsayFood;
import com.easyfoodvone.login.models.LoginResponse;

import com.easyfoodvone.new_order.adapter.ProductListAdapter;
import com.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.new_order.models.OrderDetailsRequest;
import com.easyfoodvone.orders.models.OrdersListResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.UserPreferences;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
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
    private MenuItemListAdapter mAdapter;
    LoginResponse.Data baseDetails;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout mainLayout;
    LinearLayout orderItemListView;
    CircleImageView resturant_image;
    TextView contact, address;
    LinearLayout actionLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    OrdersListResponse.Orders orderDetail;
    NewDetailBean.OrdersDetailsBean orderDetailsResponse;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        prefManager = PrefManager.getInstance(NewOrderActivity.this);
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
        orderItemListView = findViewById(R.id.orderItemList);
      /*  mAdapter = new MenuItemListAdapter(NewOrderActivity.this);
        RecyclerLayoutManager layoutManager = new RecyclerLayoutManager(1, RecyclerLayoutManager.VERTICAL);
        layoutManager.setScrollEnabled(false);
        listOrderDetails.setLayoutManager(layoutManager);
        listOrderDetails.setAdapter(mAdapter);
        listOrderDetails.setNestedScrollingEnabled(false);*/


        if (getIntent().hasExtra(Constants.ORDER_DETAIL)) {
            orderDetail = (OrdersListResponse.Orders) getIntent().getExtras().get(Constants.ORDER_DETAIL);


            if (orderDetail != null) {
                getOrederDetails(orderDetail.getOrder_num());
                //getOrederDetails(orderDetail.getOrder_num());

            }
        }


        /*if (getIntent().hasExtra(Constants.ORDER_DETAIL)) {
            orderDetail = (OrdersListResponse.Orders) getIntent().getExtras().get(Constants.ORDER_DETAIL);
            if (orderDetail == null) {

                //getOrederDetails(orderDetail.getOrder_num());
                if (getIntent().hasExtra("order_number")) {
                    //orderDetailsNew(getIntent().getExtras().getString("order_number"));
                    getOrederDetails(getIntent().getExtras().getString("order_number"));
                } else {
                    Toast.makeText(this, "Order Not Found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                getOrederDetails(orderDetail.getOrder_num());
                //orderDetailsNew(orderDetail.getOrder_num());
           //     getOrederDetails(getIntent().getExtras().getString("order_number"));

            }
        } else {

            if (getIntent().hasExtra("order_number")) {
                getOrederDetails(getIntent().getExtras().getString("order_number"));
            } else {
                Toast.makeText(this, "Order Not Found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }*/


        //  swipeRefresh = findViewById(R.id.swipeRefresh);
       /* swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                getOrederDetails(orderDetail.getOrder_num());
            }
        });*/


        baseDetails = (LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(this, Constants.LOGIN_RESPONSE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRestaurantDetails();
            }
        });

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


    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
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


    private String formatDate(String inputDate) {

      /*  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date = dateFormat.parse(dealInfo.getDealStartDate());//You will get date object relative to server/client timezone wherever it is parsed
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");*/


        String outputDate = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return outputDate;
    }


    private void setData(NewDetailBean.OrdersDetailsBean orderDetail) {

        txtOrderId.setText(orderDetail.getOrder_num());
        txtAddress.setText(orderDetail.getDelivery_address().getCustomer_location());
        txtTotalAmountPaid.setText(Constants.POUND + orderDetail.getOrder_total());
        if (orderDetail.getOrder_phone_number() != null && !orderDetail.getOrder_phone_number().isEmpty()) {
            txtContact.setText(orderDetail.getOrder_phone_number());
        } else {
            txtContact.setText(orderDetail.getDelivery_address().getPhone_number());
        }

        txtName.setText(orderDetail.getDelivery_address().getCustomer_name());
        txtOrderDate.setText(formatDate(orderDetail.getOrder_date_time()));
        if (orderDetail.getDelivery_option().equalsIgnoreCase("table")) {
            txtDeliveryOption.setText(capitalize(orderDetail.getDelivery_option()) + " (" + orderDetail.getUnitId() + ")");
        } else {
            txtDeliveryOption.setText(capitalize(orderDetail.getDelivery_option()));
        }

        //11 Jul 2019 07:33:50
        txtDeleveryDate.setText(orderDetail.getDelivery_date_time());

        txtDeleveryDate.setText(formatDate(orderDetail.getDelivery_date_time()));
        txtTotalAmountPaid.setText(Constants.POUND + orderDetail.getOrder_total());
        txtPaymentMethod.setText(orderDetail.getPayment_mode());
        txtOrderId.setText(orderDetail.getOrder_num());
        //    txtOrderDate.setText(orderDetail.getOrder_date_time());
        txtDeleveryCharges.setText(Constants.POUND + orderDetail.getDelivery_charge());
        txtSubTotal.setText(Constants.POUND + String.valueOf(orderDetail.getSub_total()));
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


    private void setAdapter(List<NewDetailBean.OrdersDetailsBean.Cart.MenuBean> menuBeans) {
        ProductListAdapter productListAdapter = new ProductListAdapter(this, menuBeans);
        listOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        listOrderDetails.setAdapter(productListAdapter);
    }

    private void setMenuItems(NewDetailBean.OrdersDetailsBean.Cart data) {
        for (int i = 0; i < data.getMenu().size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View root = inflater.inflate(R.layout.order_list_item, null);
            TextView title = root.findViewById(R.id.title);
            TextView eachPrice = root.findViewById(R.id.each);
            TextView price = root.findViewById(R.id.price);
            // Menu menu = data.getMenu().get(i);
            title.setText(data.getMenu().get(i).getQty() + "x " + data.getMenu().get(i).getName());
            eachPrice.setText(getString(R.string.currency) + String.format("%.2f", data.getMenu().get(i).getPrice()));
            price.setText(getString(R.string.currency) + String.format("%.2f", data.getMenu().get(i).getSubtotal()));
            orderItemListView.addView(root);

            if (data.getMenu().get(i).getOptions().getProductModifiers() != null) {
                //  List<ProductModifier> productModifiers = data.getMenu().get(i).getOptions().getProductModifiers();
                for (int j = 0; j < data.getMenu().get(i).getOptions().getProductModifiers().size(); j++) {
                    for (int k = 0; k < data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().size(); k++) {
                        LayoutInflater inflaterModi = LayoutInflater.from(this);
                        View rootModi = inflaterModi.inflate(R.layout.order_list_item_modifiers, null);
                        TextView titleModi = rootModi.findViewById(R.id.title);
                        TextView priceModi = rootModi.findViewById(R.id.price);

                        titleModi.setText(data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getProductName());
                        priceModi.setText(getString(R.string.currency) + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getModifierProductPrice());
                        orderItemListView.addView(rootModi);
                    }
                }
            }
            if (data.getMenu().get(i).getOptions().getMealProducts() != null) {
                //List<MealProduct> mealProducts = data.getMenu().get(i).getOptions().getMealProducts();
                for (int j = 0; j < data.getMenu().get(i).getOptions().getMealProducts().size(); j++) {
                    // List<SizeModifier> sizeModifiers = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers();
                    for (int k = 0; k < data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().size(); k++) {
                        //  List<SizeModifierProduct> sizeModifierProducts = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts();
                        for (int l = 0; l < data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().size(); l++) {
                            LayoutInflater inflaterSizeModi = LayoutInflater.from(this);
                            View rootSizeModi = inflaterSizeModi.inflate(R.layout.order_list_item_modifiers, null);
                            TextView titleModi = rootSizeModi.findViewById(R.id.title);
                            TextView priceModi = rootSizeModi.findViewById(R.id.price);

                            titleModi.setText(data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getQuantity() + "x " + data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getProductName());
                            priceModi.setText(getString(R.string.currency) + String.format("%.2f", Double.parseDouble(data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getAmount())));
                            orderItemListView.addView(rootSizeModi);
                        }
                    }
                }
            }

            if (data.getMenu().get(i).getOptions().getSize() != null && data.getMenu().get(i).getOptions().getSize().getProductSizeName() != null) {

                LayoutInflater sizeModiInflater = LayoutInflater.from(this);
                View rootSize = sizeModiInflater.inflate(R.layout.order_list_item_modifiers, null);
                TextView titleSizeModi = rootSize.findViewById(R.id.title);
                TextView priceSizeModi = rootSize.findViewById(R.id.price);

                titleSizeModi.setText(data.getMenu().get(i).getOptions().getSize().getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getProductSizeName());
                priceSizeModi.setText(getString(R.string.currency) + String.format("%.2f", data.getMenu().get(i).getOptions().getSize().getProductSizePrice()));
                orderItemListView.addView(rootSize);

                for (int j = 0; j < data.getMenu().get(i).getOptions().getSize().getSizemodifiers().size(); j++) {
                    // List<SizeModifierProduct> sizeModifierProducts = data.getMenu().get(i).getOptions().getSizeBeans().getSizemodifiers().get(j).getSizeModifierProducts();
                    for (int k = 0; k < data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().size(); k++) {

                        LayoutInflater inflaterSizeModi = LayoutInflater.from(this);
                        View rootSizeModi = inflaterSizeModi.inflate(R.layout.order_list_item_modifiers, null);
                        TextView titleModi = rootSizeModi.findViewById(R.id.title);
                        TextView priceModi = rootSizeModi.findViewById(R.id.price);

                        titleModi.setText(data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getProductName());
                        priceModi.setText(getString(R.string.currency) + String.format("%.2f", data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getAmount()));
                        orderItemListView.addView(rootSizeModi);
                    }


                }
            }
        }
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


    OrderDetailsRequest request;

  /*  private void orderDetailsNew(String ordersNumber) {
        final LoadingDialog dialog = new LoadingDialog(NewOrderActivity.this, "Loading details...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(NewOrderActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.orderDetailsNeww(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<NewOrdersDetailsResponse>() {
                        @Override
                        public void onSuccess(NewOrdersDetailsResponse data) {
                            orderDetailsResponse = data.getOrdersDetails();
                            mainLayout.setVisibility(View.VISIBLE);

                            btnAccept.setEnabled(true);
                            btnReject.setEnabled(true);
                            swipeRefresh.setRefreshing(false);
                            dialog.dismiss();
                            Log.e("Order details ", data.toString());
                            setData(data.getOrdersDetails());

                           *//* mAdapter.clearData();
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
                            mAdapter.addItem(menuProducts);*//*
                        }

                        @Override
                        public void onError(Throwable exe) {
                            btnAccept.setEnabled(false);
                            btnReject.setEnabled(false);
                            mainLayout.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                            swipeRefresh.setRefreshing(false);

                            Toast.makeText(NewOrderActivity.this, "Loading failed Swipe down to try again!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + exe.getMessage());
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

    }*/


    private void getOrederDetails(String orderNumber) {
        if (isInternetOn(this)) {
            final LoadingDialog dialog = new LoadingDialog(this, "");
            dialog.setCancelable(false);
            dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("order_number", orderNumber);
            com.easyfoodvone.charity.webservice.ApiInterface apiInterface = com.easyfoodvone.charity.webservice.ApiClient.getClient().create(com.easyfoodvone.charity.webservice.ApiInterface.class);
            Call<NewDetailBean> call = apiInterface.getOrderDetail(prefManager.getPreference(AUTH_TOKEN, ""), jsonObject);

            Log.e("Login Request", "" + jsonObject);
            call.enqueue(new Callback<NewDetailBean>() {
                @Override
                public void onResponse(@NonNull Call<NewDetailBean> call, @NonNull Response<NewDetailBean> response) {
                    try {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            //  swipeRefresh.setRefreshing(false);
                            NewDetailBean newDetailBean = response.body();
                            if (newDetailBean.isSuccess()) {
                                orderDetailsResponse = newDetailBean.getOrders_details();
                                setMenuItems(orderDetailsResponse.getCart());
                                setData(orderDetailsResponse);
                                //setAdapter(orderDetailsResponse.getCart().getMenu());
                            } /*else {
                                showSnackBar(view, commonResponseBean.getMessage());
                            }*/

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<NewDetailBean> call, @NonNull Throwable t) {
                    if (dialog != null)
                        dialog.dismiss();
                    //  swipeRefresh.setRefreshing(false);
                    // showSnackBar(view, getString(R.string.msg_please_try_later));
                }
            });
        } else {
            // showSnackBar(view, getString(R.string.no_internet_available_msg));

        }
    }




  /*  @Override
    public void onRefresh() {
        swipe_refresh.setRefreshing(false);
        previousMealsBeans.clear();
        getCharityInfo();
    }*/


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
            disposable.add(apiService.acceptRejectOrders(prefManager.getPreference(AUTH_TOKEN, ""), request)
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
                                    // PrintEsayFood.printOrderNew(NewOrderDetailActivity.this, logo, Constants.getStoredData(NewOrderDetailActivity.this), orderDetailsResponse);

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
