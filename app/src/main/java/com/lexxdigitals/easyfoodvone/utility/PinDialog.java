package com.lexxdigitals.easyfoodvone.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;

public class PinDialog extends Dialog
{

    EditText firstMpin;
    EditText secondMpin;
    EditText thirdMpin;
    EditText fourthMpin;
    TextView error;
    TextView msg;
    Context context;

    OnClick onClick;

    public interface OnClick{
        void onYesClick();
        void onNoClick();
    }


    public PinDialog(Context context,OnClick onClick)
    {
        super(context);
        this.context = context;
        this.onClick =onClick;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.popup_enter_mpin);


        firstMpin = findViewById(R.id.mpin_first);
        secondMpin = findViewById(R.id.mpin_two);
        thirdMpin = findViewById(R.id.mpin_three);
        fourthMpin = findViewById(R.id.mpin_four);
        error = findViewById(R.id.txt_error_message);
        msg = findViewById(R.id.txt_message);


        msg.setText("Please enter 4 digit pin code \n" +
                "to view revenue Report");
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

            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (firstMpin.getText().toString().trim().equalsIgnoreCase("1") && secondMpin.getText().toString().trim().equalsIgnoreCase("1") && thirdMpin.getText().toString().trim().equalsIgnoreCase("1") && fourthMpin.getText().toString().trim().equalsIgnoreCase("1")) {
                    dismiss();
                    onClick.onYesClick();
                    error.setVisibility(View.GONE);

                } else {
                    error.setVisibility(View.VISIBLE);

                }

            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                dismiss();
                onClick.onNoClick();
            }
        });

    }


}