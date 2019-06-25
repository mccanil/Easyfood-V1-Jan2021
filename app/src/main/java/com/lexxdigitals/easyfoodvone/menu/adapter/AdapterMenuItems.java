package com.lexxdigitals.easyfoodvone.menu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.menu_details.view.impl.MenuDetailsActivity;

import java.util.ArrayList;

public class AdapterMenuItems extends RecyclerView.Adapter<AdapterMenuItems.MyViewHolder> {


    private Context mContext;

    private ArrayList<Integer> imageSmall = new ArrayList<>();
    private ArrayList<String> imageListTitle = new ArrayList<>();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView mTitle;
        public SquareLayout rowItems;
        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mTitle = (TextView) view.findViewById(R.id.txt_title);
            rowItems = (SquareLayout) view.findViewById(R.id.row_items);
        }
    }


    public AdapterMenuItems(Context context,ArrayList<Integer> imageListSmall,ArrayList<String> imageList) {
        mContext = context;
        this.imageSmall = imageListSmall;
        this.imageListTitle = imageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_menu_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Glide.with(mContext)
                .load(imageSmall.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
        holder.mTitle.setText(imageListTitle.get(position));

        holder.rowItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,MenuDetailsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

//        Glide.with(mContext).load(imageSmall.get(position))
//                .thumbnail(0.5f)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return imageSmall.size();
    }

}
