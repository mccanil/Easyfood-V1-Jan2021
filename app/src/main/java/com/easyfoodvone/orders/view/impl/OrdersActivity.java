package com.easyfoodvone.orders.view.impl;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.utility.PrefManager;
import com.google.gson.JsonObject;
import com.easyfoodvone.R;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.charity.CharityFragment;
import com.easyfoodvone.charity.webservice.responsebean.NewDetailBean;
import com.easyfoodvone.fragments.ChangePasswordFragment;
import com.easyfoodvone.fragments.DeleverySettingFragment;
import com.easyfoodvone.fragments.ListOfOffersFragment;
import com.easyfoodvone.fragments.MenuFragment;
import com.easyfoodvone.fragments.OrderListFragment;
import com.easyfoodvone.fragments.OrderReportFragment;
import com.easyfoodvone.fragments.ProfileFragment;
import com.easyfoodvone.fragments.RatingReviewFragment;
import com.easyfoodvone.fragments.RevenueReportFragment;
import com.easyfoodvone.fragments.SetRestaurantTimingsFragment;
import com.easyfoodvone.helper.PrintEsayFood;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.login.view.impl.LoginActivity;
import com.easyfoodvone.menu.CommonRequest;
import com.easyfoodvone.models.ServeStyleResponse;
import com.easyfoodvone.models.SetServeStyleRequest;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.orders.adapter.AdapterOrderList;
import com.easyfoodvone.orders.presenter.OrdersPresenter;
import com.easyfoodvone.restaurant_models.RestaurantOpenCloseRequest;
import com.easyfoodvone.restaurant_models.RestaurantOpenCloseResponse;
import com.easyfoodvone.utility.ApplicationContext;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.UserPreferences;
import com.newrelic.agent.android.NewRelic;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

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
import static com.easyfoodvone.utility.Helper.setFragment;
import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class OrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText input;
    Button action;
    OrdersPresenter presenter;
    @BindView(R.id.home_delevery)
    LinearLayout homeDelevery;
    @BindView(R.id.open)
    LinearLayout open;
    @BindView(R.id.close)
    LinearLayout close;
    @BindView(R.id.home_delevery_off)
    LinearLayout homeDeleveryOff;
    @BindView(R.id.layout_restaurent_name_logo)
    LinearLayout layoutRestaurentNameLogo;
    @BindView(R.id.layout_delevery_on_off)
    LinearLayout layoutDeleveryOnOff;
    private AdapterOrderList mAdapter;
    private boolean isOpen = false;
    private DrawerLayout mDrawerLayout;
    CircleImageView restaurant_logo;
    TextView restaurant_name, postal_code;
    LoginResponse.Data baseDetails;
    boolean isHomeDelivery = false;
    boolean isDashBoard = true;
    TextView serve_style;
    ServeStyleResponse serveStyleResponse;
    Dialog dialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ImageView ivNav;
    Toolbar toolbar;
    TextView tvToolbarTitle;
    DrawerLayout drawer;
    private static OrdersActivity instance = null;
    int fragCount = 1;
    PrefManager prefManager;
    // NewDetailBean.OrdersDetailsBean orderDetailsResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.setStatusBarGradiant(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        instance = this;
        setContentView(R.layout.activity_orders);
        prefManager = PrefManager.getInstance(OrdersActivity.this);
        NewRelic.withApplicationToken(
                "eu01xx8e8c3e4790f9795fc7133941ac935ff9a204"
        ).start(this.getApplication());
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(null);

        restaurant_name = findViewById(R.id.restaurant_name);
        restaurant_logo = findViewById(R.id.restaurant_logo);
        serve_style = findViewById(R.id.serve_style);
        postal_code = findViewById(R.id.post_code);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivNav = findViewById(R.id.iv_nav);
        serveStyleResponse = new ServeStyleResponse();
        baseDetails = (LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(this, Constants.LOGIN_RESPONSE);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ivNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ImageView navCancel = (ImageView) headerView.findViewById(R.id.back_menu);

        // toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (getIntent().hasExtra(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
            if (getIntent().getStringExtra(Constants.NOTIFICATION_TYPE_ACCEPTED).equals(Constants.NOTIFICATION_TYPE_ACCEPTED) &&
                    UserPreferences.getUserPreferences().getResponse(OrdersActivity.this, Constants.LOGIN_RESPONSE) != null) {
               /* if (getIntent().getStringExtra(Constants.ORDER_NUMBER) != null) {
                    //getOrederDetails(getIntent().getStringExtra(Constants.ORDER_NUMBER));
                }*/
                loadFragment(new OrderListFragment(getApplicationContext()));
                onTabClickEvents();


            } else {
                Constants.switchActivity(OrdersActivity.this, OrdersActivity.class);
                finish();
            }

        } else {
            //TODO: Default page loder

            loadFragment(new OrderListFragment(getApplicationContext()));
            onTabClickEvents();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.END);
            }
        });

        serve_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServeStyle();

