package com.easyfoodvone.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.models.OrderReportResponse;
import com.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.easyfoodvone.utility.Constants;

import java.util.List;

public class AdapterReportOrderList extends RecyclerView.Adapter<AdapterReportOrderList.MyViewHolder> {

    public Context mContext;
    List<OrderReportResponse.OrdersList> order_list;

    private Activity mActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutRow;
        TextView order_number, postcode, date, totalItems, amount;

        public MyViewHolder(View view) {
            super(view);
            layoutRow = view.findViewById(R.id.layout_row);
            order_number = view.findViewById(R.id.order_number);
            postcode = view.findViewById(R.id.postcode);
            date = view.findViewById(R.id.date);
            totalItems = view.findViewById(R.id.totalItems);
            amount = view.findViewById(R.id.amount);
        }
    }


    public AdapterReportOrderList(Context context, Activity activity, List<OrderReportResponse.OrdersList> order_list) {
        this.mContext = context;
        this.mActivity = activity;
        this.order_list = order_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_report_order_list, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.amount.setText(Constants.POUND + order_list.get(position).getOrder_total());
        holder.order_number.setText(order_list.get(position).getOrder_id().substring(order_list.get(position).getOrder_id().length() - 8, order_list.get(position).getOrder_id().length()));
        holder.date.setText(order_list.get(position).getOrder_date());
        holder.totalItems.setText(order_list.get(position).getTotal_items());
        holder.postcode.setText(order_list.get(position).getCustomer_post_code());

        holder.layoutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, NewOrderActivity.class);
                i.putExtra("order_number", order_list.get(position).getOrder_id());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return order_list.size();
    }


}