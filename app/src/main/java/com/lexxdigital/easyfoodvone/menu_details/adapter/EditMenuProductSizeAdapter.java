package com.lexxdigital.easyfoodvone.menu_details.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.menu_details.models.MenuProductDetails;


import java.util.List;

public class EditMenuProductSizeAdapter extends RecyclerView.Adapter<EditMenuProductSizeAdapter.MenuProductSizeViews> {
    Context context;
    MenuProductDetails details;

    public EditMenuProductSizeAdapter(Context context, MenuProductDetails details) {
        this.context = context;
        this.details = details;
    }

    @NonNull
    @Override
    public MenuProductSizeViews onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new MenuProductSizeViews(LayoutInflater.from(context).inflate(R.layout.menu_item_size_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuProductSizeViews holder, final int i) {
        holder.menuName.setText(details.getData().getMenu_product_size().get(i).getSize_name());
        holder.menuName.setTextColor(context.getResources().getColor(R.color.black));
        holder.menuPrice.setText(details.getData().getMenu_product_size().get(i).getSize_price());
        holder.menuPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equalsIgnoreCase("")) {
                    details.getData().getMenu_product_size().get(i).setSize_price("0");
                } else
                    details.getData().getMenu_product_size().get(i).setSize_price(s.toString());
            }
        });
        holder.modifiresList.setLayoutManager(new LinearLayoutManager(context));

        if (details.getData().getMenu_product_size().get(i).getSize_modifiers() != null)
            holder.modifiresList.setAdapter(new SizeModifireAdapter(context, details.getData().getMenu_product_size().get(i).getSize_modifiers()));
    }

    @Override
    public int getItemCount() {
        return details.getData().getMenu_product_size().size();
    }

    public class MenuProductSizeViews extends RecyclerView.ViewHolder {
        TextView menuName;
        EditText menuPrice;
        RecyclerView modifiresList;

        public MenuProductSizeViews(@NonNull View itemView) {
            super(itemView);

            menuName = itemView.findViewById(R.id.sizeName);
            menuPrice = itemView.findViewById(R.id.price);
            modifiresList = itemView.findViewById(R.id.modifiresList);


        }
    }

    //TODO::      Size Modifier Adapter.............

    public class SizeModifireAdapter extends RecyclerView.Adapter<SizeModifireAdapter.SizeModifireViews> {
        Context context;
        List<MenuProductDetails.SizeModifires> size_modifiers;

        public SizeModifireAdapter(Context context, List<MenuProductDetails.SizeModifires> size_modifiers) {
            this.context = context;
            this.size_modifiers = size_modifiers;
        }

        @NonNull
        @Override
        public SizeModifireViews onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new SizeModifireViews(LayoutInflater.from(context).inflate(R.layout.menu_item_size_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SizeModifireViews holder, int position) {
            holder.sizeModifireName.setText(size_modifiers.get(position).getModifier_name());
            holder.sizeModifireName.setTextColor(context.getResources().getColor(R.color.black));
            holder.sizeModifirePrice.setVisibility(View.GONE);
            holder.sizeModifiresProductsList.setLayoutManager(new LinearLayoutManager(context));


            if (size_modifiers.get(position).getSize_modifier_products() != null)
                holder.sizeModifiresProductsList.setAdapter(new SizeModifiresproductAdapter(context, size_modifiers.get(position).getSize_modifier_products()));
        }

        @Override
        public int getItemCount() {
            return size_modifiers.size();
        }

        public class SizeModifireViews extends RecyclerView.ViewHolder {
            TextView sizeModifireName;
            EditText sizeModifirePrice;
            RecyclerView sizeModifiresProductsList;

            public SizeModifireViews(@NonNull View itemView) {
                super(itemView);
                sizeModifireName = itemView.findViewById(R.id.sizeName);
                sizeModifirePrice = itemView.findViewById(R.id.price);
                sizeModifiresProductsList = itemView.findViewById(R.id.modifiresList);
            }
        }
    }


    //TODO::      Size Modifier Product Adapter.............
    public class SizeModifiresproductAdapter extends RecyclerView.Adapter<SizeModifiresproductAdapter.SizeModifireProductViews> {
        Context context;
        List<MenuProductDetails.SizeModifireProducts> size_modifier_products;

        public SizeModifiresproductAdapter(Context context, List<MenuProductDetails.SizeModifireProducts> size_modifier_products) {
            this.context = context;
            this.size_modifier_products = size_modifier_products;
        }

        @NonNull
        @Override
        public SizeModifireProductViews onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new SizeModifireProductViews(LayoutInflater.from(context).inflate(R.layout.size_modifier_product_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final SizeModifireProductViews holder, final int position) {
            holder.modifireName.setText(size_modifier_products.get(position).getProduct_name());
            holder.modifirePrice.setText(size_modifier_products.get(position).getSell_price());
            holder.modifirePrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equalsIgnoreCase("")) {
                        size_modifier_products.get(position).setSell_price("0.0");
                    }else {
                        size_modifier_products.get(position).setSell_price(s.toString());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return size_modifier_products.size();
        }

        public class SizeModifireProductViews extends RecyclerView.ViewHolder {
            TextView modifireName;
            EditText modifirePrice;

            public SizeModifireProductViews(@NonNull View itemView) {
                super(itemView);
                modifireName = itemView.findViewById(R.id.modifireName);
                modifirePrice = itemView.findViewById(R.id.modifirePrice);
            }
        }
    }


}
