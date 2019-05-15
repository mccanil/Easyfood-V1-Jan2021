package com.lexxdigital.easyfoodvone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.adapters.MenuProductSelectorAdapter;
import com.lexxdigital.easyfoodvone.adapters.SelectedComboProducts;
import com.lexxdigital.easyfoodvone.models.MenuProducts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComboMenuProductSelectorDialog extends Dialog {
    RecyclerView product_list;
    SelectedComboProducts adapter;
    Context context;
    List<MenuProducts.Data> productList = new ArrayList<>();
    Button btn_ok, btn_cancel;
    OnDialogButtonClickListener onDialogButtonClickListener;


    public interface OnDialogButtonClickListener {
        void onOkClick(String ids, List<MenuProducts.Data> selectedItemList);

        void onCancelClick();
    }


    public ComboMenuProductSelectorDialog(Context context, List<MenuProducts.Data> productList, OnDialogButtonClickListener onDialogButtonClickListener) {
        super(context);
        this.context = context;
        this.productList.clear();
        this.productList = productList;
        this.onDialogButtonClickListener = onDialogButtonClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.popup_menu_category);

        product_list = findViewById(R.id.product_list);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        product_list.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SelectedComboProducts(context, productList);
        product_list.setAdapter(adapter);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onDialogButtonClickListener.onCancelClick();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(context, adapter.getSelectedIds(), Toast.LENGTH_SHORT).show();

                List<MenuProducts.Data> list = adapter.getSelectedItemList();
                onDialogButtonClickListener.onOkClick("",list);
                dismiss();




            }
        });


    }
}
