package com.easyfoodvone.menu_details.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.helper.ItemTouchHelperAdapter;
import com.easyfoodvone.helper.ItemTouchHelperViewHolder;
import com.easyfoodvone.menu.MenuCategoryList;
import com.easyfoodvone.menu_details.models.MenuCategoryItemsResponse;
import com.easyfoodvone.utility.Constants;

import java.util.Collections;
import java.util.List;

public class AdapterItemsList extends RecyclerView.Adapter<AdapterItemsList.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private static Context mContext;
    private List<MenuCategoryItemsResponse.Items> itemList;
    private MenuCategoryList.MenuCategories menuCategories;
    private boolean active = false;
    private boolean isActive = false;
    private OnItemPositionChanged onItemPositionChanged;


    public interface OnItemPositionChanged {
        void onItemPositionChanged(int currentPosition, int previousPosition, MenuCategoryItemsResponse.Items itemList);
        void onActivateDeavtivateProduct(MenuCategoryItemsResponse.Items menuCategories, int position, ItemViewHolder holder, boolean isActive);
        void onEditClicked(int position, ItemViewHolder holder, MenuCategoryItemsResponse.Items items);
    }


    public AdapterItemsList(Context context, Activity activity, OnItemPositionChanged onItemPositionChanged, MenuCategoryList.MenuCategories menuCategories, List<MenuCategoryItemsResponse.Items> itemList, boolean isActive) {
        this.onItemPositionChanged = onItemPositionChanged;
        this.mContext = context;
        this.itemList = itemList;
        this.menuCategories = menuCategories;
        this.isActive = isActive;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_details, parent, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {


        if (isActive) {
            holder.mItemName.setText(itemList.get(position).getMenu_product_name());
            holder.mPrice.setText(Constants.POUND + itemList.get(position).getMenu_product_price());
            if (itemList.get(position).getActive().equals("1")) {
                deactiveItems(holder);
            } else {
                activeItem(holder);
            }
        } else {

            holder.mItemName.setText(itemList.get(position).getMenu_product_name());
            holder.mItemName.setTextColor(mContext.getResources().getColor(R.color.inactive_category_count));
            holder.mPrice.setText(Constants.POUND + itemList.get(position).getMenu_product_price());
            holder.mPrice.setTextColor(mContext.getResources().getColor(R.color.inactive_category_count));
            holder.btnEditInactive.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnActive.setVisibility(View.GONE);
            holder.btnInactive.setVisibility(View.VISIBLE);

            activeItem(holder);

        }


        holder.btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemPositionChanged.onActivateDeavtivateProduct(itemList.get(position), position, holder, false);
            }
        });
        holder.btnInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive)
                    onItemPositionChanged.onActivateDeavtivateProduct(itemList.get(position), position, holder, true);
                else
                    Toast.makeText(mContext, "Please turn on your menu first", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onItemPositionChanged.onEditClicked(position,holder,itemList.get(position));
            }
        });
    }


    private void activeItem(ItemViewHolder holder) {
        holder.btnActive.setVisibility(View.GONE);
        holder.btnInactive.setVisibility(View.VISIBLE);
        holder.mItemName.setTextColor(mContext.getResources().getColor(R.color.inactive));
        holder.mPrice.setTextColor(mContext.getResources().getColor(R.color.inactive));
        holder.btnEditInactive.setVisibility(View.VISIBLE);
        holder.btnEdit.setVisibility(View.GONE);

    }

    private void deactiveItems(ItemViewHolder holder) {
        holder.btnActive.setVisibility(View.VISIBLE);
        holder.btnInactive.setVisibility(View.GONE);
        holder.mItemName.setTextColor(mContext.getResources().getColor(R.color.txt_orderid));
        holder.mPrice.setTextColor(mContext.getResources().getColor(R.color.txt_orderid));
        holder.btnEditInactive.setVisibility(View.GONE);
        holder.btnEdit.setVisibility(View.VISIBLE);
    }


    @Override
    public void onItemDismiss(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(itemList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        onItemPositionChanged.onItemPositionChanged(toPosition, fromPosition, itemList.get(fromPosition));

        return true;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public LinearLayout rowItems, btnActive, btnInactive;
        public TextView mItemName, mPrice;
        public Button btnEdit, btnEditInactive;

        public ItemViewHolder(View itemView) {
            super(itemView);

            rowItems = (LinearLayout) itemView.findViewById(R.id.layout_row);
            btnActive = (LinearLayout) itemView.findViewById(R.id.btn_item_active);
            btnInactive = (LinearLayout) itemView.findViewById(R.id.btn_item_inactive);
            btnEdit = (Button) itemView.findViewById(R.id.btn_edit);
            mItemName = (TextView) itemView.findViewById(R.id.txt_item_name);
            mPrice = (TextView) itemView.findViewById(R.id.txt_price);
            btnEditInactive = (Button) itemView.findViewById(R.id.btn_edit_inactive);
        }

        @Override
        public void onItemSelected() {
            rowItems.setBackground(mContext.getResources().getDrawable(R.drawable.border_seven));
        }

        @Override
        public void onItemClear() {
            // rowItems.setBackgroundColor(Color.TRANSPARENT);
            rowItems.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }








}