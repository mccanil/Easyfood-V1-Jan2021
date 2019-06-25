package com.lexxdigitals.easyfoodvone.new_order.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigitals.easyfoodvone.utility.Constants;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Views>
{
    Context context;
    List<OrderDetailsResponse.OrderDetails.Cart> orderDetail;

    public CartAdapter(Context context, List<OrderDetailsResponse.OrderDetails.Cart> orderDetail) {
        this.context = context;
        this.orderDetail = orderDetail;
    }

    @NonNull
    @Override
    public Views onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new Views(LayoutInflater.from(context).inflate(R.layout.order_detail_item_layout,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Views holder, int position)
    {
        holder.catId.setText(orderDetail.get(position).getCategoryName());
        holder.itemList.setLayoutManager(new LinearLayoutManager(context));
        holder.itemList.setAdapter(new ItemAdapter(context,orderDetail.get(position).getItems()));
    }

    @Override
    public int getItemCount() {
        return orderDetail.size();
    }

    public class Views extends RecyclerView.ViewHolder {
        TextView catId;
        RecyclerView itemList;

        public Views(@NonNull View itemView) {
            super(itemView);

            catId = itemView.findViewById(R.id.catId);
            itemList = itemView.findViewById(R.id.itemList);


        }
    }


    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Views>
    {
        Context context;
        List<OrderDetailsResponse.OrderDetails.Cart.Items> cartItemList;

        public ItemAdapter(Context context, List<OrderDetailsResponse.OrderDetails.Cart.Items> cartItemList) {
            this.context = context;
            this.cartItemList = cartItemList;
        }

        @NonNull
        @Override
        public Views onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            return new Views(LayoutInflater.from(context).inflate(R.layout.row_order_details,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull Views holder, int position)
        {
            holder.name.setText(cartItemList.get(position).getProduct_name());
            holder.each.setText(Constants.POUND+cartItemList.get(position).getProduct_price());
            holder.price.setText(Constants.POUND+cartItemList.get(position).getTotal_amount());
            holder.quantity.setText(cartItemList.get(position).getProduct_qty());

        }

        @Override
        public int getItemCount() {
            return cartItemList.size();
        }

        public class Views extends RecyclerView.ViewHolder
        {
            TextView quantity,name,each,price;

            public Views(View view) {
                super(view);
                quantity = view.findViewById(R.id.quantity);
                name = view.findViewById(R.id.name);
                price = view.findViewById(R.id.price);
                each = view.findViewById(R.id.each);

            }
        }
    }
}
