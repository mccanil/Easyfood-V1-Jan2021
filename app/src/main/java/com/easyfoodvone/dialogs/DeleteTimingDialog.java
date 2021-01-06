package com.easyfoodvone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.easyfoodvone.R;

public class DeleteTimingDialog extends Dialog {

    DialogButtonsClickListener dialogButtonsClickListener;
    String id = "";
    int position;

    public interface DialogButtonsClickListener {
        void onDeleteDilogOkClicked(int position, String id);

    }

    TextView btn_yes, btn_no;
    CardView cvYes, cvNo;

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
        cvYes = findViewById(R.id.cv_yes);
        cvNo = findViewById(R.id.cv_no);


        cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogButtonsClickListener.onDeleteDilogOkClicked(position, id);
            }
        });
        cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txt_message = findViewById(R.id.txt_message);

    }

}
