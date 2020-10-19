
package com.easyfoodvone.charity;

import android.app.Activity;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfoodvone.R;
import com.easyfoodvone.charity.webservice.responsebean.CharityInfoBean;
import com.easyfoodvone.databinding.LayoutCellPreviousCharityBinding;
import com.easyfoodvone.interfaces.PreviousMealListener;
import com.easyfoodvone.utility.Helper;


import java.util.List;


/*for setting item for the recycler view for   */
public class PreviousCharityAdapter extends RecyclerView.Adapter<PreviousCharityAdapter.ViewResource> {

    private Activity activity;
    private PreviousMealListener previousMealListener;
    private List<CharityInfoBean.MealDonatedBean.PreviousMealsBean> previousMealsBeans;


    public PreviousCharityAdapter(Activity activity, PreviousMealListener previousMealListener, List<CharityInfoBean.MealDonatedBean.PreviousMealsBean> previousMealsBeans) {
        this.activity = activity;
        this.previousMealListener = previousMealListener;
        this.previousMealsBeans = previousMealsBeans;
    }

    @NonNull
    @Override
    public PreviousCharityAdapter.ViewResource onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cell_previous_charity, parent, false);
        return new PreviousCharityAdapter.ViewResource(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PreviousCharityAdapter.ViewResource holder, int position) {

        if (Integer.parseInt(previousMealsBeans.get(position).getNo_of_meals()) > 1) {
            holder.binding.tvMeals.setText(previousMealsBeans.get(position).getNo_of_meals() + " Meals");
        } else {
            holder.binding.tvMeals.setText(previousMealsBeans.get(position).getNo_of_meals() + " Meal");
        }

        holder.binding.tvDate.setText(Helper.formatDate(previousMealsBeans.get(position).getCreated_at()));
        if (previousMealsBeans.get(position).getCharity_meal_request() != null && previousMealsBeans.get(position).getCharity_meal_request().size() > 0) {
            if (previousMealsBeans.get(position).getIs_collected() == 1 && previousMealsBeans.get(position).getCharity_meal_request().get(0).getIs_accepted() == 1) {
                holder.binding.rlFoodCollected.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(activity.getResources().getString(R.string.collected));
                holder.binding.tvStatus.setBackground(null);
            } else if (previousMealsBeans.get(position).getIs_collected() == 0 && previousMealsBeans.get(position).getCharity_meal_request().get(0).getIs_accepted() == 1) {
                holder.binding.rlFoodCollected.setVisibility(View.VISIBLE);
                holder.binding.tvStatus.setText(activity.getResources().getString(R.string.accepted));
                holder.binding.tvStatus.setBackground(null);
            } else if (previousMealsBeans.get(position).getIs_collected() == 2 && previousMealsBeans.get(position).getCharity_meal_request().get(0).getIs_accepted() == 1) {
                holder.binding.tvStatus.setText(activity.getResources().getString(R.string.not_collected));
                holder.binding.tvStatus.setBackground(null);
                holder.binding.rlFoodCollected.setVisibility(View.GONE);
            } else if (previousMealsBeans.get(position).getIs_collected() == 3) {
                holder.binding.rlFoodCollected.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(activity.getResources().getString(R.string.cancelled));
                holder.binding.tvStatus.setBackground(null);
            }
        } else {
            if (previousMealsBeans.get(position).getIs_collected() == 0) {
                holder.binding.rlFoodCollected.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(activity.getResources().getString(R.string.cancel));
                holder.binding.tvStatus.setBackground(activity.getResources().getDrawable(R.drawable.wired_orange_rect));
            } else if (previousMealsBeans.get(position).getIs_collected() == 3) {
                holder.binding.rlFoodCollected.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(activity.getResources().getString(R.string.cancelled));
                holder.binding.tvStatus.setBackground(null);
            }
        }


        holder.binding.cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMealListener.onNo(holder.getAdapterPosition());
            }
        });


        holder.binding.cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMealListener.onYes(holder.getAdapterPosition());

            }
        });

        holder.binding.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.tvStatus.getText().toString().trim().equals(activity.getResources().getString(R.string.cancel))) {
                    previousMealListener.onCancel(holder.getAdapterPosition());
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return previousMealsBeans == null ? 0 : previousMealsBeans.size();
    }

    public class ViewResource extends RecyclerView.ViewHolder {
        public LayoutCellPreviousCharityBinding binding;

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
