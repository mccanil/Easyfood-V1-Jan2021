package com.lexxdigital.easyfoodvone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.models.AllDaysRestaurantTiming;
import com.lexxdigital.easyfoodvone.utility.Constants;

public class EditTimingDialog extends Dialog
{
    TextView fromOpeningTime;
    TextView toOpeningTime;
    TextView fromDeleveryTime;
    TextView toDeleveryTime;
    TextView fromCollectionTime;
    TextView toCollectionTime;
    Context context;
    RadioGroup radioGroup;
    RadioButton openRadio,closeRadio;
    EditTimingDialogClickListener dialogClickListener;
    AllDaysRestaurantTiming.Data.TimingData timings;

    public interface EditTimingDialogClickListener {
        void onUpdateButtonClicked(String id,String isOpen, String day, String s, String s1, String s2);
    }

    String isOpen = "open";

    public EditTimingDialog(Context context, EditTimingDialogClickListener dialogClickListener, AllDaysRestaurantTiming.Data.TimingData timings) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.timings = timings;
        this.dialogClickListener = dialogClickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.popup_set_restaurent_time);


        radioGroup = findViewById(R.id.radioGroup);

        fromOpeningTime = findViewById(R.id.from_opening_time);
        toOpeningTime = findViewById(R.id.to_opening_time);
        fromDeleveryTime = findViewById(R.id.from_delevery_time);
        toDeleveryTime = findViewById(R.id.to_delevery_time);
        fromCollectionTime = findViewById(R.id.from_collection_time);
        toCollectionTime = findViewById(R.id.to_collection_time);
        openRadio = findViewById(R.id.open);
        closeRadio = findViewById(R.id.close);



        if (timings.getStatus().equalsIgnoreCase("open"))
        {
            isOpen = "open";
            openRadio.setChecked(true);
        }else {
            isOpen ="close";
            closeRadio.setChecked(false);
        }

        //TODO: Set fre filled Data:-----

        fromOpeningTime.setText(timings.getOpening_start_time());
        toOpeningTime.setText(timings.getOpening_end_time());
        fromDeleveryTime.setText(timings.getDelivery_start_time());
        toDeleveryTime.setText(timings.getDelivery_end_time());
        fromCollectionTime.setText(timings.getCollection_start_time());
        toCollectionTime.setText(timings.getCollection_end_time());


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.open:
                        isOpen = "open";
                        break;
                    case R.id.close:
                        isOpen = "close";
                        break;
                }
            }
        });

        fromOpeningTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Constants.selectTime(fromOpeningTime, context);
            }
        });
        toOpeningTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Constants.selectTime(toOpeningTime, context);
            }
        });
        fromDeleveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Constants.selectTime(fromDeleveryTime, context);
            }
        });
        toDeleveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Constants.selectTime(toDeleveryTime, context);
            }
        });
        fromCollectionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Constants.selectTime(fromCollectionTime, context);
            }
        });
        toCollectionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Constants.selectTime(toCollectionTime, context);
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fromOpeningTime.getText().toString())) {
                    Toast.makeText(context, "Please enter from time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(toOpeningTime.getText().toString())) {
                    Toast.makeText(context, "Please enter to time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(fromDeleveryTime.getText().toString())) {
                    Toast.makeText(context, "Please enter delivery time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(toDeleveryTime.getText().toString())) {
                    Toast.makeText(context, "Please enter to delivery time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(fromCollectionTime.getText().toString())) {
                    Toast.makeText(context, "Please enter from collection time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(toCollectionTime.getText().toString())) {
                    Toast.makeText(context, "Please enter to collection time", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    dialogClickListener.onUpdateButtonClicked(timings.getId(), isOpen, timings.getDay(),
                            fromOpeningTime.getText().toString() + "-" + toOpeningTime.getText().toString(),
                            fromDeleveryTime.getText().toString() + "-" + toDeleveryTime.getText().toString(),
                            fromCollectionTime.getText().toString() + "-" + toCollectionTime.getText().toString()
                    );
                    dismiss();
                }


            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}