package com.lexxdigitals.easyfoodvone.orders.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.lexxdigitals.easyfoodvone.orders.models.OrdersListResponse;
import com.lexxdigitals.easyfoodvone.utility.ApplicationContext;
import com.lexxdigitals.easyfoodvone.utility.Constants;

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
    ArrayList<String> statusList;
    ArrayList<String> statusList1, statusList2;
    int whichTabActive = -1;

    public interface OnItemClickListener {
        void onAcceptClick(OrdersListResponse.Orders orderDetail, int position);

        void onRejectClick(OrdersListResponse.Orders orderDetail, int position);

        void onSpinnerStatus(String statusName, OrdersListResponse.Orders orderDetail, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener {
        public CardView layoutRow;
        TextView statusSpinner;
        TextView accept, reject, viewDetail, statusText, orderDate, newOrder;
        //String statusString[] = new String[]{"Pending", "Preparing", "Prepared", "Out For Delivery"};
        TextView customer_name, customer_address, order_number, order_amount, order_type, payment_type;
        LinearLayout llAddress;
        Spinner spinner;
        boolean isFirst = true;
        FrameLayout layoutOrderStatus;

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
            llAddress = view.findViewById(R.id.layout_address);
            layoutOrderStatus = view.findViewById(R.id.layout_orderStatus);

//            ArrayAdapter<String> status = new ArrayAdapter<String>(mContext, R.layout.simple_spinner_item, statusString); //selected item will look like a spinner set from XML
//            status.setDropDownViewResource(R.layout.simple_spinner_item);
//            statusSpinner.setAdapter(status);

            spinner = (Spinner) view.findViewById(R.id.spinner);

            // Spinner Drop down elements
        /*    statusList1 = new ArrayList<>();
            statusList = new ArrayList<>();
            statusList.add("Change Status");
            statusList.add("Prepared");
            statusList.add("On the way");
            statusList.add("Delivered");

            statusList1.add("Change Status");
            statusList1.add("preparing");
            statusList1.add("out_for_delivery");
            statusList1.add("delivered");*/


            // ,out_for_delivery,delivered
            accept.setOnClickListener(this);
            reject.setOnClickListener(this);
            viewDetail.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.accept:
                    try {
                        onItemClickListener.onAcceptClick(ordersListResponse.get(getLayoutPosition()), getLayoutPosition());

                    } catch (NullPointerException e) {
                        Log.e("NullPointerException", e.toString());
                    }
                    break;

                case R.id.declined:
                    onItemClickListener.onRejectClick(ordersListResponse.get(getLayoutPosition()), getLayoutPosition());
                    break;
                case R.id.viewDetail:
                    Intent i = new Intent(mContext, NewOrderActivity.class);
                    i.putExtra(Constants.ORDER_DETAIL, (Serializable) ordersListResponse.get(getLayoutPosition()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    break;

            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           /* try {
                ApplicationContext.getInstance().stopNotificationSound();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (position == 0) {
                isFirst = false;
                return;
            }
//            String item = parent.getItemAtPosition(position).toString();
            String item = statusList1.get(position);
            if (item != null) {
                onItemClickListener.onSpinnerStatus(item.toString(), ordersListResponse.get(getLayoutPosition()), position);

            }*/
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void clearData() {
        this.ordersListResponse.clear();
        notifyDataSetChanged();
    }

    public void addAllItems(List<OrdersListResponse.Orders> response, String orders) {
        this.order = orders;
        ordersListResponse.addAll(response);
        notifyDataSetChanged();
    }

    public void addItems(OrdersListResponse.Orders response) {
        ordersListResponse.add(0, response);
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

    public void setWhichTabActive(int whichTabActive) {
        this.whichTabActive = whichTabActive;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int mPosition = position;
        if (whichTabActive != -1 && whichTabActive == 2) {
            holder.layoutOrderStatus.setVisibility(View.VISIBLE);
        } else {
            holder.layoutOrderStatus.setVisibility(View.GONE);
        }
        if (ordersListResponse.get(position).getNewOrder()) {
            holder.newOrder.setVisibility(View.VISIBLE);
        } else {
            holder.newOrder.setVisibility(View.GONE);
        }
        mHolder = holder;

        if (!order.equals("new")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.reject.setVisibility(View.INVISIBLE);
        } else {
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.VISIBLE);
        }

        if (order.equals("accepted")) {


            if (ordersListResponse.get(position).getDelivery_option().equalsIgnoreCase("Collection")) {
                statusList = new ArrayList<>();
                statusList1 = new ArrayList<>();
                statusList.add("Change Status");
                statusList.add("Preparing");
                statusList.add("Collected");

                statusList1.add("Change Status");
                statusList1.add("preparing");
                statusList1.add("delivered");

                if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("Accepted")) {
                    holder.statusSpinner.setText("Accepted");
                } else if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("Preparing")) {
                    holder.statusSpinner.setText("Preparing");
                } else if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("Delivered")) {
                    holder.statusSpinner.setText("Collected");
                }
            } else {

                statusList = new ArrayList<>();
                statusList2 = new ArrayList<>();
                statusList.add("Change Status");
                statusList.add("Preparing");
                statusList.add("On the way");
                statusList.add("Delivered");

                statusList2.add("Change Status");
                statusList2.add("preparing");
                statusList2.add("out_for_delivery");
                statusList2.add("delivered");

                if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("Accepted")) {
                    holder.statusSpinner.setText("Accepted");
                } else if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("Preparing")) {
                    holder.statusSpinner.setText("Preparing");
                } else if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("On the way")) {
                    holder.statusSpinner.setText("On the way");
                } else if (ordersListResponse.get(position).getOrder_status().equalsIgnoreCase("Delivered")) {
                    holder.statusSpinner.setText("Delivered");
                }

            }


            //  preparing,out_for_delivery,delivered

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.order_status_spinner_item, statusList);
            adapter.setDropDownViewResource(R.layout.order_status_spinner_item);
            holder.spinner.setAdapter(adapter);

            holder.statusSpinner.setVisibility(View.VISIBLE);
            holder.statusText.setVisibility(View.VISIBLE);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        ApplicationContext.getInstance().stopNotificationSound();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (i == 0) {
                        return;
                    }
                    String item;
