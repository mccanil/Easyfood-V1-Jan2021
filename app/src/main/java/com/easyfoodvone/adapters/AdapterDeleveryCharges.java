package com.easyfoodvone.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.new_order.models.DeleverySettingResponse;
import com.easyfoodvone.new_order.models.DeliveryPostCodeBean;

import java.util.List;

public class AdapterDeleveryCharges extends RecyclerView.Adapter<AdapterDeleveryCharges.MyViewHolder> {


    public Context mContext;
    MyViewHolder mHolder;
    private int lastPosition = -1, count = 0;
    private Activity mActivity;
    List<DeliveryPostCodeBean.DataBean> deleverySetting;
    OnAdapterItemClickListener onAdapterItemClickListener;


    public interface OnAdapterItemClickListener {
        void onEditClick(int position, DeliveryPostCodeBean.DataBean data, MyViewHolder holder);

        void ondeleteClick(int position, DeliveryPostCodeBean.DataBean data, MyViewHolder holder);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button btnEdit, btnDel;
        public TextView txtPostCode, txtMinOrderVal, txtDeleveryCharge, txtFreeDelivery;


        public MyViewHolder(View view) {
            super(view);
            btnEdit = (Button) view.findViewById(R.id.btn_edit);
            btnDel = (Button) view.findViewById(R.id.btn_del);
            txtPostCode = (TextView) view.findViewById(R.id.post_code);
            txtMinOrderVal = (TextView) view.findViewById(R.id.min_order_val);
            txtDeleveryCharge = (TextView) view.findViewById(R.id.delevery_charge);
            txtFreeDelivery = (TextView) view.findViewById(R.id.free_delevery);

        }
    }

    public AdapterDeleveryCharges(Context mContext, Activity mActivity, OnAdapterItemClickListener onAdapterItemClickListener, List<DeliveryPostCodeBean.DataBean> deleverySetting) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.deleverySetting = deleverySetting;
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_delevery_time, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position) {

        deleverySetting.remove(position);
        notifyItemChanged(position);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        mHolder = holder;
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterItemClickListener.onEditClick(position, deleverySetting.get(position), holder);
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterItemClickListener.ondeleteClick(position, deleverySetting.get(position), holder);
            }
        });
        holder.txtPostCode.setText(deleverySetting.get(position).getPostcode());
        holder.txtMinOrderVal.setText("£" + deleverySetting.get(position).getDelivery_min_value());
        holder.txtDeleveryCharge.setText("£" + deleverySetting.get(position).getShip_cost());
        holder.txtFreeDelivery.setText("£" + deleverySetting.get(position).getFree_delivery_over());

        if (deleverySetting.get(position).getIs_primary() == 1) {
            holder.btnDel.setVisibility(View.GONE);
        } else {
            holder.btnDel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return deleverySetting.size();
    }


    public void alertDialog(String msg, final int position, final DeleverySettingResponse.Data data, final MyViewHolder holder) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_set_delevery_charges, null);
        final AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();

        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        final EditText etMinOrderValue = mDialogView.findViewById(R.id.edtMinOrder);
        final EditText etDeliveryCharge = mDialogView.findViewById(R.id.edtDeliveryCharge);
        final EditText etFreeDelivery = mDialogView.findViewById(R.id.edtFreeDelivery);

        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();


            }
        });
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


}