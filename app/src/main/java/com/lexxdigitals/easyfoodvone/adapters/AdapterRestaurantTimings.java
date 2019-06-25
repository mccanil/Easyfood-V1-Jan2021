package com.lexxdigitals.easyfoodvone.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.models.AllDaysRestaurantTiming;

import java.util.List;

public class AdapterRestaurantTimings extends RecyclerView.Adapter<AdapterRestaurantTimings.MyViewHolder> {
    public Context mContext;
    List<AllDaysRestaurantTiming.Data> allDaysList;
    Activity activity;
    OnAdapterItemClickListener onAdapterItemClickListener;
    TimingAdapter.OnAdapterItemClickListener editTimeClickListener;
    public TimingAdapter timingAdapter;


    public AdapterRestaurantTimings(Context mContext,TimingAdapter.OnAdapterItemClickListener editTimeClickListener, OnAdapterItemClickListener onAdapterItemClickListener, List<AllDaysRestaurantTiming.Data> allDaysList) {
        this.mContext = mContext;
        this.activity = activity;
        this.allDaysList = allDaysList;
        this.onAdapterItemClickListener = onAdapterItemClickListener;
        this.editTimeClickListener =editTimeClickListener;

    }

    public interface OnAdapterItemClickListener {
        void onAddClick(int position, AllDaysRestaurantTiming.Data timings, MyViewHolder holder);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView daysName;
        RecyclerView timingRecycler;
        ImageView addtiming;
        View v;


        public MyViewHolder(View view) {
            super(view);
            daysName = (TextView) view.findViewById(R.id.days);
            timingRecycler = view.findViewById(R.id.timingRecycler);
            addtiming = view.findViewById(R.id.addtiming);
            v = view.findViewById(R.id.v);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_restaurent_timings, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if (allDaysList != null) {
            holder.daysName.setText(allDaysList.get(position).getDay());
            holder.timingRecycler.setLayoutManager(new LinearLayoutManager(mContext));
            timingAdapter = new TimingAdapter(allDaysList.get(position).getData(), mContext,editTimeClickListener);
            holder.timingRecycler.setAdapter(timingAdapter);
            if (allDaysList.get(position).getData().size() >=4)
            {
                holder.addtiming.setVisibility(View.GONE);
                holder.v.setVisibility(View.GONE);
            } else {
                holder.v.setVisibility(View.VISIBLE);
                holder.addtiming.setVisibility(View.VISIBLE);
            }

            holder.addtiming.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAdapterItemClickListener.onAddClick(position, allDaysList.get(position), holder);
                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return allDaysList.size();
    }




    /*public void openTimePicker(final TextView txtTime){
        // Get Current Date
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

*/

}