//            String item = parent.getItemAtPosition(position).toString();
                    if (ordersListResponse.get(position).getDelivery_option().equalsIgnoreCase("Collection")) {
                        item = statusList1.get(i);
                    } else {
                        item = statusList2.get(i);
                    }

                    if (item != null) {
                        onItemClickListener.onSpinnerStatus(item.toString(), ordersListResponse.get(holder.getAdapterPosition()), i);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


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

        if (ordersListResponse.get(position).getCustomer_location() != null && ordersListResponse.get(position).getCustomer_location().trim().length() > 0) {

            holder.llAddress.setVisibility(View.VISIBLE);
            holder.customer_address.setText(ordersListResponse.get(position).getCustomer_location());

        } else {
            holder.llAddress.setVisibility(View.GONE);
        }


        holder.customer_name.setText(ordersListResponse.get(position).getCustomer_name() + " (" + prevOrder + ")");
        holder.order_amount.setText(Constants.POUND + ordersListResponse.get(position).getOrder_total());
        holder.order_number.setText((ordersListResponse.get(position).getOrder_num().substring(ordersListResponse.get(position).getOrder_num().length() - 8)).replace("-", ""));

        if (!ordersListResponse.get(position).getDelivery_option().isEmpty() && ordersListResponse.get(position).getDelivery_option().trim().length() > 3 && !ordersListResponse.get(position).getDelivery_option().trim().equals("0")) {

            if (ordersListResponse.get(position).getIs_preorder().equals(""))
                holder.payment_type.setText(ordersListResponse.get(position).getPayment_mode() + "-" + ordersListResponse.get(position).getDelivery_option().substring(0, 3));
            else
                holder.payment_type.setText(ordersListResponse.get(position).getPayment_mode() + "-PRE-" + ordersListResponse.get(position).getDelivery_option().substring(0, 3));

        }
        holder.orderDate.setText(ordersListResponse.get(position).getOrder_date_time());


        /*holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int pos, long id) {
                String item = adapterView.getItemAtPosition(pos).toString();
                if (item != null) {
                    onItemClickListener.onSpinnerStatus(item.toString(), ordersListResponse.get(mPosition), pos);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });*/
    }

    @Override
    public int getItemCount() {

        return ordersListResponse == null ? 0 : ordersListResponse.size();
        // return ordersListResponse.size();
    }

}