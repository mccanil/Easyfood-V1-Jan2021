package com.easyfoodvone.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.models.OffersResponse;

import java.util.ArrayList;
import java.util.List;

public class AdapterOfferList extends RecyclerView.Adapter<AdapterOfferList.MyViewHolder> {
    List<OffersResponse.Data.Offers> offersLists = new ArrayList<>();
    OnActionButtonClick onActionButtonClick;


    public AdapterOfferList(List<OffersResponse.Data.Offers> offersLists, OnActionButtonClick onActionButtonClick, Context mContext) {
        this.offersLists = offersLists;
        this.onActionButtonClick = onActionButtonClick;
        this.mContext = mContext;
    }

    public interface OnActionButtonClick {
        void onActionButtonClicked(int whichButton, int position, MyViewHolder myViewHolder, OffersResponse.Data.Offers offersList);
    }


    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offer_name, start_end_date, day_of_week, min_order_amt;
        Button del;

        public MyViewHolder(View view) {
            super(view);
            offer_name = view.findViewById(R.id.offer_name);
            start_end_date = view.findViewById(R.id.start_end_date);
            day_of_week = view.findViewById(R.id.day_of_week);
            min_order_amt = view.findViewById(R.id.min_order_amt);
            del = view.findViewById(R.id.btn_del);

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_offers_list, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position) {
        offersLists.remove(position);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonClick.onActionButtonClicked(2, holder.getAdapterPosition(), holder, offersLists.get(position));
            }
        });


        holder.day_of_week.setText(offersLists.get(position).getDays_available());
      //  holder.min_order_amt.setText("NA");

        holder.min_order_amt.setText(offersLists.get(position).getMin_order_value());
        holder.start_end_date.setText(offersLists.get(position).getStart_date() + "\n" + offersLists.get(position).getEnd_date());
        holder.offer_name.setText(offersLists.get(position).getOffer_title());


    }

    @Override
    public int getItemCount() {
        return offersLists.size();
    }


}