package com.lexxdigitals.easyfoodvone.new_order.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsResponse;

import java.util.List;

public class AdapterOrderDetails extends RecyclerView.Adapter<AdapterOrderDetails.MyViewHolder> {


    public Context mContext;
    List<OrderDetailsResponse.OrderDetails.Cart> cartItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quantity, name, price;

        public MyViewHolder(View view) {
            super(view);
            quantity = view.findViewById(R.id.quantity);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);

        }
    }


    public AdapterOrderDetails(Context context, List<OrderDetailsResponse.OrderDetails.Cart> cartItems) {
        this.cartItems = cartItems;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_details, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position) {

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


}