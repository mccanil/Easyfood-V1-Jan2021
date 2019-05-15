package com.lexxdigital.easyfoodvone.menu_details.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.helper.ItemTouchHelperAdapter;
import com.lexxdigital.easyfoodvone.helper.ItemTouchHelperViewHolder;
import com.lexxdigital.easyfoodvone.menu.MenuCategoryList;
import com.lexxdigital.easyfoodvone.menu_details.models.MenuCategoryItemsResponse;
import com.lexxdigital.easyfoodvone.menu_details.view.impl.MenuDetailsActivity;
import com.lexxdigital.easyfoodvone.models.UpdateMenuCategoryRequest;
import com.lexxdigital.easyfoodvone.models.UpdateMenuProductRequest;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigital.easyfoodvone.utility.ApplicationContext;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;
import com.lexxdigital.easyfoodvone.utility.PinDialog;

import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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