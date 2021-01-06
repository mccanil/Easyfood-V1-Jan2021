package com.easyfoodvone.spend_x_get_x_discount.view.impl;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.spend_x_get_x_discount.BucketDataModel;
import com.easyfoodvone.spend_x_get_x_discount.BucketsAdapter;
import com.easyfoodvone.spend_x_get_x_discount.BucketsAdapter.OnAdapterItemClickListener;
import com.easyfoodvone.spend_x_get_x_discount.SpendXgetXdiscountRequest;
import com.easyfoodvone.spend_x_get_x_discount.presenter.SpendXgetXdiscountPresenter;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class SpendXgetXdiscountActivity extends AppCompatActivity implements OnAdapterItemClickListener {
    EditText input;
    Button action;
    SpendXgetXdiscountPresenter presenter;
    @BindView(R.id.edit_offer_title)
    EditText editOfferTitle;
    @BindView(R.id.checkbox_all)
    CheckBox checkboxAll;
    @BindView(R.id.checkbox_mon)
    CheckBox checkboxMon;
    @BindView(R.id.checkbox_tue)
    CheckBox checkboxTue;
    @BindView(R.id.checkbox_wed)
    CheckBox checkboxWed;
    @BindView(R.id.checkbox_thu)
    CheckBox checkboxThu;
    @BindView(R.id.checkbox_fri)
    CheckBox checkboxFri;
    @BindView(R.id.checkbox_sat)
    CheckBox checkboxSat;
    @BindView(R.id.checkbox_sun)
    CheckBox checkboxSun;

    @BindView(R.id.edit_between)
    EditText editBetween;
    @BindView(R.id.edit_and)
    EditText editAnd;
    @BindView(R.id.edit_give_discount)
    EditText editGiveDiscount;
    @BindView(R.id.btn_add_more_bucket)
    Button btnAddMoreBucket;
    @BindView(R.id.spinner_order_availibility)
    Spinner spinnerOrderAvailibility;
    @BindView(R.id.edit_terms_condition)
    EditText editTermsCondition;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    RecyclerView bucketsRecycler;
    List<BucketDataModel> bucketDataModels;
    BucketsAdapter adapter;
    EditText offer_descripton;
    TextView applicableOn, activeFrom, activeTo, startDate, endDate;

    String days[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    boolean daysBool[] = {false, false, false, false, false, false, false};
    BroadcastReceiver mRegistrationBroadcastReceiver;
    PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_xget_xdiscount);
        prefManager=PrefManager.getInstance(SpendXgetXdiscountActivity.this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //TODO: Init views
        bucketsRecycler = findViewById(R.id.bucketsRecycler);
        applicableOn = findViewById(R.id.applicableOn);
        activeFrom = findViewById(R.id.activeFrom);
        activeTo = findViewById(R.id.activeTo);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        offer_descripton = findViewById(R.id.offer_descripton);


        bucketsRecycler.setLayoutManager(new LinearLayoutManager(this));
        bucketDataModels = new ArrayList<>();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                    if (intent.hasExtra("message")) {
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(SpendXgetXdiscountActivity.this, message);

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


    public void onClickEvents() {
        btnAddMoreBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editBetween.getText().toString())) {
                    editBetween.setError("Enter price");
                    return;
                } else if (TextUtils.isEmpty(editAnd.getText().toString())) {
                    editAnd.setError("Enter price");
                    return;
                } else if (TextUtils.isEmpty(editGiveDiscount.getText().toString())) {
                    editGiveDiscount.setError("Enter discount price");
                    return;
                } else {
                    bucketDataModels.add(new BucketDataModel(editBetween.getText().toString(), editAnd.getText().toString(), editGiveDiscount.getText().toString()));

                }

                adapter = new BucketsAdapter(bucketDataModels, SpendXgetXdiscountActivity.this, SpendXgetXdiscountActivity.this);
                bucketsRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                bucketsRecycler.setVisibility(View.VISIBLE);
                clearBucket();
            }
        });


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.dateSelector1(startDate, SpendXgetXdiscountActivity.this);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate.getText().toString().equalsIgnoreCase("Start Date") || startDate.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(SpendXgetXdiscountActivity.this, "Please select start date first", Toast.LENGTH_SHORT).show();

                else {
                    Constants.endDateSelector(endDate, SpendXgetXdiscountActivity.this, startDate.getText().toString());
                }
            }
        });
        activeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeFrom, SpendXgetXdiscountActivity.this);
            }
        });
        activeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeTo, SpendXgetXdiscountActivity.this);
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(SpendXgetXdiscountActivity.this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(SpendXgetXdiscountActivity.this).create();
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

    @OnClick({R.id.btn_start_date, R.id.btn_end_date, R.id.checkbox_all, R.id.checkbox_mon, R.id.checkbox_tue, R.id.checkbox_wed, R.id.checkbox_thu, R.id.checkbox_fri, R.id.checkbox_sat, R.id.checkbox_sun, R.id.active_from, R.id.active_to, R.id.btn_add_more_bucket, R.id.btn_save, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_date:
                break;
            case R.id.btn_end_date:
                break;
            case R.id.checkbox_all:
                if (checkboxAll.isChecked()) {
                    checkboxMon.setChecked(true);
                    checkboxTue.setChecked(true);
                    checkboxWed.setChecked(true);
                    checkboxThu.setChecked(true);
                    checkboxFri.setChecked(true);
                    checkboxSat.setChecked(true);
                    checkboxSun.setChecked(true);
                } else {
                    checkboxMon.setChecked(false);
                    checkboxTue.setChecked(false);
                    checkboxWed.setChecked(false);
                    checkboxThu.setChecked(false);
                    checkboxFri.setChecked(false);
                    checkboxSat.setChecked(false);
                    checkboxSun.setChecked(false);
                }
                break;
            case R.id.checkbox_mon:
                if (checkboxMon.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[0] = true;
                } else
                    daysBool[0] = false;

                break;

            case R.id.checkbox_tue:
                if (checkboxTue.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[1] = true;
                } else
                    daysBool[1] = false;
                break;

            case R.id.checkbox_wed:
                if (checkboxWed.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[2] = true;
                } else
                    daysBool[2] = false;

                break;
            case R.id.checkbox_thu:
                if (checkboxThu.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[3] = true;
                } else
                    daysBool[3] = false;
                break;
            case R.id.checkbox_fri:
                if (checkboxFri.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[4] = true;
                } else
                    daysBool[4] = false;
                break;
            case R.id.checkbox_sat:

                if (checkboxSat.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[5] = true;
                } else
                    daysBool[5] = false;
                break;
            case R.id.checkbox_sun:
                if (checkboxSun.isChecked()) {
                    checkboxAll.setChecked(false);
                    daysBool[6] = true;
                } else
                    daysBool[6] = false;

                break;

            case R.id.btn_save:
                createOffer();
                break;
            case R.id.btn_cancel:
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
        }
    }

    void clearBucket() {
        editBetween.setText("");
        editAnd.setText("");
        editGiveDiscount.setText("");
    }


    @Override
    public void onDeleteClick(int position, BucketsAdapter.ViewInitializer holder) {
        if (bucketDataModels.size() > 0)
            bucketDataModels.remove(position);
        else
            bucketsRecycler.setVisibility(View.GONE);

    }


    String getCompleteBucket() {
        String s = "";
        if (TextUtils.isEmpty(editBetween.getText().toString())) {
            editBetween.setError("Enter price");
            return null;
        } else if (TextUtils.isEmpty(editAnd.getText().toString())) {
            editAnd.setError("Enter price");
            return null;
        } else if (TextUtils.isEmpty(editGiveDiscount.getText().toString())) {
            editGiveDiscount.setError("Enter discount price");
            return null;
        } else {
            if (adapter.getBucketFormatedData() != null && !adapter.getBucketFormatedData().equalsIgnoreCase(""))
                s = editBetween.getText().toString() + "-" + editAnd.getText().toString() + "-" + editGiveDiscount.getText().toString() + "," + adapter.getBucketFormatedData();
            else
                s = editBetween.getText().toString() + "-" + editAnd.getText().toString() + "-" + editGiveDiscount.getText().toString();
        }

        return s;
    }

    void createOffer() {
        if (TextUtils.isEmpty(editOfferTitle.getText().toString())) {
            editOfferTitle.setError("Enter offer name");
            return;
        } else if (TextUtils.isEmpty(offer_descripton.getText().toString())) {
            offer_descripton.setError("Write description");
            return;
        } else if (startDate.getText().toString().equalsIgnoreCase("Start Date") || startDate.getText().toString().equalsIgnoreCase("")) {
            startDate.setTextColor(getResources().getColor(R.color.bg_login_center));
            return;
        } else if (endDate.getText().toString().equalsIgnoreCase("End Date") || endDate.getText().toString().equalsIgnoreCase("")) {
            endDate.setTextColor(getResources().getColor(R.color.bg_login_center));
            return;
        } else if (getSelectedDays().equals("")) {
            Toast.makeText(this, "Please select days", Toast.LENGTH_SHORT).show();
            return;
        } else if (activeFrom.getText().toString().equalsIgnoreCase("Active From") || activeFrom.getText().toString().equalsIgnoreCase("")) {
            activeFrom.setTextColor(getResources().getColor(R.color.bg_login_center));
            return;
        } else if (activeTo.getText().toString().equalsIgnoreCase("Active To") || activeTo.getText().toString().equalsIgnoreCase("")) {
            activeTo.setTextColor(getResources().getColor(R.color.bg_login_center));
            return;
        } else if (TextUtils.isEmpty(editBetween.getText().toString())) {
            editBetween.setError("Enter price");
            return;
        } else if (TextUtils.isEmpty(editAnd.getText().toString())) {
            editAnd.setError("Enter price");
            return;
        } else if (TextUtils.isEmpty(editGiveDiscount.getText().toString())) {
            editGiveDiscount.setError("Enter discount price");
            return;
        } else if (TextUtils.isEmpty(editTermsCondition.getText().toString())) {
            editTermsCondition.setError("Write terms & conditions");
            return;
        } else {
            createSpendXGetXDiscount();

        }
    }


    void clearFiledData() {
        offer_descripton.setText("");
        editOfferTitle.setText("");
        startDate.setText("");
        endDate.setText("");
        activeFrom.setText("");
        activeTo.setText("");
        editTermsCondition.setText("");
        checkboxAll.setChecked(false);
        checkboxMon.setChecked(false);
        checkboxTue.setChecked(false);
        checkboxWed.setChecked(false);
        checkboxThu.setChecked(false);
        checkboxFri.setChecked(false);
        checkboxSat.setChecked(false);
        checkboxSun.setChecked(false);
        clearBucket();
    }


    public String getSelectedDays() {
        String day = "";
        if (checkboxAll.isChecked()) {
            day = "All Days";
           /* day = days[0];
            for (int i = 1; i < days.length; i++) {
                day = day + "," + days[i];

                Log.e("Day String ", day);
                Log.e("Day concatinating  ", day + "," + days[i]);
            }*/

        } else {
            for (int i = 0; i < daysBool.length; i++) {
                if (daysBool[i]) {
                    if (day.equalsIgnoreCase("")) {
                        day = days[i];
                    } else {
                        day = day + "," + days[i];
                    }
                }

                Log.e("Day String 21 ", day);
                Log.e("Day concatinating 312 ", day + "," + days[i]);
            }

        }
        return day;

    }


    public void createSpendXGetXDiscount() {

        final LoadingDialog dialog = new LoadingDialog(SpendXgetXdiscountActivity.this, "Creating offer...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            SpendXgetXdiscountRequest request = new SpendXgetXdiscountRequest();
            request.setRestaurant_id(Constants.getStoredData(SpendXgetXdiscountActivity.this).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(SpendXgetXdiscountActivity.this).getUser_id());

            request.setOffer_title(editOfferTitle.getText().toString());
            request.setOffer_details(offer_descripton.getText().toString());
            request.setStart_date(startDate.getText().toString());
            request.setEnd_date(endDate.getText().toString());
            request.setDays_available(getSelectedDays());
            request.setStart_time(activeFrom.getText().toString());
            request.setEnd_time(activeTo.getText().toString());
            request.setSpendx(getCompleteBucket());
            request.setAvailable_for(Constants.getStoredData(SpendXgetXdiscountActivity.this).getServe_style());
            request.setTerms_conditions(editTermsCondition.getText().toString());


            ApiInterface apiService = ApiClient.getClient(SpendXgetXdiscountActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.createSpendXGetXDiscount(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                clearFiledData();
                                alertDialog("Your Offer - \n" +
                                        "has been created successfully.");

                            } else
                                Toast.makeText(SpendXgetXdiscountActivity.this, "Incorrect data ", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(SpendXgetXdiscountActivity.this, "Loading failed ", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(SpendXgetXdiscountActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


}
