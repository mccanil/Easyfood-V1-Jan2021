package com.easyfoodvone.spend_x_get_x_discount;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyfoodvone.R;

import java.util.List;

public class BucketsAdapter  extends RecyclerView.Adapter<BucketsAdapter.ViewInitializer> {
    List<BucketDataModel> bucketDataModels;
    Context context;
    OnAdapterItemClickListener onAdapterItemClickListener;


    public BucketsAdapter(List<BucketDataModel> bucketDataModels, Context context, OnAdapterItemClickListener onAdapterItemClickListener) {
        this.bucketDataModels = bucketDataModels;
        this.context = context;
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }

    public interface OnAdapterItemClickListener {
        void onDeleteClick(int position,ViewInitializer holder);
    }


    @NonNull
    @Override
    public ViewInitializer onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.add_bucket_layout, viewGroup, false);
        ViewInitializer viewInitializer = new ViewInitializer(view);
        return viewInitializer;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewInitializer holder, final int i) {
        holder.and.setText(bucketDataModels.get(i).getAnd());
        holder.between.setText(bucketDataModels.get(i).getBetween());
        holder.discount.setText(bucketDataModels.get(i).getDetDiscount());
        holder.deleteBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterItemClickListener.onDeleteClick(i,holder);
            }
        });

    }


    @Override
    public int getItemCount() {
        return bucketDataModels.size();
    }

    public class ViewInitializer extends RecyclerView.ViewHolder
    {
        TextView between, and, discount;
        ImageView deleteBucket;
        public ViewInitializer(@NonNull View itemView) {
            super(itemView);
            between = itemView.findViewById(R.id.edit_between);
            and = itemView.findViewById(R.id.edit_and);
            discount = itemView.findViewById(R.id.edit_give_discount);
            deleteBucket = itemView.findViewById(R.id.deleteBucket);
        }
    }

    public String getBucketFormatedData()
    {String  bucket = "";
        if (bucketDataModels!=  null &&  bucketDataModels.size()>0)
        {
            String temp=bucketDataModels.get(0).getBetween()+"-"+bucketDataModels.get(0).getAnd()+"-"+bucketDataModels.get(0).getDetDiscount();

            for (int i = 1; i <bucketDataModels.size() ; i++) {
                temp = temp+","+bucketDataModels.get(i).getBetween()+"-"+bucketDataModels.get(i).getAnd()+"-"+bucketDataModels.get(i).getDetDiscount();
            }
            bucket =temp;


        }
        return bucket;
    }
    public void deleteItem(int position)
    {
        bucketDataModels.remove(position);
        notifyDataSetChanged();
    }

}