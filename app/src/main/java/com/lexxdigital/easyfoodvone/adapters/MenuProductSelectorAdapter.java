package com.lexxdigital.easyfoodvone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.interfaces.OnChildChckBoxChecked;
import com.lexxdigital.easyfoodvone.models.MenuProducts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuProductSelectorAdapter extends RecyclerView.Adapter<MenuProductSelectorAdapter.MainProductsView> implements OnChildChckBoxChecked {
    Context context;
    List<MenuProducts.Data> productsList = new ArrayList<>();
    List<MenuProducts.Data> selectedProductsList;
    List<Map<String, String>> productsKeys = new ArrayList<>();
    MainProductsView holder;
    SubProductsAdapter adapter;


    public MenuProductSelectorAdapter(Context context, List<MenuProducts.Data> productsList) {
        this.context = context;
        this.productsList.clear();
        this.productsList = productsList;
        selectedProductsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MainProductsView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainProductsView(LayoutInflater.from(context).inflate(R.layout.menu_prodcut_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MainProductsView holder, final int position) {

        this.holder = holder;
        adapter = new SubProductsAdapter(context, productsList.get(position).getProduct_sizes(), holder, position);
        holder.product.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.product.setChecked(productsList.get(position).getChecked());
        holder.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productsList.get(position).setChecked(holder.product.isChecked());
                if (productsList.get(position).getChecked()) {
                    selectedProductsList.add(productsList.get(position));
                } else {
                    selectedProductsList.remove(productsList.get(position));
                }

                if (productsList.get(position).getProduct_sizes() != null && productsList.get(position).getProduct_sizes().size() > 0) {
                    for (int i = 0; i < productsList.get(position).getProduct_sizes().size(); i++) {
                        productsList.get(position).getProduct_sizes().get(i).setChecked(holder.product.isChecked());
                    }

                    adapter = new SubProductsAdapter(context, productsList.get(position).getProduct_sizes(), holder, position);
                    holder.subProductList.setLayoutManager(new LinearLayoutManager(context));
                    holder.subProductList.setAdapter(adapter);

                }

            }
        });


        holder.product.setText(productsList.get(position).getProduct_name());
        holder.subProductList.setLayoutManager(new LinearLayoutManager(context));
        holder.subProductList.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public void onChecked(int position, boolean isChecked) {


    }

    public class MainProductsView extends RecyclerView.ViewHolder {
        RecyclerView subProductList;
        CheckBox product;

        public MainProductsView(@NonNull View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.product);
            subProductList = itemView.findViewById(R.id.subProductList);

        }
    }


    public List<MenuProducts.Data> getSelectedItemList() {
        return selectedProductsList;
    }

    public List<Map<String, String>> getSelectedItemsIds() {
        Map<String, String> keyVal = null;
        productsKeys.clear();

        for (int i = 0; i < productsList.size(); i++) {
            if (productsList.get(i).getChecked()) {

                String ids = "";
                keyVal = new HashMap<>();
                for (int j = 0; j < productsList.get(i).getProduct_sizes().size(); j++) {
                    if (productsList.get(i).getProduct_sizes().get(j).getChecked()) {
                        if (ids.equalsIgnoreCase("")) {
                            ids = productsList.get(i).getProduct_sizes().get(j).getSize_id();
                        } else {
                            ids = ids + "," + productsList.get(i).getProduct_sizes().get(j).getSize_id();
                        }
                    }

                }
                keyVal.put(productsList.get(i).getProduct_id(), ids);
                productsKeys.add(keyVal);
            }

        }

        return productsKeys;
    }


    public void clearAdapter() {
        productsList.clear();
        productsKeys.clear();
        if (adapter!=null){
        adapter.notifyDataSetChanged();
        notifyDataSetChanged();}
    }


//TODO: Sub Product Adapter


    public class SubProductsAdapter extends RecyclerView.Adapter<SubProductsAdapter.SubProductsView> {
        Context context;
        List<MenuProducts.Data.SubProducts> subProductsList;
        OnChildChckBoxChecked onChildChckBoxChecked;
        MainProductsView pholder;
        int position;


        public SubProductsAdapter(Context context, List<MenuProducts.Data.SubProducts> subProductsList, MainProductsView holder, int position) {
            this.context = context;
            this.subProductsList = subProductsList;
            pholder = holder;
            this.position = position;

        }

        @NonNull
        @Override
        public SubProductsView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new SubProductsView(LayoutInflater.from(context).inflate(R.layout.sub_product_list, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final SubProductsView holder, final int i) {
            if (subProductsList != null) {
                holder.sub_product_name.setOnCheckedChangeListener(null);

                holder.sub_product_name.setChecked(subProductsList.get(i).getChecked());


                holder.sub_product_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        subProductsList.get(i).setChecked(isChecked);


                        for (int j = 0; j < subProductsList.size(); j++) {
                            if (subProductsList.get(j).getChecked()) {
                                productsList.get(position).setChecked(true);
                                selectedProductsList.add(productsList.get(position));
                                pholder.product.setChecked(true);
                                break;
                            } else {
                                pholder.product.setChecked(false);
                                selectedProductsList.remove(productsList.remove(position));
                            }
                        }
                        //set your object's last status.

                    }
                });

                holder.sub_product_name.setText(subProductsList.get(i).getSize_name());
            }


        }

        public String getSelectedItemsIds(int position) {
            String ids = "";

            for (int i = 0; i < subProductsList.size(); i++) {

                if (subProductsList.get(position).getChecked()) {
                    if (ids.equalsIgnoreCase("")) {
                        ids = productsList.get(i).getProduct_sizes().get(i).getSize_id();
                    } else {
                        ids = ids + "," + productsList.get(i).getProduct_sizes().get(i).getSize_id();
                    }
                }
            }

            return ids;
        }

        @Override
        public int getItemCount() {
            return subProductsList.size();
        }

        public class SubProductsView extends RecyclerView.ViewHolder {
            CheckBox sub_product_name;

            public SubProductsView(@NonNull View itemView) {
                super(itemView);

                sub_product_name = itemView.findViewById(R.id.sub_product_name);

            }
        }

        public void refreshAdapter(List<MenuProducts.Data.SubProducts> newData) {
            subProductsList.clear();
            subProductsList = newData;
            this.notifyDataSetChanged();
        }

    }


}
