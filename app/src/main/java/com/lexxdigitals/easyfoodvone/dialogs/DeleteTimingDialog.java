package com.lexxdigitals.easyfoodvone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;

public class DeleteTimingDialog extends Dialog {

    DialogButtonsClickListener dialogButtonsClickListener;
    String id = "";
    int position;

    public interface DialogButtonsClickListener {
        void onDeleteDilogOkClicked(int position, String id);

    }

    Button btn_yes, btn_no;
    TextView txt_message;

    public DeleteTimingDialog(int position, Context context, DialogButtonsClickListener dialogButtonsClickListener, String id) {
        super(context);
        this.dialogButtonsClickListener = dialogButtonsClickListener;
        this.id = id;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_dialog);


        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);


        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogButtonsClickListener.onDeleteDilogOkClicked(position, id);
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txt_message = findViewById(R.id.txt_message);

    }

}
