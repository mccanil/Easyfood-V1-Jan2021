package com.easyfoodvone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.menu.MenuCategoryList;
import com.easyfoodvone.menu_details.adapter.EditMenuProductModifireAdapter;
import com.easyfoodvone.menu_details.adapter.EditMenuProductSizeAdapter;
import com.easyfoodvone.menu_details.models.MenuProductDetails;


public class EditMenuItemsDialog extends Dialog {
    private MenuProductDetails menuDetail;
    TextView menuCatName;
    EditText itemName;
    TextView price;
    Context context;
    MenuCategoryList.MenuCategories menuCategories;
    Button btn_ok, btn_cancel;
    RecyclerView sizeList, itemModifireList;
    UpdateClickListener updateClickListener;
    TextView txt, txt1;


    public interface UpdateClickListener {
        void onUpdateClicked(MenuProductDetails menuDetail, EditMenuItemsDialog editMenuItemsDialog);
    }


    public EditMenuItemsDialog(Context context, MenuCategoryList.MenuCategories menuCategories, MenuProductDetails menuDetail, UpdateClickListener updateClickListener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.menuCategories = menuCategories;
        this.menuDetail = menuDetail;
        this.context = context;
        this.updateClickListener = updateClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.popup_edit_menu_item);


        menuCatName = findViewById(R.id.menuCatName);
        txt = findViewById(R.id.txt);
        txt1 = findViewById(R.id.txt1);
        sizeList = findViewById(R.id.sizeList);
        sizeList.setLayoutManager(new LinearLayoutManager(context));
        itemModifireList = findViewById(R.id.itemModifireList);
        itemModifireList.setLayoutManager(new LinearLayoutManager(context));

        if (menuDetail.getData().getProduct_modifiers() != null)
            itemModifireList.setAdapter(new EditMenuProductModifireAdapter(context, menuDetail));
        else
            txt1.setVisibility(View.GONE);


        if (menuDetail.getData().getMenu_product_size() != null)
            sizeList.setAdapter(new EditMenuProductSizeAdapter(context, menuDetail));
        else
            txt.setVisibility(View.GONE);

        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);
        menuCatName.setText(menuCategories.getMenu_category_name());
        itemName = findViewById(R.id.itemName);
        itemName.setText(menuDetail.getData().getProduct_name());
        price = findViewById(R.id.price);
        price.setText(menuDetail.getData().getProduct_price());


        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                menuDetail.getData().setProduct_price(s.toString());
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClickListener.onUpdateClicked(menuDetail, EditMenuItemsDialog.this);
            }
        });
    }


}
