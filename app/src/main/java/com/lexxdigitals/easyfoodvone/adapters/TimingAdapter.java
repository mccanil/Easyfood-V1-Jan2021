package com.lexxdigitals.easyfoodvone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.models.AllDaysRestaurantTiming;

import java.util.List;

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.TimingViewHolder> {
    List<AllDaysRestaurantTiming.Data.TimingData> timings;
    Context context;
    OnAdapterItemClickListener onAdapterItemClickListener;


    public TimingAdapter(List<AllDaysRestaurantTiming.Data.TimingData> timings, Context context, OnAdapterItemClickListener onAdapterItemClickListener) {
        this.timings = timings;
        this.context = context;
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }

    public void removeItem(int position) {
        timings.remove(position);
        notifyItemChanged(position);
    }

    public interface OnAdapterItemClickListener {
        void onEditClick(int position, AllDaysRestaurantTiming.Data.TimingData timings, TimingViewHolder holder);

        void onDeleteClick(int position, AllDaysRestaurantTiming.Data.TimingData timings, TimingViewHolder holder);
    }

    @NonNull
    @Override
    public TimingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timing_layout, viewGroup, false);

        return new TimingViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final TimingViewHolder timingViewHolder, final int i) {
        timingViewHolder.txtCollectionTime.setText(timings.get(i).getCollection_start_time() + "-" + timings.get(i).getCollection_end_time());
        timingViewHolder.txtOpeningTime.setText(timings.get(i).getOpening_start_time() + "-" + timings.get(i).getOpening_end_time());
        timingViewHolder.txtDeleveryTime.setText(timings.get(i).getDelivery_start_time() + "-" + timings.get(i).getDelivery_end_time());

        timingViewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterItemClickListener.onEditClick(i, timings.get(i), timingViewHolder);
            }
        });
        timingViewHolder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterItemClickListener.onDeleteClick(i, timings.get(i), timingViewHolder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return timings.size();
    }

    public class TimingViewHolder extends RecyclerView.ViewHolder {
        TextView txtOpeningTime, txtCollectionTime, txtDeleveryTime;
        Button btn_edit, btn_del;

        public TimingViewHolder(@NonNull View view) {
            super(view);
            txtOpeningTime = (TextView) view.findViewById(R.id.opening_time);
            txtCollectionTime = (TextView) view.findViewById(R.id.collection_time);
            txtDeleveryTime = (TextView) view.findViewById(R.id.delevery_time);
            btn_del = view.findViewById(R.id.btn_del);
            btn_edit = view.findViewById(R.id.btn_edit);
        }
    }
}
