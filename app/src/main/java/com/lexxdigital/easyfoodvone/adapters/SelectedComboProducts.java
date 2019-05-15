package com.lexxdigital.easyfoodvone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.interfaces.OnChildChckBoxChecked;
import com.lexxdigital.easyfoodvone.models.MenuProducts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectedComboProducts extends RecyclerView.Adapter<SelectedComboProducts.MainProductsView> implements OnChildChckBoxChecked {
    Context context;
    List<MenuProducts.Data> productsList = new ArrayList<>();
    List<Map<String, String>> productsKeys = new ArrayList<>();
    MainProductsView holder;
    SubProductsAdapter adapter;


    public SelectedComboProducts(Context context, List<MenuProducts.Data> productsList) {
        this.context = context;
        this.productsList.clear();
        this.productsList = productsList;
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
        holder.qty.setText(productsList.get(position).getQuantity());
        holder.amt.setText(productsList.get(position).getPrice());


        holder.product.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.product.setChecked(productsList.get(position).getChecked());
        holder.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                productsList.get(position).setChecked(holder.product.isChecked());

                if (productsList.get(position).getChecked())
                {
                    productsList.get(position).setChecked(true);
                    if (productsList.get(position).getProduct_sizes() != null && productsList.get(position).getProduct_sizes().size() <= 0) {
                        holder.combo_layout.setVisibility(View.VISIBLE);
                    }
                } else {
                    productsList.get(position).setChecked(false);
                    holder.combo_layout.setVisibility(View.GONE);
                }


                //TODO: To notify the child parent are selected and select or deselect child
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


        holder.amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                productsList.get(position).setPrice(s.toString());
            }
        });

        holder.qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                productsList.get(position).setQuantity(s.toString());
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
        LinearLayout combo_layout;
        EditText qty, amt;

        public MainProductsView(@NonNull View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.product);
            subProductList = itemView.findViewById(R.id.subProductList);
            amt = itemView.findViewById(R.id.amt);
            qty = itemView.findViewById(R.id.qty);
            combo_layout = itemView.findViewById(R.id.combo_layout);

        }
    }



    public List<MenuProducts.Data> getSelectedItemList() {
        return productsList;
    }




//TODO: Sub Product Adapter


    public class SubProductsAdapter extends RecyclerView.Adapter<SubProductsAdapter.SubProductsView> {
        Context context;
        List<MenuProducts.Data.SubProducts> subProductsList;
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
            return new SubProductsView(LayoutInflater.from(context).inflate(R.layout.combo_sub_product_list_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final SubProductsView holder, final int i) {
            if (subProductsList != null)
            {

                holder.qty.setText(subProductsList.get(i).getQuantity());
                holder.amt.setText(subProductsList.get(i).getPrice());
                holder.sub_product_name.setOnCheckedChangeListener(null);

                holder.sub_product_name.setChecked(subProductsList.get(i).getChecked());

                if (subProductsList.get(i).getChecked()) {
                    holder.combo_layout.setVisibility(View.VISIBLE);
                }


                holder.qty.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        productsList.get(position).getProduct_sizes().get(i).setQuantity(s.toString());
                    }
                });
                holder.amt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        productsList.get(position).getProduct_sizes().get(i).setPrice(s.toString());
                    }
                });


                holder.sub_product_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        subProductsList.get(i).setChecked(isChecked);

                        if (isChecked) {
                            holder.combo_layout.setVisibility(View.VISIBLE);
                            holder.qty.setText(subProductsList.get(i).getQuantity());
                            holder.amt.setText(subProductsList.get(i).getPrice());
                        }
                        else {
                            holder.combo_layout.setVisibility(View.GONE);
                            productsList.get(position).getProduct_sizes().get(i).setPrice("");
                            productsList.get(position).getProduct_sizes().get(i).setQuantity("");
                        }

                        for (int j = 0; j < subProductsList.size(); j++)
                        {
                            if (subProductsList.get(j).getChecked()) {
                                productsList.get(position).setChecked(true);
                                pholder.product.setChecked(true);
                                break;
                            } else {
                                pholder.product.setChecked(false);
                                productsList.get(position).setChecked(false);
                            }
                        }
                    }
                });

                holder.sub_product_name.setText(subProductsList.get(i).getSize_name());
            }


        }


        @Override
        public int getItemCount() {
            return subProductsList.size();
        }

        public class SubProductsView extends RecyclerView.ViewHolder {
            CheckBox sub_product_name;
            LinearLayout combo_layout;
            EditText qty, amt;

            public SubProductsView(@NonNull View itemView) {
                super(itemView);

                sub_product_name = itemView.findViewById(R.id.sub_product_name);
                combo_layout = itemView.findViewById(R.id.combo_layout);
                amt = itemView.findViewById(R.id.amt);
                qty = itemView.findViewById(R.id.qty);

            }
        }


    }


}