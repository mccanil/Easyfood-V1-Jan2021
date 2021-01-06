package com.easyfoodvone.menu_details.view.impl;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.dialogs.EditMenuItemsDialog;
import com.easyfoodvone.helper.OnStartDragListener;
import com.easyfoodvone.helper.SimpleItemTouchHelperCallback;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.menu.CommonRequest;
import com.easyfoodvone.menu.MenuCategoryList;
import com.easyfoodvone.menu_details.adapter.AdapterItemsList;
import com.easyfoodvone.menu_details.models.MenuCategoryItemsResponse;
import com.easyfoodvone.menu_details.models.MenuProductDetails;
import com.easyfoodvone.models.UpdateMenuCategoryRequest;
import com.easyfoodvone.models.UpdateMenuProductRequest;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class MenuDetailsActivity extends AppCompatActivity implements OnStartDragListener, AdapterItemsList.OnItemPositionChanged, EditMenuItemsDialog.UpdateClickListener {
    @BindView(R.id.list_items)
    RecyclerView listItems;
    @BindView(R.id.list_title_main)
    TextView listTitleMain;
    @BindView(R.id.list_items_count)
    TextView listItemsCount;
    @BindView(R.id.btn_main_inactive)
    LinearLayout btnMainInactive;
    @BindView(R.id.btn_main_active)
    LinearLayout btnMainActive;
    @BindView(R.id.btn_edit)
    Button btnEdit;
    @BindView(R.id.btn_edit_inactive)
    Button btnEditInactive;
    private ItemTouchHelper mItemTouchHelper;
    private boolean isOpen = true;
    MenuCategoryList.MenuCategories menuCategories;
    LoginResponse.Data baseDetails;
    boolean isMenuActive = false;
    boolean active = false;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    AdapterItemsList adapter;
    List<MenuCategoryItemsResponse.Items> itemList;
    PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_details);
        prefManager = PrefManager.getInstance(MenuDetailsActivity.this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        baseDetails = Constants.getStoredData(this);


        //TODO: Getting intent order value......
        if (getIntent().hasExtra("menu")) {
            menuCategories = (MenuCategoryList.MenuCategories) getIntent().getExtras().get("menu");
            if (menuCategories == null) {
                Toast.makeText(this, "Menu Not Found", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                setData();
                getMenuDetails(menuCategories.getMenu_category_id());

            }
        } else {
            Toast.makeText(this, "Menu Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                    if (intent.hasExtra("message")) {
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(MenuDetailsActivity.this, message);

                    }

                }
            }
        };


        onTabClickEvents();
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_TYPE_ACCEPTED));
    }

    private void setData() {

        listTitleMain.setText(menuCategories.getMenu_category_name());
        listItemsCount.setText("(" + menuCategories.getNumber_of_menu_product() + " Items)");
        if (menuCategories.getMenu_category_status() != null)
            if (menuCategories.getMenu_category_status().equals("1")) {
                menuDeactive();
            } else {
                menuActive();
            }

        //TODO: Open dialer on click the phone icon...
    }


    public void onTabClickEvents() {

        btnMainActive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                activeDeactiveMenu(false);
            }
        });
        btnMainInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeDeactiveMenu(true);
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    private void menuDeactive() {
        // TODO Auto-generated method stub
        btnMainActive.setVisibility(View.VISIBLE);
        btnMainInactive.setVisibility(View.GONE);
        listTitleMain.setTextColor(getResources().getColor(R.color.bg_login_start));
        listItemsCount.setTextColor(getResources().getColor(R.color.txt_orderid));
        btnEditInactive.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
    }

    private void menuActive() {
        btnMainInactive.setVisibility(View.VISIBLE);
        btnMainActive.setVisibility(View.GONE);
        listTitleMain.setTextColor(getResources().getColor(R.color.inactive_category));
        listItemsCount.setTextColor(getResources().getColor(R.color.inactive_category_count));
        btnEditInactive.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.GONE);
    }

    public void setItemsList(List<MenuCategoryItemsResponse.Items> menu_items) {

        adapter = new AdapterItemsList(getApplicationContext(), MenuDetailsActivity.this, this, menuCategories, menu_items, menuCategories.getMenu_category_status().equals("1"));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        listItems.setLayoutManager(mLayoutManager);
        listItems.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(listItems);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder, int position) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void alertDialog() {
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View mDialogView = factory.inflate(R.layout.popup_edit_menu_category, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this).create();
        mDialog.setView(mDialogView);

        final EditText menuCatName = mDialogView.findViewById(R.id.menuCatName);
        menuCatName.setText(menuCategories.getMenu_category_name());


        isMenuActive = menuCategories.getMenu_category_status().equals("1");

        if (menuCategories.getMenu_category_status().equals("1")) {
            mDialogView.findViewById(R.id.btn_item_active).setVisibility(View.VISIBLE);
            mDialogView.findViewById(R.id.btn_item_inactive).setVisibility(View.GONE);
        } else {
            mDialogView.findViewById(R.id.btn_item_active).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.btn_item_inactive).setVisibility(View.VISIBLE);
        }


        mDialogView.findViewById(R.id.btn_item_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isMenuActive = false;
                mDialogView.findViewById(R.id.btn_item_active).setVisibility(View.GONE);
                mDialogView.findViewById(R.id.btn_item_inactive).setVisibility(View.VISIBLE);
            }
        });
        mDialogView.findViewById(R.id.btn_item_inactive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMenuActive = true;
                mDialogView.findViewById(R.id.btn_item_active).setVisibility(View.VISIBLE);
                mDialogView.findViewById(R.id.btn_item_inactive).setVisibility(View.GONE);
            }
        });
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(menuCatName.getText().toString()))
                    menuCatName.setError("Enter Category Name");
                else
                    updateMenuCategory(menuCatName.getText().toString(), isMenuActive, mDialog);
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


    private void getMenuDetails(String menuCategoryId) {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(baseDetails.getRestaurant_id());
            request.setMenu_category_id(menuCategoryId);

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getMenuCategoryItems(prefManager.getPreference(AUTH_TOKEN, ""), request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<MenuCategoryItemsResponse>() {
                        @Override
                        public void onSuccess(MenuCategoryItemsResponse data) {


                            if (data.isSuccess()) {
                                itemList = data.getItems();
                                setItemsList(data.getItems());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                            Toast.makeText(MenuDetailsActivity.this, "Loading failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {

            Log.e("Exception ", e.toString());
            Toast.makeText(MenuDetailsActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void activeDeactiveMenu(final boolean isActive) {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(baseDetails.getRestaurant_id());
            request.setMenu_category_id(menuCategories.getMenu_category_id());
            request.setMenu_category_status(isActive ? 1 + "" : 0 + "");

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.activeDeactiveMenu(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            if (data.isSuccess()) {
                                if (isActive) {
                                    menuDeactive();
                                } else {
                                    menuActive();
                                }

                                adapter = new AdapterItemsList(getApplicationContext(), MenuDetailsActivity.this, MenuDetailsActivity.this, menuCategories, itemList, isActive);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(MenuDetailsActivity.this);
                                listItems.setLayoutManager(mLayoutManager);
                                listItems.setAdapter(adapter);
                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                            //  Toast.makeText(MenuDetailsActivity.this, "Loading failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {

            Log.e("Exception ", e.toString());
            Toast.makeText(MenuDetailsActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    private void updateMenuCategory(final String name, final boolean isActive, final AlertDialog mDialog) {

        final LoadingDialog dialog = new LoadingDialog(MenuDetailsActivity.this, "Updating menu....");
        dialog.setCancelable(false);
        dialog.show();
        try {
            UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest();
            request.setMenu_category_id(menuCategories.getMenu_category_id());
            request.setMenu_category_name(name);
            request.setMenu_category_status(isActive ? 1 + "" : 0 + "");

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.updateMenuCategory(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            mDialog.dismiss();
                            if (data.isSuccess()) {
                                if (isActive) {
                                    menuDeactive();
                                } else {
                                    menuActive();
                                }

                                listTitleMain.setText(name);
                                adapter = new AdapterItemsList(getApplicationContext(), MenuDetailsActivity.this, MenuDetailsActivity.this, menuCategories, itemList, isActive);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(MenuDetailsActivity.this);
                                listItems.setLayoutManager(mLayoutManager);
                                listItems.setAdapter(adapter);

                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            mDialog.dismiss();
                            Toast.makeText(MenuDetailsActivity.this, "Updation failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            mDialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(MenuDetailsActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onItemPositionChanged(int currentPosition, int previousPosition, MenuCategoryItemsResponse.Items itemList) {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(MenuDetailsActivity.this).getRestaurant_id());
            request.setMenu_product_id(itemList.getMenu_product_id());
            request.setMenu_product_new_position(previousPosition + "");
            request.setMenu_product_current_position(currentPosition + "");

            ApiInterface apiService = ApiClient.getClient(MenuDetailsActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getMenuCategoryItemsPositin(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            //Toast.makeText(MenuDetailsActivity.this, "Replaced", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(Throwable e) {
                            //Toast.makeText(MenuDetailsActivity.this, "Loading failed \n Swipe down to try again!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void onActivateDeavtivateProduct(MenuCategoryItemsResponse.Items menuCategories, int position, final AdapterItemsList.ItemViewHolder holder, final boolean isActive) {

        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(MenuDetailsActivity.this).getRestaurant_id());
            request.setMenu_product_id(menuCategories.getMenu_product_id());
            request.setMenu_status(isActive ? 1 + "" : 0 + "");

            ApiInterface apiService = ApiClient.getClient(MenuDetailsActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.activeDeactiveMenuProduct(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            if (data.isSuccess()) {
                                if (isActive) {
                                    holder.btnActive.setVisibility(View.VISIBLE);
                                    holder.btnInactive.setVisibility(View.GONE);
                                    holder.mItemName.setTextColor(getResources().getColor(R.color.txt_orderid));
                                    holder.mPrice.setTextColor(getResources().getColor(R.color.txt_orderid));
                                    holder.btnEditInactive.setVisibility(View.GONE);
                                    holder.btnEdit.setVisibility(View.VISIBLE);
                                } else {
                                    holder.btnActive.setVisibility(View.GONE);
                                    holder.btnInactive.setVisibility(View.VISIBLE);
                                    holder.mItemName.setTextColor(getResources().getColor(R.color.inactive));
                                    holder.mPrice.setTextColor(getResources().getColor(R.color.inactive));
                                    holder.btnEditInactive.setVisibility(View.VISIBLE);
                                    holder.btnEdit.setVisibility(View.GONE);

                                }
                            } else
                                Toast.makeText(MenuDetailsActivity.this, "Unable to process your request", Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onError(Throwable e) {
                            //Toast.makeText(MenuDetailsActivity.this, "Loading failed \n Swipe down to try again!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onEditClicked(int position, AdapterItemsList.ItemViewHolder holder, MenuCategoryItemsResponse.Items items) {
        alertDialogMPIN(position, holder, items.getMenu_product_id());


    }

    public void alertDialogMPIN(final int position, final AdapterItemsList.ItemViewHolder holder, final String menu_product_id) {
        final char[] pin = Constants.getStoredData(MenuDetailsActivity.this).getPincode().toCharArray();
        LayoutInflater factory = LayoutInflater.from(MenuDetailsActivity.this);
        final View mDialogView = factory.inflate(R.layout.popup_enter_mpin, null);
        final AlertDialog mDialog = new AlertDialog.Builder(MenuDetailsActivity.this).create();
        mDialog.setCancelable(false);
        mDialog.setView(mDialogView);
        final EditText firstMpin = mDialogView.findViewById(R.id.mpin_first);
        final EditText secondMpin = mDialogView.findViewById(R.id.mpin_two);
        final EditText thirdMpin = mDialogView.findViewById(R.id.mpin_three);
        final EditText fourthMpin = mDialogView.findViewById(R.id.mpin_four);
        final TextView error = mDialogView.findViewById(R.id.txt_error_message);
        final TextView msg = mDialogView.findViewById(R.id.txt_message);
        msg.setText("Please enter 4 digit pin code \n" +
                "to edit the item");
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

                    getMenuItemDetail(menu_product_id, mDialog);

                    // alertDialog(position, holder);

                } else {
                    error.setVisibility(View.VISIBLE);
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

    public void alertDialog(final int position, final AdapterItemsList.ItemViewHolder holder) {


        LayoutInflater factory = LayoutInflater.from(MenuDetailsActivity.this);
        final View mDialogView = factory.inflate(R.layout.popup_edit_menu_item, null);
        final AlertDialog mDialog = new AlertDialog.Builder(MenuDetailsActivity.this).create();
        mDialog.setView(mDialogView);

        TextView menuCatName = mDialogView.findViewById(R.id.menuCatName);
        menuCatName.setText(menuCategories.getMenu_category_name());
        final EditText itemName = mDialogView.findViewById(R.id.itemName);
        itemName.setText(itemList.get(position).getMenu_product_name());
        final TextView price = mDialogView.findViewById(R.id.price);
        price.setText(itemList.get(position).getMenu_product_price());

        active = itemList.get(position).getActive().equals("1");

        if (active) {
            deactiveItems(holder);
        } else
            activeItem(holder);


        mDialogView.findViewById(R.id.btn_item_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                active = false;
                mDialogView.findViewById(R.id.btn_item_active).setVisibility(View.GONE);
                mDialogView.findViewById(R.id.btn_item_inactive).setVisibility(View.VISIBLE);
            }
        });
        mDialogView.findViewById(R.id.btn_item_inactive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                active = true;

                mDialogView.findViewById(R.id.btn_item_active).setVisibility(View.VISIBLE);
                mDialogView.findViewById(R.id.btn_item_inactive).setVisibility(View.GONE);
            }
        });
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();

                updatePriceOfItem(position, itemName.getText().toString(), price.getText().toString(), mDialog, holder, active);

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

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this).create();
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

    public void updatePriceOfItem(int position, final String itemName, final String price, final AlertDialog mDialog, final AdapterItemsList.ItemViewHolder holder, final boolean isActive) {
        final LoadingDialog dialog = new LoadingDialog(this, "Updating menu....");
        dialog.setCancelable(false);
        dialog.show();
        try {
            UpdateMenuProductRequest request = new UpdateMenuProductRequest();
            request.setMenu_category_id(itemList.get(position).getMenu_product_id());
            request.setMenu_product_name(itemName);
            request.setMenu_product_price(price);
            request.setMenu_status(isActive ? 1 + "" : 0 + "");

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.updateMenuProduct(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            mDialog.dismiss();
                            if (data.isSuccess()) {
                                if (isActive) {
                                    deactiveItems(holder);
                                } else {
                                    activeItem(holder);
                                }

                                holder.mPrice.setText(Constants.POUND + price);
                                holder.mItemName.setText(itemName);
                                alertDialog("Category item updated\nsuccessfully.");

                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            mDialog.dismiss();
                            Toast.makeText(MenuDetailsActivity.this, "Updation failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            mDialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(MenuDetailsActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    public void getMenuItemDetail(String menu_product_id, final AlertDialog mDialog) {
        final LoadingDialog dialog = new LoadingDialog(this, "");
        dialog.setCancelable(false);
        dialog.show();
        try {
            CommonRequest commonRequest = new CommonRequest();
            commonRequest.setMenu_product_id(menu_product_id);
            commonRequest.setRestaurant_id(Constants.getStoredData(MenuDetailsActivity.this).getRestaurant_id());
//            commonRequest.setMenu_product_id("53a35bd8-5229-11e9-857f-0657952ed75a");
//            commonRequest.setRestaurant_id("f32ac07c-4f97-11e9-85a2-0657952ed75a");

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getMenuDetails(prefManager.getPreference(AUTH_TOKEN,""),commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<MenuProductDetails>() {
                        @Override
                        public void onSuccess(MenuProductDetails data) {
                            dialog.dismiss();
                            mDialog.dismiss();
                            if (data.isSuccess()) {
                                EditMenuItemsDialog dialog = new EditMenuItemsDialog(MenuDetailsActivity.this, menuCategories, data, MenuDetailsActivity.this);
                                dialog.setCancelable(false);
                                dialog.show();


                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            mDialog.dismiss();

                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            mDialog.dismiss();
            Log.e("Exception ", e.toString());
            e.printStackTrace();
        }


    }

    public void saveMenuItemDetail(MenuProductDetails menuDetail, final EditMenuItemsDialog editMenuItemsDialog) {
        final LoadingDialog dialog = new LoadingDialog(this, "");
        dialog.setCancelable(false);
        dialog.show();
        try {
            MenuProductDetails commonRequest = new MenuProductDetails();
            commonRequest.setMenu_product_id(menuDetail.getData().getId());
            commonRequest.setRestaurant_id(Constants.getStoredData(MenuDetailsActivity.this).getRestaurant_id());
//            commonRequest.setMenu_product_id("53a35bd8-5229-11e9-857f-0657952ed75a");
//            commonRequest.setRestaurant_id("f32ac07c-4f97-11e9-85a2-0657952ed75a");
            commonRequest.setData(menuDetail.getData());

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.saveMenuDetails(prefManager.getPreference(AUTH_TOKEN,""),commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();

                            if (data.isSuccess()) {
                                editMenuItemsDialog.dismiss();
                                getMenuDetails(menuCategories.getMenu_category_id());
                            } else {
                                Toast.makeText(MenuDetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                Log.e("Error  ----- ", data.getMessage());

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
            e.printStackTrace();
        }


    }


    private void activeItem(AdapterItemsList.ItemViewHolder holder) {
        holder.btnActive.setVisibility(View.GONE);
        holder.btnInactive.setVisibility(View.VISIBLE);
        holder.mItemName.setTextColor(getResources().getColor(R.color.inactive));
        holder.mPrice.setTextColor(getResources().getColor(R.color.inactive));
        holder.btnEditInactive.setVisibility(View.VISIBLE);
        holder.btnEdit.setVisibility(View.GONE);

    }

    private void deactiveItems(AdapterItemsList.ItemViewHolder holder) {
        holder.btnActive.setVisibility(View.VISIBLE);
        holder.btnInactive.setVisibility(View.GONE);
        holder.mItemName.setTextColor(getResources().getColor(R.color.txt_orderid));
        holder.mPrice.setTextColor(getResources().getColor(R.color.txt_orderid));
        holder.btnEditInactive.setVisibility(View.GONE);
        holder.btnEdit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdateClicked(MenuProductDetails menuDetail, EditMenuItemsDialog editMenuItemsDialog) {
        // saveMenuItemDetail();
        Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show();
        Log.e("Updated    ---------- ", menuDetail.toString());

        saveMenuItemDetail(menuDetail, editMenuItemsDialog);

    }
}
