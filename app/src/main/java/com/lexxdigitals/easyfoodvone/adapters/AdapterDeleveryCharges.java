package com.lexxdigitals.easyfoodvone.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.new_order.models.DeleverySettingResponse;

import java.util.List;

public class AdapterDeleveryCharges extends RecyclerView.Adapter<AdapterDeleveryCharges.MyViewHolder> {


    public Context mContext;
    MyViewHolder mHolder;
    private int lastPosition = -1, count = 0;
    private Activity mActivity;
    List<DeleverySettingResponse.Data> deleverySetting;
    OnAdapterItemClickListener onAdapterItemClickListener;



    public interface OnAdapterItemClickListener {
        void onEditClick(int position, DeleverySettingResponse.Data data, MyViewHolder holder);

        void ondeleteClick(int position, DeleverySettingResponse.Data data, MyViewHolder holder);
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

    public AdapterDeleveryCharges(Context mContext, Activity mActivity, OnAdapterItemClickListener onAdapterItemClickListener, List<DeleverySettingResponse.Data> deleverySetting) {
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
//        locationList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, locationList.size());
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        mHolder = holder;
        // holder.txtPostCode.setText("BH"+ ++count);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterItemClickListener.onEditClick(position, deleverySetting.get(position), holder);
//                alertDialog("Enter the delivery charges for "+holder.txtPostCode.getText().toString().trim()+" postcode within 6 miles of radius for deliveries",position,deleverySetting.get(position),holder);
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onAdapterItemClickListener.ondeleteClick(position, deleverySetting.get(position), holder);
//                alertDialogSingle("You have deleted "+holder.txtPostCode.getText().toString().trim()+" postcode successfully.");
            }
        });
        holder.txtPostCode.setText(deleverySetting.get(position).getPost_code());
        holder.txtMinOrderVal.setText("£" + deleverySetting.get(position).getMin_order_value());
        holder.txtDeleveryCharge.setText("£" + deleverySetting.get(position).getDelivery_charge());
        holder.txtFreeDelivery.setText("£" + deleverySetting.get(position).getFree_delivery());

        //  setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {

        return deleverySetting.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
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


//                updatePostCode(Constants.getStoredData(mContext).getRestaurant_id(), Constants.getStoredData(mContext).getPost_code(),
//                        etMinOrderValue.getText().toString(), etDeliveryCharge.getText().toString(),etFreeDelivery.getText().toString() );
            }
        });
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


}