package com.lexxdigital.easyfoodvone.discount_offer_with_percentage.view.impl;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.adapters.SelectedProductsListAdapter;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.dialogs.MenuProductSelectorDialog;
import com.lexxdigital.easyfoodvone.flat_discount_amount_offer.view.impl.FlatDiscountAmountOfferActivity;
import com.lexxdigital.easyfoodvone.menu.CommonRequest;
import com.lexxdigital.easyfoodvone.models.DiscountWithPercentageRequest;
import com.lexxdigital.easyfoodvone.models.MenuProducts;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;

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


public class DiscountOfferWithPercentageActivity extends AppCompatActivity implements MenuProductSelectorDialog.OnDialogButtonClickListener {

    @BindView(R.id.image_top)
    ImageView imageTop;
    @BindView(R.id.edit_offer_title)
    EditText editOfferTitle;
    @BindView(R.id.edit_discount_amount)
    EditText editDiscountAmount;
    @BindView(R.id.imageView9)
    ImageView imageView9;
    @BindView(R.id.edit_how_much_customer)
    EditText editHowMuchCustomer;
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
    MenuProductSelectorDialog dialog;
    List<MenuProducts.Data> menuProducts;
    private static final int PICK_IMAGE_CAMERA = 223;
    private static final int PICK_IMAGE_GALLERY = 623;
    EditText offer_description;
    Bitmap bitmap;
    String imgBase64 = "";
    private File destination = null;
    TextView applicableOn, activeFrom, activeTo, startDate, endDate;
    RecyclerView selcetdItemsList;
    SelectedProductsListAdapter selectedProductsListAdapter;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    String days[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    boolean daysBool[] = {false, false, false, false, false, false, false};
    private String idsList="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_offer_with_percentage);


        applicableOn = findViewById(R.id.applicableOn);
        applicableOn.setText(Constants.getStoredData(DiscountOfferWithPercentageActivity.this).getServe_style());
        activeFrom = findViewById(R.id.activeFrom);
        activeTo = findViewById(R.id.activeTo);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        offer_description = findViewById(R.id.offer_descripton);
        selcetdItemsList = findViewById(R.id.selcetdItemsList);

        selcetdItemsList.setLayoutManager(new LinearLayoutManager(this));


        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED))
                {
                    if (intent.hasExtra("message")){
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(DiscountOfferWithPercentageActivity.this,message);

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
                if (ContextCompat.checkSelfPermission(DiscountOfferWithPercentageActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(DiscountOfferWithPercentageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {

                    selectImage();
                } else
                    ActivityCompat.requestPermissions(DiscountOfferWithPercentageActivity.this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);

            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.dateSelector1(startDate, DiscountOfferWithPercentageActivity.this);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startDate.getText().toString().equalsIgnoreCase("Start Date") || startDate.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(DiscountOfferWithPercentageActivity.this, "Please Select start date first", Toast.LENGTH_SHORT).show();
                }else
                Constants.endDateSelector(endDate, DiscountOfferWithPercentageActivity.this,startDate.getText().toString());
            }
        });
        activeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeFrom, DiscountOfferWithPercentageActivity.this);
            }
        });
        activeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeTo, DiscountOfferWithPercentageActivity.this);
            }
        });

        editDiscountAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (!s.toString().equals(""))
                if (Integer.parseInt(s.toString())>100)
                {
                    editDiscountAmount.setText("");
                    Toast.makeText(DiscountOfferWithPercentageActivity.this, "Discount % could not be greater than 100", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DiscountOfferWithPercentageActivity.this);
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


    @OnClick({R.id.checkbox_all, R.id.checkbox_mon, R.id.checkbox_tue, R.id.checkbox_wed, R.id.checkbox_thu, R.id.checkbox_fri, R.id.checkbox_sat, R.id.checkbox_sun, R.id.btn_save, R.id.btn_cancel})
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
        LayoutInflater factory = LayoutInflater.from(DiscountOfferWithPercentageActivity.this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(DiscountOfferWithPercentageActivity.this).create();
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


    private void getAllMenuProducts() {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(DiscountOfferWithPercentageActivity.this).getRestaurant_id());

            ApiInterface apiService = ApiClient.getClient(DiscountOfferWithPercentageActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getProducts(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<MenuProducts>() {
                        @Override
                        public void onSuccess(MenuProducts data) {
                            Log.e("Menu product ", data.getData().toString());
                            menuProducts = data.getData();
                            dialog = new MenuProductSelectorDialog(DiscountOfferWithPercentageActivity.this, menuProducts,DiscountOfferWithPercentageActivity.this);
                            dialog.setCancelable(false);
                            dialog.show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(DiscountOfferWithPercentageActivity.this, "Products not found", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            Toast.makeText(DiscountOfferWithPercentageActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void createOfferWithPercentage()
    {

        final LoadingDialog dialog = new LoadingDialog(DiscountOfferWithPercentageActivity.this,"Creating offer with percentage.");
        dialog.setCancelable(false);
        dialog.show();


        try {
            DiscountWithPercentageRequest request = new DiscountWithPercentageRequest();
            request.setRestaurant_id(Constants.getStoredData(DiscountOfferWithPercentageActivity.this).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(DiscountOfferWithPercentageActivity.this).getUser_id());
            request.setSpend_amount_to_avail_offer(editHowMuchCustomer.getText().toString());
            request.setDiscount_amount(editDiscountAmount.getText().toString());
            request.setDays_available(getSelectedDays());
            request.setOffer_image(imgBase64);
            request.setOffer_description(offer_description.getText().toString());
            request.setEnd_date(endDate.getText().toString());
            request.setStart_date(startDate.getText().toString());
            request.setStart_time(activeFrom.getText().toString());
            request.setEnd_time(activeTo.getText().toString());
            request.setOffer_title(editOfferTitle.getText().toString());
            request.setAvailable_for(Constants.getStoredData(DiscountOfferWithPercentageActivity.this).getServe_style());
            request.setTerms_conditions(editTermsCondition.getText().toString());
            if (radioOnEntireMenu.isChecked())
            {
                request.setOfferDetail("menuitems");
            }else if (radioSelectedItems.isChecked() && !idsList.equalsIgnoreCase(""))
            {
                request.setOfferDetail(idsList);
            }else {
                Toast.makeText(this, "Please select menu item for offer", Toast.LENGTH_SHORT).show();
                return;
            }




            ApiInterface apiService = ApiClient.getClient(DiscountOfferWithPercentageActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.createOfferWithPercentage(request)
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
                            Toast.makeText(DiscountOfferWithPercentageActivity.this, "Offer Creation failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(DiscountOfferWithPercentageActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void createOffer() {
        if (TextUtils.isEmpty(editOfferTitle.getText().toString())) {
            editOfferTitle.setError("Enter offer name");
            return;
        } else if (TextUtils.isEmpty(offer_description.getText().toString())) {
            offer_description.setError("Write description");
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
        } else/* if (TextUtils.isEmpty(editBetween.getText().toString())) {
            editBetween.setError("Enter price");
            return;
        } else if (TextUtils.isEmpty(editAnd.getText().toString())) {
            editAnd.setError("Enter price");
            return;
        } else if (TextUtils.isEmpty(editGiveDiscount.getText().toString())) {
            editGiveDiscount.setError("Enter discount price");
            return;
        } else*/ if (TextUtils.isEmpty(editTermsCondition.getText().toString())) {
            editTermsCondition.setError("Write terms & conditions");
            return;
        } else {
            createOfferWithPercentage();
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


    @Override
    public void onOkClick(String ids, List<MenuProducts.Data> selectedItemList)
    {

        this.idsList=ids;
        if (ids.equalsIgnoreCase(""))
        {
            radioOnEntireMenu.setChecked(false);
            radioSelectedItems.setChecked(false);
            selcetdItemsList.setVisibility(View.VISIBLE);
            Toast.makeText(this, "You did't select any menu item or category", Toast.LENGTH_SHORT).show();
        }

        if (selectedItemList!=null) {
            selectedProductsListAdapter = new SelectedProductsListAdapter(DiscountOfferWithPercentageActivity.this, selectedItemList);
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

    void clearFiledData() {
        offer_description.setText("");
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
        //radioSelectedItems.setChecked(false);
        imgBase64="";
    }
}
