package com.lexxdigitals.easyfoodvone.new_order.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.NewDetailBean;
import com.lexxdigitals.easyfoodvone.databinding.LayoutCellProductBinding;

import java.util.List;


/*for setting item for the recycler view for   */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewResource> {

    private Activity activity;
    private List<NewDetailBean.OrdersDetailsBean.Cart.MenuBean> menuBeanList;


    public ProductListAdapter(Activity activity, List<NewDetailBean.OrdersDetailsBean.Cart.MenuBean> menuBeanList) {
        this.activity = activity;
        this.menuBeanList = menuBeanList;


    }

    @NonNull
    @Override
    public ProductListAdapter.ViewResource onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cell_product, parent, false);
        return new ProductListAdapter.ViewResource(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductListAdapter.ViewResource holder, int position) {

        holder.binding.name.setText(menuBeanList.get(position).getName());
        holder.binding.price.setText("£" + menuBeanList.get(position).getSubtotal());
        holder.binding.quantity.setText(String.valueOf(menuBeanList.get(position).getQty()));
        holder.binding.each.setText("£" + String.valueOf(menuBeanList.get(position).getPrice()));

        if (menuBeanList.get(position).getOptions() != null) {
            if (menuBeanList.get(position).getOptions().getMealProducts() != null && menuBeanList.get(position).getOptions().getMealProducts().size() > 0) {

            } else if (menuBeanList.get(position).getOptions().getProductModifiers() != null && menuBeanList.get(position).getOptions().getProductModifiers().size() > 0) {

            } else if (menuBeanList.get(position).getOptions().getSize() != null && menuBeanList.get(position).getOptions().getSize().getSizemodifiers().size() > 0) {

            }
        }


    }


    @Override
    public int getItemCount() {

        return menuBeanList == null ? 0 : menuBeanList.size();
    }

    public class ViewResource extends RecyclerView.ViewHolder {
        public LayoutCellProductBinding binding;

        ViewResource(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
