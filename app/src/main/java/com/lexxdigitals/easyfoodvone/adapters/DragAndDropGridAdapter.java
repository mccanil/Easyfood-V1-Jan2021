package com.lexxdigitals.easyfoodvone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.helper.ItemTouchHelperAdapter;
import com.lexxdigitals.easyfoodvone.helper.ItemTouchHelperViewHolder;
import com.lexxdigitals.easyfoodvone.menu.MenuCategoryList;
import com.lexxdigitals.easyfoodvone.menu.adapter.SquareLayout;
import com.lexxdigitals.easyfoodvone.menu_details.view.impl.MenuDetailsActivity;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


public class DragAndDropGridAdapter extends RecyclerView.Adapter<DragAndDropGridAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private static Context mContext;
    List<MenuCategoryList.MenuCategories> menuCategories;
    OnItemPositionChanged onItemPositionChanged;

    public interface OnItemPositionChanged {
        void onItemPositionChanged(int currentPosition, int previousPosition, MenuCategoryList.MenuCategories menuCategories);
    }


    public DragAndDropGridAdapter(Context context, OnItemPositionChanged onItemPositionChanged, List<MenuCategoryList.MenuCategories> menuCategories) {
        this.mContext = context;
        this.menuCategories = menuCategories;
        this.onItemPositionChanged = onItemPositionChanged;
    }

    @Override
    public DragAndDropGridAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_items, parent, false);
        DragAndDropGridAdapter.ItemViewHolder itemViewHolder = new DragAndDropGridAdapter.ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        Picasso.get().load(menuCategories.get(position).getMenu_category_image())
                .placeholder(R.drawable.restaurant_icon).error(R.drawable.restaurant_icon).into(holder.thumbnail);

        holder.mTitle.setText(menuCategories.get(position).getMenu_category_name());
        holder.txt_items.setText(menuCategories.get(position).getNumber_of_menu_product() + " items");

        holder.rowItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MenuDetailsActivity.class);
                i.putExtra("menu", (Serializable) menuCategories.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });


    }

    @Override
    public void onItemDismiss(int position) {
        menuCategories.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(menuCategories, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public int getItemCount() {
        return menuCategories.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public ImageView thumbnail;
        public TextView mTitle, txt_items;
        public SquareLayout rowItems;

        public ItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txt_items = (TextView) itemView.findViewById(R.id.txt_items);
            rowItems = (SquareLayout) itemView.findViewById(R.id.row_items);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg_on_select));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }
}
