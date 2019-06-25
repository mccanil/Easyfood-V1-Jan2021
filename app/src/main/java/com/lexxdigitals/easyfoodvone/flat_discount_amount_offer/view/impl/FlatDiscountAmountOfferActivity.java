package com.lexxdigitals.easyfoodvone.flat_discount_amount_offer.view.impl;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.adapters.SelectedProductsListAdapter;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.dialogs.MenuProductSelectorDialog;
import com.lexxdigitals.easyfoodvone.menu.CommonRequest;
import com.lexxdigitals.easyfoodvone.models.DiscountWithPercentageRequest;
import com.lexxdigitals.easyfoodvone.models.MenuProducts;
import com.lexxdigitals.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FlatDiscountAmountOfferActivity extends AppCompatActivity implements MenuProductSelectorDialog.OnDialogButtonClickListener {
    private static final int PICK_IMAGE_CAMERA = 223;
    private static final int PICK_IMAGE_GALLERY = 623;
    @BindView(R.id.image_top)
    ImageView imageTop;
    @BindView(R.id.edit_offer_title)
    EditText editOfferTitle;
    @BindView(R.id.edit_discount_amount)
    EditText editDiscountAmount;
    @BindView(R.id.edit_how_much_customer)
    EditText editHowMuchCustomer;
    @BindView(R.id.btn_start_date)
    LinearLayout btnStartDate;
    @BindView(R.id.btn_end_date)
    LinearLayout btnEndDate;
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
    @BindView(R.id.radio_on_entire_menu)
    RadioButton radioOnEntireMenu;
    @BindView(R.id.radio_selected_items)
    RadioButton radioSelectedItems;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.edit_terms_condition)
    EditText editTermsCondition;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    Bitmap bitmap;
    String imgBase64 = "";
    private File destination = null;
    /* @BindView(R.id.edit_between)
     EditText editBetween;
     @BindView(R.id.edit_and)
     EditText editAnd;
     @BindView(R.id.edit_give_discount)
     EditText editGiveDiscount;*/
    BroadcastReceiver mRegistrationBroadcastReceiver;
    EditText offer_descripton;

    TextView applicableOn, activeFrom, activeTo, startDate, endDate;

    String days[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    boolean daysBool[] = {false, false, false, false, false, false, false};
    private String idsList = "";
    RecyclerView selcetdItemsList;
    SelectedProductsListAdapter selectedProductsListAdapter;
    MenuProductSelectorDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_discount_amount_offer);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        applicableOn = findViewById(R.id.applicableOn);
        applicableOn.setText(Constants.getStoredData(FlatDiscountAmountOfferActivity.this).getServe_style());
        activeFrom = findViewById(R.id.activeFrom);
        activeTo = findViewById(R.id.activeTo);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        offer_descripton = findViewById(R.id.offer_descripton);
        selcetdItemsList = findViewById(R.id.selcetdItemsList);
        selcetdItemsList.setLayoutManager(new LinearLayoutManager(this));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_selected_items:
                        getAllMenuProducts();
                        break;

                    case R.id.radio_on_entire_menu:
                        selcetdItemsList.setVisibility(View.GONE);
                        break;

                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                    if (intent.hasExtra("message")) {
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(FlatDiscountAmountOfferActivity.this, message);

                    }

                }
            }
        };


        clickListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_TYPE_ACCEPTED));
    }

    private void clickListeners() {
        imageTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(FlatDiscountAmountOfferActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(FlatDiscountAmountOfferActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {

                    selectImage();
                } else
                    ActivityCompat.requestPermissions(FlatDiscountAmountOfferActivity.this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.dateSelector1(startDate, FlatDiscountAmountOfferActivity.this);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate.getText().toString().equalsIgnoreCase("Start Date") || startDate.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(FlatDiscountAmountOfferActivity.this, "Please select start date first", Toast.LENGTH_SHORT).show();

                else {
                    Constants.endDateSelector(endDate, FlatDiscountAmountOfferActivity.this, startDate.getText().toString());
                }
            }
        });
        activeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeFrom, FlatDiscountAmountOfferActivity.this);
            }
        });
        activeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeTo, FlatDiscountAmountOfferActivity.this);
            }
        });

    }


    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(FlatDiscountAmountOfferActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Drawable d = new BitmapDrawable(getResources(), bitmap);
                imageTop.setImageDrawable(d);
                imgBase64 = convertBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBase64 = convertBitmap(bitmap);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    imageTop.setImageDrawable(d);
                }
            }

        }
    }

    public static String convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    @OnClick({R.id.btn_start_date, R.id.btn_end_date, R.id.checkbox_all, R.id.checkbox_mon, R.id.checkbox_tue, R.id.checkbox_wed, R.id.checkbox_thu, R.id.checkbox_fri, R.id.checkbox_sat, R.id.checkbox_sun, R.id.active_from, R.id.active_to, R.id.btn_save, R.id.btn_cancel})
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(FlatDiscountAmountOfferActivity.this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(FlatDiscountAmountOfferActivity.this).create();
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

    void clearFiledData() {
        offer_descripton.setText("");
        editOfferTitle.setText("");
        startDate.setText("");
        endDate.setText("");
        activeFrom.setText("");
        activeTo.setText("");
        editTermsCondition.setText("");
        applicableOn.setText("");
        editTermsCondition.setText("");
        checkboxAll.setChecked(false);
        checkboxMon.setChecked(false);
        checkboxTue.setChecked(false);
        checkboxWed.setChecked(false);
        checkboxThu.setChecked(false);
        checkboxFri.setChecked(false);
        checkboxSat.setChecked(false);
        checkboxSun.setChecked(false);
        radioOnEntireMenu.setChecked(false);
        selcetdItemsList.setVisibility(View.GONE);
        // radioSelectedItems.setChecked(false);
        imgBase64 = "";
    }


    private void getAllMenuProducts() {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(FlatDiscountAmountOfferActivity.this).getRestaurant_id());

            ApiInterface apiService = ApiClient.getClient(FlatDiscountAmountOfferActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getProducts(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<MenuProducts>() {
                        @Override
                        public void onSuccess(MenuProducts data) {
                            dialog = new MenuProductSelectorDialog(FlatDiscountAmountOfferActivity.this, data.getData(), FlatDiscountAmountOfferActivity.this);
                            dialog.setCancelable(false);
                            dialog.show();
                            Log.e("Menu product ", data.getData().toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(FlatDiscountAmountOfferActivity.this, "Products not found", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            Toast.makeText(FlatDiscountAmountOfferActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void createOffer() {
        if (TextUtils.isEmpty(editOfferTitle.getText().toString())) {
            editOfferTitle.setError("Enter offer name");
            return;
        } else if (TextUtils.isEmpty(offer_descripton.getText().toString())) {
            offer_descripton.setError("Write description");
            return;
        } else if (TextUtils.isEmpty(editDiscountAmount.getText().toString())) {
            editDiscountAmount.setError("Enter discount amount");
            return;
        } else if (TextUtils.isEmpty(editHowMuchCustomer.getText().toString())) {
            editHowMuchCustomer.setError("Enter amount");
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
        } else if (TextUtils.isEmpty(editTermsCondition.getText().toString())) {
            editTermsCondition.setError("Write terms & conditions");
            return;
        } else {
            createOfferWithAmount();

        }
    }

    public String getSelectedDays() {
        String day = "";
        if (checkboxAll.isChecked()) {
            day = days[0];
            for (int i = 1; i < days.length; i++) {
                day = day + "," + days[i];

                Log.e("Day String ", day);
                Log.e("Day concatinating  ", day + "," + days[i]);
            }

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

    private void createOfferWithAmount() {

        final LoadingDialog dialog = new LoadingDialog(FlatDiscountAmountOfferActivity.this, "Creating amount offer with percentage.");
        dialog.setCancelable(false);
        dialog.show();


        try {
            DiscountWithPercentageRequest request = new DiscountWithPercentageRequest();
            request.setRestaurant_id(Constants.getStoredData(FlatDiscountAmountOfferActivity.this).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(FlatDiscountAmountOfferActivity.this).getUser_id());
            request.setSpend_amount_to_avail_offer(editHowMuchCustomer.getText().toString());
            request.setDiscount_amount(editDiscountAmount.getText().toString());
            request.setDays_available(getSelectedDays());
            request.setOffer_image(imgBase64);
            request.setOffer_description(offer_descripton.getText().toString());
            request.setEnd_date(endDate.getText().toString());
            request.setStart_date(startDate.getText().toString());
            request.setStart_time(activeFrom.getText().toString());
            request.setEnd_time(activeTo.getText().toString());
            request.setOffer_title(editOfferTitle.getText().toString());
            request.setAvailable_for(Constants.getStoredData(FlatDiscountAmountOfferActivity.this).getServe_style());
            request.setTerms_conditions(editTermsCondition.getText().toString());
            if (radioOnEntireMenu.isChecked()) {
                request.setOfferDetail("menuitems");
            } else if (radioSelectedItems.isChecked() && !idsList.equalsIgnoreCase("")) {
                request.setOfferDetail(idsList);
            } else {
                alertDialog("Please select menu item or select entire menu for offer");
                Toast.makeText(this, "Please select menu item for or select all menu offer", Toast.LENGTH_SHORT).show();
                return;
            }


            ApiInterface apiService = ApiClient.getClient(FlatDiscountAmountOfferActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.createOfferWithAmt(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            alertDialog("Offer - \n" +
                                    "has been created successfully.");
                            clearFiledData();
//                            Toast.makeText(DiscountOfferWithPercentageActivity.this, "Hey! \n You just created new offer.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(FlatDiscountAmountOfferActivity.this, "Offer Creation failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(FlatDiscountAmountOfferActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onOkClick(String ids, List<MenuProducts.Data> selectedItemList) {

        this.idsList = ids;
        if (ids.equalsIgnoreCase("")) {
            radioOnEntireMenu.setChecked(false);
            radioSelectedItems.setChecked(false);
            selcetdItemsList.setVisibility(View.VISIBLE);
            Toast.makeText(this, "You did't select any menu item or category", Toast.LENGTH_SHORT).show();
        }

        if (selectedItemList != null) {
            selectedProductsListAdapter = new SelectedProductsListAdapter(FlatDiscountAmountOfferActivity.this, selectedItemList);
            selcetdItemsList.setAdapter(selectedProductsListAdapter);
            selcetdItemsList.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCancelClick() {
        radioOnEntireMenu.setChecked(false);
        radioSelectedItems.setChecked(false);
        selcetdItemsList.setVisibility(View.GONE);
    }
}
