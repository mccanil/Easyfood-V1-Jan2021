package com.easyfoodvone.create_combo_meals_offer.view.impl;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.ComboProductAdapter;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.dialogs.ComboMenuProductSelectorDialog;
import com.easyfoodvone.menu.CommonRequest;
import com.easyfoodvone.models.CreateComboOfferRequest;
import com.easyfoodvone.models.MenuProducts;
import com.easyfoodvone.models.OfferDetailsModel;
import com.easyfoodvone.models.TempModel;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class CreateComboMealsOfferActivity extends AppCompatActivity implements ComboMenuProductSelectorDialog.OnDialogButtonClickListener {

    @BindView(R.id.image_top)
    ImageView imageTop;
    @BindView(R.id.edit_offer_title)
    EditText editOfferTitle;
    @BindView(R.id.spinner_main_item)
    Spinner spinnerMainItem;
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

    @BindView(R.id.applicable_on)
    TextView applicableOn;
    @BindView(R.id.edit_terms_condition)
    EditText editTermsCondition;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_select_combo_items)
    Button btnSelectComboItems;
    ComboMenuProductSelectorDialog dialog;
    List<MenuProducts.Data> menuList;
    RecyclerView selected_menu_items_list;
    TextView activeFrom, activeTo, startDate, endDate;
    EditText offer_descripton;
    LinearLayout totalAmtLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int PICK_IMAGE_CAMERA = 223;
    private static final int PICK_IMAGE_GALLERY = 623;
    Bitmap bitmap;
    String imgBase64 = "";
    private File destination = null;

    ComboProductAdapter adapter;
    List<OfferDetailsModel> selectedItemsJson = new ArrayList<>();

    String days[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    boolean daysBool[] = {false, false, false, false, false, false, false};
    private String idsList = "";
    TextView total_combo_amt;

    int selectedMenuPosition = 0;
    double mainItemDiscountPrice = 0;
    double comboTotalPrice = 0;
    boolean isCombo = false;
    String filter = "";
    String mainProductId = "";
    JSONArray mainArray;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_combo_meals_offer);
        prefManager=PrefManager.getInstance(CreateComboMealsOfferActivity.this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        selected_menu_items_list = findViewById(R.id.selected_menu_items_list);
        total_combo_amt = findViewById(R.id.total_combo_amt);
        applicableOn.setText(Constants.getStoredData(CreateComboMealsOfferActivity.this).getServe_style());
        activeFrom = findViewById(R.id.activeFrom);
        activeTo = findViewById(R.id.activeTo);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        offer_descripton = findViewById(R.id.offer_descripton);
        totalAmtLayout = findViewById(R.id.totalAmtLayout);

        selected_menu_items_list.setLayoutManager(new LinearLayoutManager(this));


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                    if (intent.hasExtra("message")) {
                        String message = intent.getStringExtra("message");
                        Constants.showNewOrder(CreateComboMealsOfferActivity.this, message);

                    }

                }
            }
        };


        getAllMenuProducts(false);


        clickListeners();


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_TYPE_ACCEPTED));
    }


    private void clickListeners() {
        spinnerMainItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMenuPosition = position;
                if (position == 0) {
                    mainItemDiscountPrice = 0;
                    filter = "";
                    editDiscountAmount.setText("");
                    mainProductId = "";
                } else {

                    isCombo = false;
                    filter = menuList.get(selectedMenuPosition - 1).getProduct_id();
                    total_combo_amt.setText("0");
                    totalAmtLayout.setVisibility(View.GONE);
                    mainProductId = menuList.get(position - 1).getProduct_id();

                    Log.e("main item name ", menuList.get(position - 1).getProduct_name());
                    Log.e("main item price ", menuList.get(position - 1).getProduct_price());
                    Log.e("Discounted price ", position + "");

                }

                selected_menu_items_list.setVisibility(View.GONE);
                if (adapter != null)
                    adapter.clear();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.dateSelector1(startDate, CreateComboMealsOfferActivity.this);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startDate.getText().toString().equalsIgnoreCase("Start Date") || startDate.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(CreateComboMealsOfferActivity.this, "Please Select start date first", Toast.LENGTH_SHORT).show();
                } else
                    Constants.endDateSelector(endDate, CreateComboMealsOfferActivity.this, startDate.getText().toString());
            }
        });
        activeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeFrom, CreateComboMealsOfferActivity.this);
            }
        });
        activeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectTime(activeTo, CreateComboMealsOfferActivity.this);
            }
        });

        editDiscountAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMenuPosition == 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editDiscountAmount.getWindowToken(), 0);
                    Toast.makeText(CreateComboMealsOfferActivity.this, "Please select menu item first", Toast.LENGTH_LONG).show();
                } else {
                    editDiscountAmount.setEnabled(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editDiscountAmount, 0);
                }
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
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 100) {
                        editDiscountAmount.setText("");
                        Toast.makeText(CreateComboMealsOfferActivity.this, "Discount could not be greater than 100", Toast.LENGTH_SHORT).show();
                    } else {
                        if (selectedMenuPosition > 0) {


                            mainItemDiscountPrice = Double.parseDouble(menuList.get(selectedMenuPosition - 1).getProduct_price()) - ((Double.parseDouble(menuList.get(selectedMenuPosition - 1).getProduct_price()) * Double.parseDouble(editDiscountAmount.getText().toString())) / 100);
                            total_combo_amt.setText(Constants.POUND + (new DecimalFormat("##.##").format(comboTotalPrice + mainItemDiscountPrice)));

                            Log.e("main item name ", menuList.get(selectedMenuPosition - 1).getProduct_name());
                            Log.e("main item price ", menuList.get(selectedMenuPosition - 1).getProduct_price());
                            Log.e("Disconuted price ", selectedMenuPosition - 1 + "");
                        }
                    }
                } else {
                    if (selectedMenuPosition > 0) {
                        mainItemDiscountPrice = Double.parseDouble(menuList.get(selectedMenuPosition - 1).getProduct_price());
                        total_combo_amt.setText(Constants.POUND + (new DecimalFormat("##.##").format(comboTotalPrice + mainItemDiscountPrice)));

                    }
                }

            }
        });
        imageTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CreateComboMealsOfferActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(CreateComboMealsOfferActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {

                    selectImage();
                } else
                    ActivityCompat.requestPermissions(CreateComboMealsOfferActivity.this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);

            }
        });


    }


    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CreateComboMealsOfferActivity.this);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(CreateComboMealsOfferActivity.this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(CreateComboMealsOfferActivity.this).create();
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


    private void getAllMenuProducts(final boolean b) {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(CreateComboMealsOfferActivity.this).getRestaurant_id());
            request.setFilter(filter);

            ApiInterface apiService = ApiClient.getClient(CreateComboMealsOfferActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getProducts(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<MenuProducts>() {
                        @Override
                        public void onSuccess(MenuProducts data) {
                            menuList = data.getData();

                            Log.e("Menu product ", data.getData().toString());
                            if (b) {
                                dialog = new ComboMenuProductSelectorDialog(CreateComboMealsOfferActivity.this, data.getData(), CreateComboMealsOfferActivity.this);
                                dialog.setCancelable(false);
                                dialog.show();
                            } else {
                                if (data.getData().size() > 0) {
                                    String[] menuArray = new String[data.getData().size() + 1];
                                    menuArray[0] = "Select menu item";
                                    for (int i = 0; i < data.getData().size(); i++) {
                                        menuArray[i + 1] = data.getData().get(i).getProduct_name();
                                    }

                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CreateComboMealsOfferActivity.this, android.R.layout.simple_spinner_item, menuArray);
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerMainItem.setAdapter(spinnerArrayAdapter);


                                } else {
                                    String[] menuArray = new String[1];
                                    menuArray[0] = "Select menu item";
                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CreateComboMealsOfferActivity.this, android.R.layout.simple_spinner_item, menuArray);
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerMainItem.setAdapter(spinnerArrayAdapter);
                                }


                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            //Toast.makeText(CreateComboMealsOfferActivity.this, "Products not found", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            Toast.makeText(CreateComboMealsOfferActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_select_combo_items)
    public void onViewClicked() {
        if (selectedMenuPosition > 0)
            getAllMenuProducts(true);
        else
            Toast.makeText(this, "Please select menu item first", Toast.LENGTH_LONG).show();
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


    void createOffer() {
        if (TextUtils.isEmpty(editOfferTitle.getText().toString())) {
            editOfferTitle.setError("Enter offer name");
            return;
        } else if (TextUtils.isEmpty(offer_descripton.getText().toString())) {
            offer_descripton.setError("Write description");
            return;
        } else if (TextUtils.isEmpty(editHowMuchCustomer.getText().toString())) {
            editHowMuchCustomer.setError("Enter amount");
            return;
        } else if (selectedMenuPosition == 0) {
            Toast.makeText(this, "Please select menu item", Toast.LENGTH_SHORT).show();
            ;
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
        } else if (!isCombo) {
            Toast.makeText(this, "Nothing found like combo", Toast.LENGTH_SHORT).show();
            return;
        } else {
            createComboOffer();
        }
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


    @Override
    public void onOkClick(String ids, List<MenuProducts.Data> selectedItemList) {
        mainArray = new JSONArray();
        JSONArray childArray = null;
        JSONObject parentObject;
        JSONObject subChild;


        List<TempModel> tm = new ArrayList<>();


        for (int i = 0; i < selectedItemList.size(); i++) {
            parentObject = new JSONObject();
            childArray = new JSONArray();

            if (selectedItemList.get(i).getChecked()
                    && selectedItemList.get(i).getProduct_sizes() != null
                    && selectedItemList.get(i).getProduct_sizes().size() == 0
                    && !selectedItemList.get(i).getQuantity().equals("")
                    && !selectedItemList.get(i).getPrice().equals("")) {

                tm.add(new TempModel(selectedItemList.get(i).getProduct_id(),
                        selectedItemList.get(i).getProduct_name(),
                        selectedItemList.get(i).getProduct_price(),
                        selectedItemList.get(i).getPrice(),
                        selectedItemList.get(i).getQuantity(),
                        selectedItemList.get(i).getChecked()
                ));


                try {
                    parentObject.put("product_id", selectedItemList.get(i).getProduct_id());
                    parentObject.put("product_name", selectedItemList.get(i).getProduct_name());
                    parentObject.put("product_price", selectedItemList.get(i).getProduct_price());
                    parentObject.put("price", selectedItemList.get(i).getPrice());
                    parentObject.put("quantity", selectedItemList.get(i).getQuantity());
                    parentObject.put("product_sizes", childArray);


                    mainArray.put(parentObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                selectedItemsJson.add(new OfferDetailsModel(
                        selectedItemList.get(i).getProduct_id(),
                        selectedItemList.get(i).getProduct_name(),
                        selectedItemList.get(i).getProduct_price(),
                        selectedItemList.get(i).getPrice(),
                        selectedItemList.get(i).getQuantity(),
                        null
                ));

            } else if (selectedItemList.get(i).getChecked() && selectedItemList.get(i).getProduct_sizes() != null) {
                childArray = new JSONArray();

                List<MenuProducts.Data.SubProducts> items = selectedItemList.get(i).getProduct_sizes();
                List<MenuProducts.Data.SubProducts> t = new ArrayList<>();

                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getChecked() && !items.get(j).getQuantity().equals("") && !items.get(j).getPrice().equals("")) {
                        t.add(items.get(j));
                    }
                }
                if (t.size() > 0) {
                    double price = 0;
                    double qty = 0;
                    List<OfferDetailsModel.SubProducts> subProducts = new ArrayList<>();

                    for (int j = 0; j < t.size(); j++) {
                        subChild = new JSONObject();
                        try {
                            subChild.put("size_id", t.get(j).getSize_id());
                            subChild.put("size_name", t.get(j).getSize_name());
                            subChild.put("sell_price", t.get(j).getSell_price());
                            subChild.put("quantity", t.get(j).getQuantity());
                            subChild.put("price", t.get(j).getPrice());

                            qty = qty + Double.parseDouble(t.get(j).getQuantity());
                            price = price + Double.parseDouble(t.get(j).getPrice());


                            childArray.put(subChild);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        tm.add(new TempModel(t.get(j).getSize_id(),
                                t.get(j).getSize_name(),
                                t.get(j).getSell_price(),
                                t.get(j).getPrice(),
                                t.get(j).getQuantity(),
                                t.get(j).getChecked()
                        ));
                    }


                    try {
                        parentObject.put("product_id", selectedItemList.get(i).getProduct_id());
                        parentObject.put("product_name", selectedItemList.get(i).getProduct_name());
                        parentObject.put("product_price", selectedItemList.get(i).getProduct_price());
                        parentObject.put("price", price + "");
                        parentObject.put("quantity", qty + "");
                        parentObject.put("product_sizes", childArray);


                        mainArray.put(parentObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    selectedItemsJson.add(new OfferDetailsModel(
                            selectedItemList.get(i).getProduct_id(),
                            selectedItemList.get(i).getProduct_name(),
                            selectedItemList.get(i).getProduct_price(),
                            selectedItemList.get(i).getPrice(),
                            selectedItemList.get(i).getQuantity(),
                            subProducts
                    ));


                }


            }
        }

        Log.e("Json format", ">>>>>>>>>>>>>>>>>>>" + mainArray.toString());


        if (tm.size() <= 0) {
            totalAmtLayout.setVisibility(View.GONE);
            comboTotalPrice = 0;
            isCombo = false;
        } else {
            isCombo = true;
            totalAmtLayout.setVisibility(View.VISIBLE);
            //TODO: calculate combo total price:
            for (int i = 0; i < tm.size(); i++) {
                comboTotalPrice = comboTotalPrice + Double.parseDouble(tm.get(i).getPrice());
            }
            total_combo_amt.setText(Constants.POUND + (new DecimalFormat("##.##").format(comboTotalPrice + mainItemDiscountPrice)));
        }


        adapter = new ComboProductAdapter(CreateComboMealsOfferActivity.this, tm);
        selected_menu_items_list.setLayoutManager(new LinearLayoutManager(this));
        selected_menu_items_list.setVisibility(View.VISIBLE);
        totalAmtLayout.setVisibility(View.VISIBLE);
        selected_menu_items_list.setAdapter(adapter);

    }


    @Override
    public void onCancelClick() {

        selected_menu_items_list.setVisibility(View.GONE);
        totalAmtLayout.setVisibility(View.GONE);
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
        imgBase64 = "";

        if (adapter != null)
            adapter.clear();

        selected_menu_items_list.setVisibility(View.GONE);
        totalAmtLayout.setVisibility(View.GONE);
        selectedItemsJson = null;
        editDiscountAmount.setText("");
        spinnerMainItem.setSelection(0);
        editHowMuchCustomer.setText("");
    }

    private void createComboOffer() {

        final LoadingDialog dialog = new LoadingDialog(CreateComboMealsOfferActivity.this, "Creating offer with percentage.");
        dialog.setCancelable(false);
        dialog.show();


        try {
            CreateComboOfferRequest request = new CreateComboOfferRequest();
            request.setRestaurant_id(Constants.getStoredData(CreateComboMealsOfferActivity.this).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(CreateComboMealsOfferActivity.this).getUser_id());
            request.setSpend_amount_to_avail_offer(editHowMuchCustomer.getText().toString());
            request.setDiscount_percentage(editDiscountAmount.getText().toString());
            request.setDays_available(getSelectedDays());
            request.setOffer_image(imgBase64);
            request.setOffer_description(offer_descripton.getText().toString());
            request.setEnd_date(endDate.getText().toString());
            request.setStart_date(startDate.getText().toString());
            request.setStart_time(activeFrom.getText().toString());
            request.setEnd_time(activeTo.getText().toString());
            request.setOffer_title(editOfferTitle.getText().toString());
            request.setAvailable_for(Constants.getStoredData(CreateComboMealsOfferActivity.this).getServe_style());
            request.setTerms_conditions(editTermsCondition.getText().toString());
            request.setCombo_total(total_combo_amt.getText().toString().replace(Constants.POUND, ""));
            request.setMain_product_id(mainProductId);
            request.setOffer_details(mainArray.toString());


            ApiInterface apiService = ApiClient.getClient(CreateComboMealsOfferActivity.this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.createComboOffer(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            if (data.isSuccess()) {
                                alertDialog("Offer - \n" +
                                        "has been created successfully.");
                                clearFiledData();

                            } else {
                                Toast.makeText(CreateComboMealsOfferActivity.this, "Unable to create offer, try again later!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
//
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(CreateComboMealsOfferActivity.this, "Offer Creation failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(CreateComboMealsOfferActivity.this, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
