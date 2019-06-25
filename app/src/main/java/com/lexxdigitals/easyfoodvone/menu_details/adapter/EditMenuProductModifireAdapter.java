package com.lexxdigitals.easyfoodvone.menu_details.adapter;

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

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.menu_details.models.MenuProductDetails;

import java.util.List;

public class EditMenuProductModifireAdapter extends RecyclerView.Adapter<EditMenuProductModifireAdapter.MenuProductModifireViews>
{
    Context context;
    MenuProductDetails details;

    public EditMenuProductModifireAdapter(Context context, MenuProductDetails details) {
        this.context = context;
        this.details = details;
    }

    @NonNull
    @Override
    public MenuProductModifireViews onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new MenuProductModifireViews(LayoutInflater.from(context).inflate(R.layout.modifire_items_layout,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuProductModifireViews holder, int i) {
        holder.modifireName.setText(details.getData().getProduct_modifiers().get(i).getModifier_name());
        holder.modifireName.setTextColor(context.getResources().getColor(R.color.black));

        holder.modifirePrice.setVisibility(View.GONE);
        holder.modifireProductList.setLayoutManager(new LinearLayoutManager(context));
        holder.modifireProductList.setAdapter(new ProductModifierSizeAdapter(context,details.getData().getProduct_modifiers().get(i).getModifier_products()));
    }

    @Override
    public int getItemCount() {
        return details.getData().getProduct_modifiers().size();
    }

    public class MenuProductModifireViews extends RecyclerView.ViewHolder
    {
        TextView modifireName;
        EditText modifirePrice;
        RecyclerView modifireProductList;
        public MenuProductModifireViews(@NonNull View itemView)
        {
            super(itemView);

            modifireName = itemView.findViewById(R.id.modifireName);
            modifirePrice = itemView.findViewById(R.id.modifirePrice);
            modifireProductList = itemView.findViewById(R.id.modifireProductList);


        }
    }

    public class ProductModifierSizeAdapter extends RecyclerView.Adapter<ProductModifierSizeAdapter.ModifierSizeViews>
    {
        Context context;
        List<MenuProductDetails.ModifireProducts> modifier_products;

        public ProductModifierSizeAdapter(Context context, List<MenuProductDetails.ModifireProducts> modifier_products) {
            this.context = context;
            this.modifier_products = modifier_products;
        }

        @NonNull
        @Override
        public ModifierSizeViews onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            return new ModifierSizeViews(LayoutInflater.from(context).inflate(R.layout.size_modifier_product_layout,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ModifierSizeViews holder, final int position) {
            holder.modifireName.setText(modifier_products.get(position).getProduct_name());
            holder.modifirePrice.setText(modifier_products.get(position).getSell_price());
            holder.modifirePrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equalsIgnoreCase(""))
                    {
                        modifier_products.get(position).setSell_price("0.0");
                    }else {
                        modifier_products.get(position).setSell_price(s.toString());
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return modifier_products.size();
        }

        class ModifierSizeViews extends RecyclerView.ViewHolder {
            TextView modifireName;
            EditText modifirePrice;
            public ModifierSizeViews(@NonNull View itemView) {
                super(itemView);
                modifireName = itemView.findViewById(R.id.modifireName);
                modifirePrice = itemView.findViewById(R.id.modifirePrice);
            }
        }

    }


}
