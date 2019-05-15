package com.lexxdigital.easyfoodvone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.helper.PrintEsayFood;
import com.lexxdigital.easyfoodvone.models.OfferDetailsModel;
import com.lexxdigital.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigital.easyfoodvone.utility.Constants;

import java.util.List;

public class AdapterProductList extends RecyclerView.Adapter<AdapterProductList.MyViewHolder> {


    public Context mContext;
    private OrderDetailsResponse.OrderDetails cart;
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        RecyclerView itmsList;

        public MyViewHolder(View view) {
            super(view);
            itmsList =  view.findViewById(R.id.itmsList);

        }
    }


    public AdapterProductList(Context context, OrderDetailsResponse.OrderDetails cart) {
        this.cart=cart;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_product_list, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position)
    {

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        holder.itmsList.setLayoutManager(new LinearLayoutManager(mContext));
        holder.itmsList.setAdapter(new SubItems(mContext,cart.getCart().get(position).getItems()));
    }

    @Override
    public int getItemCount() {
        return cart.getCart().size();
    }




    class SubItems extends RecyclerView.Adapter<SubItems.MyView> {
        public Context mContext;
        private List<OrderDetailsResponse.OrderDetails.Cart.Items> cart;
        public class MyView extends RecyclerView.ViewHolder
        {
            LinearLayout layoutRow;
            TextView product,price,
                    quantity,discount,amount;
            public MyView(View view) 
            {
                super(view);
                layoutRow =  view.findViewById(R.id.layout_row);
                product =  view.findViewById(R.id.product);
                price =  view.findViewById(R.id.price);
                quantity =  view.findViewById(R.id.quantity);
                discount =  view.findViewById(R.id.discount);
                amount =  view.findViewById(R.id.amount);
            }
        }


        public SubItems(Context context, List<OrderDetailsResponse.OrderDetails.Cart.Items> cart) {
            this.cart=cart;
            this.mContext = context;
        }

        @NonNull
        @Override
        public SubItems.MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_product_list, parent, false);

            return new MyView(itemView);
        }

        public void remove(int position)
        {

        }

        @Override
        public void onBindViewHolder(@NonNull final SubItems.MyView holder, int position)
        {
            holder.product.setText(cart.get(position).getProduct_name());
            holder.quantity.setText(cart.get(position).getProduct_qty());
            holder.price.setText(cart.get(position).getProduct_price());
            holder.amount.setText(Constants.POUND+cart.get(position).getTotal_amount());

        }

        @Override
        public int getItemCount() {
            return cart.size();
        }




    }

}