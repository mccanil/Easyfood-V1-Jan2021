/*Created by Omnisttechhub Solution*/
package com.easyfoodvone.charity;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.databinding.LayoutCellTimeBinding;
import com.easyfoodvone.interfaces.RecyclerItemListener;

import java.util.List;


/*for setting item for the recycler view for   */
public class DonationTimeAdapter extends RecyclerView.Adapter<DonationTimeAdapter.ViewResource> {

    private Activity activity;
    private int selectedPos = -1;
    private List<String> donationTimeList;
    private RecyclerItemListener recyclerItemListener;


    public DonationTimeAdapter(Activity activity, List<String> donationTimeList, RecyclerItemListener recyclerItemListener) {
        this.activity = activity;
        this.donationTimeList = donationTimeList;
        this.recyclerItemListener = recyclerItemListener;
    }

    @NonNull
    @Override
    public DonationTimeAdapter.ViewResource onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cell_time, parent, false);
        return new DonationTimeAdapter.ViewResource(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DonationTimeAdapter.ViewResource holder, final int position) {
        if (position == donationTimeList.size() - 1) {
            holder.binding.tvTime.setText("10 hrs+");
        } else {
            holder.binding.tvTime.setText(donationTimeList.get(position) + " hrs");
        }

        setSlection(position, holder.binding.rlTime, holder.binding.tvTime);
        holder.binding.rlTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPos = position;
                if (selectedPos >= 0)
                    notifyDataSetChanged();

                recyclerItemListener.onItemClick(position);
            }
        });

    }


    private void setSlection(final int pos, View view, TextView textView) {
        if (pos == selectedPos) {
            view.setBackground(activity.getResources().getDrawable(R.drawable.wired_orange_rect));
            textView.setTextColor(activity.getResources().getColor(R.color.bg_login_center));
        } else {
            view.setBackground(activity.getResources().getDrawable(R.drawable.wired_grey_rect));
            textView.setTextColor(activity.getResources().getColor(R.color.txt_grey_two));
        }
    }


    @Override
    public int getItemCount() {
        return donationTimeList == null ? 0 : donationTimeList.size();
    }

    public class ViewResource extends RecyclerView.ViewHolder {
        public LayoutCellTimeBinding binding;

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
