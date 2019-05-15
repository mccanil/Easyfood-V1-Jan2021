package com.lexxdigital.easyfoodvone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.models.MenuProducts;
import com.lexxdigital.easyfoodvone.models.TempModel;
import com.lexxdigital.easyfoodvone.utility.Constants;

import java.util.List;

public class ComboProductAdapter extends RecyclerView.Adapter<ComboProductAdapter.Views> {

    Context context;
    List<TempModel> data;

    public ComboProductAdapter(Context context, List<TempModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Views onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Views(LayoutInflater.from(context).inflate(R.layout.selected_combo_items_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Views holder, int position) {


        if (!data.get(position).getProduct_price().equals("")
                && !data.get(position).getProduct_name().equals("")
                && !data.get(position).getQuantity().equals("")) {
            holder.name.setText(data.get(position).getProduct_name());
            holder.each.setText(Constants.POUND + data.get(position).getProduct_price() + "");
            holder.qty.setText(data.get(position).getQuantity());
            holder.total.setText(Constants.POUND + Double.parseDouble(data.get(position).getPrice())+ "");

        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear()
    {
        data.clear();
        notifyDataSetChanged();
    }

    class Views extends RecyclerView.ViewHolder {
        TextView name, each, qty, total;

        public Views(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            each = itemView.findViewById(R.id.each);
            qty = itemView.findViewById(R.id.qty);
            total = itemView.findViewById(R.id.total);

        }
    }
}
