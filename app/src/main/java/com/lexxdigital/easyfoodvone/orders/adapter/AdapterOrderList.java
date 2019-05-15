package com.lexxdigital.easyfoodvone.orders.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.lexxdigital.easyfoodvone.orders.models.OrdersListResponse;
import com.lexxdigital.easyfoodvone.utility.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdapterOrderList extends RecyclerView.Adapter<AdapterOrderList.MyViewHolder> {
    List<OrdersListResponse.Orders> ordersListResponse = new ArrayList<>();
    public Context mContext;
    MyViewHolder mHolder;
    private int lastPosition = -1;
    OnItemClickListener onItemClickListener;
    String order = "new";

    public interface OnItemClickListener {
        void onAcceptClick(OrdersListResponse.Orders orderDetail, int position);

        void onRejectClick(OrdersListResponse.Orders orderDetail, int position);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView layoutRow;
        TextView statusSpinner;
        TextView accept, reject, viewDetail, statusText, orderDate,newOrder;
        //String statusString[] = new String[]{"Pending", "Preparing", "Prepared", "Out For Delivery"};
        TextView customer_name, customer_address, order_number, order_amount, order_type, payment_type;

        public MyViewHolder(View view) {
            super(view);
            layoutRow = (CardView) view.findViewById(R.id.layout_row);
            customer_name = view.findViewById(R.id.customer_name);
            newOrder = view.findViewById(R.id.newOrder);
            customer_address = view.findViewById(R.id.customer_address);
            order_number = view.findViewById(R.id.order_number);
            order_amount = view.findViewById(R.id.order_amount);
            orderDate = view.findViewById(R.id.orderDate);
            // order_type = view.findViewById(R.id.order_type);
            payment_type = view.findViewById(R.id.payment_type);
            statusSpinner = view.findViewById(R.id.status);
            accept = view.findViewById(R.id.accept);
            reject = view.findViewById(R.id.declined);
            viewDetail = view.findViewById(R.id.viewDetail);
            statusText = view.findViewById(R.id.statusText);

//            ArrayAdapter<String> status = new ArrayAdapter<String>(mContext, R.layout.simple_spinner_item, statusString); //selected item will look like a spinner set from XML
//            status.setDropDownViewResource(R.layout.simple_spinner_item);
//            statusSpinner.setAdapter(status);

        }
    }

    public void addAllItems(List<OrdersListResponse.Orders> response, String orders) {
        this.order = orders;
        ordersListResponse.addAll(response);
        notifyDataSetChanged();
    }

    public void addItems(OrdersListResponse.Orders response) {
        ordersListResponse.add(0,response);
        notifyDataSetChanged();
    }

    public void removeAll() {
        ordersListResponse.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ordersListResponse.remove(position);
        notifyItemRemoved(position);
    }

    public AdapterOrderList(Context context, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_orders_list, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        if (ordersListResponse.get(position).getNewOrder())
        {
            holder.newOrder.setVisibility(View.VISIBLE);
        }else {
            holder.newOrder.setVisibility(View.GONE);
        }
        mHolder = holder;
        holder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, NewOrderActivity.class);
                i.putExtra(Constants.ORDER_DETAIL, (Serializable) ordersListResponse.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onAcceptClick(ordersListResponse.get(position), position);
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onRejectClick(ordersListResponse.get(position), position);
            }
        });

        if (!order.equals("new")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.reject.setVisibility(View.INVISIBLE);
        } else {
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.VISIBLE);
        }

        if (order.equals("accepted")) {
            holder.statusSpinner.setVisibility(View.VISIBLE);
            holder.statusText.setVisibility(View.VISIBLE);
            holder.statusSpinner.setText(ordersListResponse.get(position).getOrder_status());
        } else {
            holder.statusSpinner.setVisibility(View.INVISIBLE);
            holder.statusText.setVisibility(View.INVISIBLE);
        }

        String prevOrder = "";
        if (ordersListResponse.get(position).getPrev_order() != null)
            if (ordersListResponse.get(position).getPrev_order().equals("0") || ordersListResponse.get(position).getPrev_order().equals(""))
                prevOrder = "New ";
            else
                prevOrder = "" + ordersListResponse.get(position).getPrev_order();
        else
            prevOrder = "New ";

        holder.customer_address.setText(ordersListResponse.get(position).getCustomer_location());
        holder.customer_name.setText(ordersListResponse.get(position).getCustomer_name() + " (" + prevOrder + ")");
        holder.order_amount.setText(Constants.POUND + ordersListResponse.get(position).getOrder_total());
        holder.order_number.setText((ordersListResponse.get(position).getOrder_num().substring(ordersListResponse.get(position).getOrder_num().length() - 8)).replace("-", ""));
        if (ordersListResponse.get(position).getIs_preorder().equals(""))
            holder.payment_type.setText(ordersListResponse.get(position).getPayment_mode() + "-" + ordersListResponse.get(position).getDelivery_option().substring(0, 3));
        else
            holder.payment_type.setText(ordersListResponse.get(position).getPayment_mode() + "-PRE-" + ordersListResponse.get(position).getDelivery_option().substring(0, 3));
        holder.orderDate.setText(ordersListResponse.get(position).getOrder_date_time());


    }

    @Override
    public int getItemCount() {
        return ordersListResponse.size();
    }

}