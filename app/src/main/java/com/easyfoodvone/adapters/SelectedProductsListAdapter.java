package com.easyfoodvone.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.interfaces.OnChildChckBoxChecked;
import com.easyfoodvone.models.MenuProducts;

import java.util.ArrayList;
import java.util.List;

public class SelectedProductsListAdapter extends RecyclerView.Adapter<SelectedProductsListAdapter.MainProductsView> implements OnChildChckBoxChecked {

    Context context;
    List<MenuProducts.Data> productsList;
    List<MenuProducts.Data> selectedProductsList;
    SelectedProductsListAdapter.MainProductsView holder;
    SelectedProductsListAdapter.SubProductsAdapter adapter;


    public SelectedProductsListAdapter(Context context, List<MenuProducts.Data> productsList) {
        this.context = context;
        this.productsList = productsList;
        selectedProductsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SelectedProductsListAdapter.MainProductsView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelectedProductsListAdapter.MainProductsView(LayoutInflater.from(context).inflate(R.layout.selected_menu_product_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectedProductsListAdapter.MainProductsView holder, final int position) {

        this.holder = holder;
        adapter = new SelectedProductsListAdapter.SubProductsAdapter(context, productsList.get(position).getProduct_sizes(), holder, position);

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
        TextView product;

        public MainProductsView(@NonNull View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.product);
            subProductList = itemView.findViewById(R.id.subProductList);

        }
    }


//TODO: Sub Product Adapter


    public class SubProductsAdapter extends RecyclerView.Adapter<SelectedProductsListAdapter.SubProductsAdapter.SubProductsView> {
        Context context;
        List<MenuProducts.Data.SubProducts> subProductsList;
        SelectedProductsListAdapter.MainProductsView pholder;
        int position;


        public SubProductsAdapter(Context context, List<MenuProducts.Data.SubProducts> subProductsList, SelectedProductsListAdapter.MainProductsView holder, int position) {
            this.context = context;
            this.subProductsList = subProductsList;
            pholder = holder;
            this.position = position;

        }

        @NonNull
        @Override
        public SelectedProductsListAdapter.SubProductsAdapter.SubProductsView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new SelectedProductsListAdapter.SubProductsAdapter.SubProductsView(LayoutInflater.from(context).inflate(R.layout.selected_menu_sub_product_list, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final SelectedProductsListAdapter.SubProductsAdapter.SubProductsView holder, final int i) {
            if (subProductsList != null) {

                holder.sub_product_name.setText("- " + subProductsList.get(i).getSize_name());
            }


        }

        @Override
        public int getItemCount() {
            return subProductsList.size();
        }

        public class SubProductsView extends RecyclerView.ViewHolder {
            TextView sub_product_name;

            public SubProductsView(@NonNull View itemView) {
                super(itemView);

                sub_product_name = itemView.findViewById(R.id.sub_product_name);

            }
        }


    }


}