//                Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("com.lexxdigital.easyfoodvone",
//                        getPackageManager().getPackageArchiveInfo(, 0).packageName,null));
//                startActivity(intent);

//                Uri packageURI = Uri.parse("package:" + "com.lexxdigital.easyfoodvone");
//                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
////                uninstallIntent.setAction(Intent.ACTION_VIEW);
////                uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(uninstallIntent);


            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {

                    Constants.ORDER_COUNT = Constants.ORDER_COUNT + 1;
                    if (dialog != null && dialog.isShowing()) {
                        ((TextView) dialog.findViewById(R.id.txtmsg)).setText((Constants.ORDER_COUNT) + " New order arrived");
                    } else {
                        if (intent.hasExtra("message")) {
                            String message = intent.getStringExtra("message");
                            dialog = new Dialog(OrdersActivity.this);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.order_alert_dialog_layout);
                            TextView txtmsg = dialog.findViewById(R.id.txtmsg);
                            TextView feednow = dialog.findViewById(R.id.feednow);
                            TextView later = dialog.findViewById(R.id.later);

                            txtmsg.setText((Constants.ORDER_COUNT) + " New order arrived");

                            feednow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    try{
//                                        ApplicationContext.getInstance().stopNotificationSound();
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                    }
                                    loadFragment(new OrderListFragment(getApplicationContext()));
                                    onTabClickEvents();
                                    Constants.ORDER_COUNT = 0;
                                    dialog.dismiss();
                                }
                            });

                            later.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.dismiss();

                        }
                    }


                } else if (intent.getAction().equalsIgnoreCase("local")) {
                    String action = intent.getStringExtra("type");

                    if (action.equalsIgnoreCase("closed")) {
                        open.setVisibility(View.GONE);
                        close.setVisibility(View.VISIBLE);
                    } else if (action.equalsIgnoreCase("open")) {
                        open.setVisibility(View.VISIBLE);
                        close.setVisibility(View.GONE);
                    }
                  /*  if (action.equalsIgnoreCase("open")) {
                        isOpen = true;
                        //TODO: Open here....
                        openRestaurant("open", baseDetails.getRestaurant_id());

                    } else if (action.equalsIgnoreCase("closed")) {
                        isOpen = false;
                        openRestaurant("closed", baseDetails.getRestaurant_id());
                    }*/
                }
            }
        };

        setRestaurantDetails();

    }

    public static OrdersActivity getInstance() {
        return instance;
    }

    public void updateRestaurantStatus(boolean isOpen) {
        if (isOpen) {
            open.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
        } else {
            open.setVisibility(View.GONE);
            close.setVisibility(View.VISIBLE);
        }
    }

    public void setBackAction(int backNo) {
        fragCount = backNo;
    }

    private void getServeStyle() {
        final LoadingDialog dialog = new LoadingDialog(OrdersActivity.this, "getting serve style");
        dialog.setCancelable(false);
        dialog.show();

        SetServeStyleRequest request = new SetServeStyleRequest();
        request.setRestaurant_id(Constants.getStoredData(OrdersActivity.this).getRestaurant_id());

        try {
            ApiInterface apiService = ApiClient.getClient(OrdersActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getServerStyle(prefManager.getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ServeStyleResponse>() {
                        @Override
                        public void onSuccess(ServeStyleResponse serveStyleResponse) {
                            dialog.dismiss();
                            if (serveStyleResponse.isSuccess()) {
                                final Dialog dialog = new Dialog(OrdersActivity.this);
                                dialog.setContentView(R.layout.serve_style_layout);

                                final CheckBox delivery, diniin, collection;
                                Button save, cancel;


                                delivery = dialog.findViewById(R.id.delivery);
                                diniin = dialog.findViewById(R.id.dinein);
                                collection = dialog.findViewById(R.id.collection);
                                save = dialog.findViewById(R.id.save);
                                cancel = dialog.findViewById(R.id.cancel);

                                if (serveStyleResponse.getData().getCollection() == 1) {
                                    collection.setChecked(true);
                                }
                                if (serveStyleResponse.getData().getDelivery() == 1) {
                                    delivery.setChecked(true);
                                }
                                if (serveStyleResponse.getData().getDine_in() == 1) {
                                    diniin.setChecked(true);
                                }


                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveServeStyle(delivery.isChecked() ? 1 : 0, diniin.isChecked() ? 1 : 0, collection.isChecked() ? 1 : 0, dialog);
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });


                                dialog.show();


                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(OrdersActivity.this, "Unable to process your request", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(OrdersActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveServeStyle(final int i, final int i1, final int i2,
                                final Dialog dialog2) {
        final LoadingDialog dialog = new LoadingDialog(OrdersActivity.this, "Updating serve style");
        dialog.setCancelable(false);
        dialog.show();

        SetServeStyleRequest request = new SetServeStyleRequest();
        request.setRestaurant_id(Constants.getStoredData(OrdersActivity.this).getRestaurant_id());
        request.setCollection(i2 + "");
        request.setDine_in(i1 + "");
        request.setDelivery(i + "");

        try {
            ApiInterface apiService = ApiClient.getClient(OrdersActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.setServeStyle(prefManager.getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ServeStyleResponse>() {
                        @Override
                        public void onSuccess(ServeStyleResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                dialog2.dismiss();
                                Toast.makeText(OrdersActivity.this, "Serve Style saved", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            dialog2.dismiss();
                            Toast.makeText(OrdersActivity.this, "Unable to update ", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            dialog2.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(OrdersActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void setRestaurantDetails() {
        if (baseDetails != null) {
            Picasso.get().load(baseDetails.getLogo())
                    .placeholder(R.drawable.restaurant_icon)
                    .error(R.drawable.restaurant_icon)
                    .into(restaurant_logo);


            restaurant_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDashBoard = false;
                    layoutDeleveryOnOff.setVisibility(View.VISIBLE);
                    layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
                    ActionBar ab = getSupportActionBar();
                    ab.setTitle("Your Profile");
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout, new ProfileFragment(getApplicationContext(), OrdersActivity.this));
                    ft.commit();
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            });

            postal_code.setText(baseDetails.getPost_code());
            restaurant_name.setText(baseDetails.getRestaurant_name());


            if (baseDetails.isOpen()) {
                isOpen = baseDetails.isOpen();
                open.setVisibility(View.VISIBLE);
                close.setVisibility(View.GONE);

            } else {
                close.setVisibility(View.VISIBLE);
                open.setVisibility(View.GONE);
                isOpen = false;
            }


        }
    }


    public void onTabClickEvents() {

        open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isOpen = false;
                openRestaurant("closed", baseDetails.getRestaurant_id());
            }
        });
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isOpen = true;
                //TODO: Open here....
                openRestaurant("open", baseDetails.getRestaurant_id());
            }
        });

    }


    private void printOrder(NewDetailBean.OrdersDetailsBean orderDetailsResponse) {
        byte[] logoByte = UserPreferences.getUserPreferences().getByteArray(OrdersActivity.this, Constants.RESTAURANT_LOGO);
        Bitmap logo = null;
        if (logoByte != null) {
            logo = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
//                    logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), 80, false);
        }

        if (orderDetailsResponse != null) {
//                    Print ANAND
            PrintEsayFood.printOrderNew(OrdersActivity.this, logo, Constants.getStoredData(OrdersActivity.this), orderDetailsResponse);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            isDashBoard = true;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Orders");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new OrderListFragment(getApplicationContext()));
            ft.commit();
        } else if (id == R.id.nav_menu) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Menu");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new MenuFragment(getApplicationContext()));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_delivery_settings) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.GONE);
            layoutRestaurentNameLogo.setVisibility(View.GONE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Set Delivery Charges");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new DeleverySettingFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_restaurant_timings) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Set Restaurant Timings");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new SetRestaurantTimingsFragment(OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_orders_report) {//
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Orders Report");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new OrderReportFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_revenue_report) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Revenue Report");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new RevenueReportFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_offers) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("List of Offers");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new ListOfOffersFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_review_ratings) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Ratings & Reviews");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new RatingReviewFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_charity) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.GONE);
            layoutRestaurentNameLogo.setVisibility(View.GONE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Charity");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new CharityFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_profile) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.VISIBLE);
            layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Your Profile");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new ProfileFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_change_password) {
            isDashBoard = false;
            layoutDeleveryOnOff.setVisibility(View.GONE);
            layoutRestaurentNameLogo.setVisibility(View.GONE);
            ActionBar ab = getSupportActionBar();
            tvToolbarTitle.setText("Change Password");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new ChangePasswordFragment(getApplicationContext(), OrdersActivity.this));
            ft.commit();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_logout) {
            try {
                ApplicationContext.getInstance().stopNotificationSound();
            } catch (Exception e) {
                e.printStackTrace();
            }
            logoutNow();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    //TODO:  Login now....
    public void logoutNow() {
        dialog = new LoadingDialog(OrdersActivity.this, "Logging out...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            CommonRequest commonRequest = new CommonRequest();
            commonRequest.setUser_id(Constants.getStoredData(OrdersActivity.this).getUser_id());
            commonRequest.setFcm_id(UserPreferences.getUserPreferences().getString(OrdersActivity.this, Constants.FIREBASE_TOKEN) != null ? UserPreferences.getUserPreferences().getString(OrdersActivity.this, Constants.FIREBASE_TOKEN) : "");

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.logout(prefManager.getPreference(AUTH_TOKEN, ""), commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                isDashBoard = false;
                                UserPreferences.getUserPreferences().clearPrefrences(OrdersActivity.this);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(OrdersActivity.this, "Unable to logout", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(OrdersActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    }));
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(OrdersActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    private void loadFragment(OrderListFragment fragment) {
        ActionBar ab = getSupportActionBar();
        //ab.setTitle("Orders");
        tvToolbarTitle.setText("Orders");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }

    public void openRestaurant(final String openOrClose, String restaurantId) {
        final LoadingDialog dialog = new LoadingDialog(OrdersActivity.this, openOrClose.equals("closed") ? "Closing Restaurant..." : "Opening Restaurant...");
        dialog.setCancelable(false);
        dialog.show();

        RestaurantOpenCloseRequest restaurantOpenCloseRequest = new RestaurantOpenCloseRequest();
        restaurantOpenCloseRequest.setOpen_close(openOrClose);
        restaurantOpenCloseRequest.setRestaurant_id(restaurantId);

        try {
            ApiInterface apiService = ApiClient.getClient(OrdersActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.openCloseRestaurant(prefManager.getPreference(AUTH_TOKEN, ""), restaurantOpenCloseRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<RestaurantOpenCloseResponse>() {
                        @Override
                        public void onSuccess(RestaurantOpenCloseResponse data) {
                            if (data.isSuccess()) {
                                dialog.dismiss();
                                LoginResponse.Data loginResponse = Constants.getStoredData(OrdersActivity.this);
                                loginResponse.setHome_delivery(openOrClose.equals("open"));
                                loginResponse.setOpen(openOrClose.equals("open"));

                                UserPreferences.getUserPreferences().setResponse(OrdersActivity.this, Constants.LOGIN_RESPONSE, loginResponse);

                                if (openOrClose.equalsIgnoreCase("closed")) {
                                    open.setVisibility(View.GONE);
                                    close.setVisibility(View.VISIBLE);
                                } else if (openOrClose.equalsIgnoreCase("open")) {
                                    open.setVisibility(View.VISIBLE);
                                    close.setVisibility(View.GONE);
                                }

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(OrdersActivity.this, "Unable process your request", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(OrdersActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (isDashBoard) {

            TextView yes, no, messge;
            CardView cvYes, cvNo;
            final Dialog dialog = new Dialog(this);
            View layoutView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null, false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(layoutView);
            cvYes = layoutView.findViewById(R.id.cv_yes);
            cvNo = layoutView.findViewById(R.id.cv_no);
            yes = layoutView.findViewById(R.id.btn_yes);
            no = layoutView.findViewById(R.id.btn_no);
            messge = layoutView.findViewById(R.id.txt_message);

            messge.setText("Do you want exit?");
            yes.setText("YES");
            cvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    finish();
                }
            });
            no.setText("NO");
            no.setTextColor(getResources().getColor(R.color.black));
            cvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } else {

            if (fragCount > 1) {
                CharityFragment.getInstance().getCharityInfo();
                CharityFragment successFragment = new CharityFragment();
                setFragment(successFragment, false, this, R.id.frameLayout);

            } else {
                isDashBoard = true;
                layoutDeleveryOnOff.setVisibility(View.VISIBLE);
                layoutRestaurentNameLogo.setVisibility(View.VISIBLE);
                ActionBar ab = getSupportActionBar();
                //ab.setTitle("Orders");
                tvToolbarTitle.setText("Orders");
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, new OrderListFragment(getApplicationContext()));
                ft.commit();
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_TYPE_ACCEPTED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("local"));
        loadRestaurantLogo();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(OrdersActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void loadRestaurantLogo() {
        if (UserPreferences.getUserPreferences().getByteArray(this, Constants.RESTAURANT_LOGO) != null) {
            return;
        }
        Picasso.get().load(Constants.getStoredData(this).getLogo()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteImage = stream.toByteArray();
                UserPreferences.getUserPreferences().storeByteArray(OrdersActivity.this, byteImage, Constants.RESTAURANT_LOGO);

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e("onBitmapFailed", "RestaurantLogo Not Load");

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("onPrepareLoad", "RestaurantLogo Not Load");

            }
        });
    }


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
                                Toast.makeText(OrdersActivity.this, "Success Print Now", Toast.LENGTH_SHORT).show();
                                // orderDetailsResponse = newDetailBean.getOrders_details();
                                //printOrder(newDetailBean.getOrders_details());
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
}
