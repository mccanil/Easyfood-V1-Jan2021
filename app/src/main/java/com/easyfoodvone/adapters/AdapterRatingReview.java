package com.easyfoodvone.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyfoodvone.R;
import com.easyfoodvone.models.RatingResponse;

import java.util.List;

public class AdapterRatingReview extends RecyclerView.Adapter<AdapterRatingReview.MyViewHolder> {

    List<RatingResponse.Data.UserRatingsList> user_review_ratings;
    public Context mContext;
    private Activity mActivity;
    RatingCommentClickListner ratingCommentClickListner;


    public interface RatingCommentClickListner {
        void onReplyClicked(int position, MyViewHolder holder, RatingResponse.Data.UserRatingsList data);

        void onThankyouClicked(int position, MyViewHolder holder, RatingResponse.Data.UserRatingsList data);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView[] foodQuality;
        ImageView[] onTime;
        ImageView[] againFrom;
        ImageView[] recommend;
        ImageView[] averageRating;
        TextView name, postcode_time, date;


        public Button btnThanks, btnReply;
        ImageView s1, s2, s3, s4, s5,
                s11, s21, s31, s41, s51,
                s12, s22, s32, s42, s52,
                s13, s23, s33, s43, s53,
                s14, s24, s34, s44, s54;

        AppCompatRatingBar ratingBarOverall, ratingBarFood, ratingBarOnTime, ratingBarOrderAgain, ratingBarRecom;
        TextView tvOverall;

        public MyViewHolder(View view) {
            super(view);
            btnThanks = (Button) view.findViewById(R.id.btn_send_thanks);
            btnReply = (Button) view.findViewById(R.id.btn_send_reply);

            name = view.findViewById(R.id.name);
            postcode_time = view.findViewById(R.id.postcode_time);
            date = view.findViewById(R.id.date);


            s1 = view.findViewById(R.id.s1);
            s2 = view.findViewById(R.id.s2);
            s3 = view.findViewById(R.id.s3);
            s4 = view.findViewById(R.id.s4);
            s5 = view.findViewById(R.id.s5);

            s11 = view.findViewById(R.id.s11);
            s21 = view.findViewById(R.id.s21);
            s31 = view.findViewById(R.id.s31);
            s41 = view.findViewById(R.id.s41);
            s51 = view.findViewById(R.id.s51);

            s12 = view.findViewById(R.id.s12);
            s22 = view.findViewById(R.id.s22);
            s32 = view.findViewById(R.id.s32);
            s42 = view.findViewById(R.id.s42);
            s52 = view.findViewById(R.id.s52);

            s13 = view.findViewById(R.id.s13);
            s23 = view.findViewById(R.id.s23);
            s33 = view.findViewById(R.id.s33);
            s43 = view.findViewById(R.id.s43);
            s53 = view.findViewById(R.id.s53);

            s14 = view.findViewById(R.id.s14);
            s24 = view.findViewById(R.id.s24);
            s34 = view.findViewById(R.id.s34);
            s44 = view.findViewById(R.id.s44);
            s54 = view.findViewById(R.id.s54);


            foodQuality = new ImageView[]{s11, s21, s31, s41, s51};
            onTime = new ImageView[]{s12, s22, s32, s42, s52};
            againFrom = new ImageView[]{s13, s23, s33, s43, s53};
            recommend = new ImageView[]{s14, s24, s34, s44, s54};
            averageRating = new ImageView[]{s1, s2, s3, s4, s5};


            tvOverall = view.findViewById(R.id.tvOverall);
            ratingBarOverall = view.findViewById(R.id.ratingBarOverall);
            ratingBarFood = view.findViewById(R.id.ratingBarFood);
            ratingBarOnTime = view.findViewById(R.id.ratingBarOnTime);
            ratingBarOrderAgain = view.findViewById(R.id.ratingBarOrderAgain);
            ratingBarRecom = view.findViewById(R.id.ratingBarRecom);

        }
    }


    public AdapterRatingReview(List<RatingResponse.Data.UserRatingsList> user_review_ratings, Context mContext, Activity mActivity, RatingCommentClickListner ratingCommentClickListner) {
        this.user_review_ratings = user_review_ratings;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.ratingCommentClickListner = ratingCommentClickListner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_review_rating, parent, false);

        return new MyViewHolder(itemView);
    }

    public void remove(int position) {

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.btnThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingCommentClickListner.onThankyouClicked(position, holder, user_review_ratings.get(position));
            }
        });
        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ratingCommentClickListner.onReplyClicked(position, holder, user_review_ratings.get(position));

            }
        });


        holder.name.setText(user_review_ratings.get(position).getCustomer_name());
        holder.date.setText(user_review_ratings.get(position).getRating_date());
        holder.postcode_time.setText(user_review_ratings.get(position).getCustomer_post_code() + "|" + user_review_ratings.get(position).getRating_time());


        holder.tvOverall.setText(user_review_ratings.get(position).getOverall_rating());
        holder.ratingBarOverall.setVisibility(View.VISIBLE);
        holder.ratingBarOverall.setNumStars(1);
        holder.ratingBarOverall.setRating(1);

        holder.ratingBarFood.setRating(Float.parseFloat(user_review_ratings.get(position).getFood_quality_rating()));
        holder.ratingBarOnTime.setRating(Float.parseFloat(user_review_ratings.get(position).getDelivery_rating()));
        holder.ratingBarOrderAgain.setRating(Float.parseFloat(user_review_ratings.get(position).getOrder_again_rating()));
        holder.ratingBarRecom.setRating(Float.parseFloat(user_review_ratings.get(position).getRecommendation_rating()));


        setRatings(holder.recommend, (int) Double.parseDouble(user_review_ratings.get(position).getRecommendation_rating()));
        setRatings(holder.foodQuality, (int) Double.parseDouble(user_review_ratings.get(position).getFood_quality_rating()));
        setRatings(holder.againFrom, (int) Double.parseDouble(user_review_ratings.get(position).getOrder_again_rating()));
        setRatings(holder.averageRating, (int) Double.parseDouble(user_review_ratings.get(position).getOverall_rating()));
        setRatings(holder.onTime, (int) Double.parseDouble(user_review_ratings.get(position).getDelivery_rating()));


    }


    private void setRatings(ImageView[] rating, int ratings) {
        if (ratings > 0)
            for (int i = 0; i < ratings; i++) {
                rating[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.active_star));
            }
    }


    @Override
    public int getItemCount() {
        return user_review_ratings.size();
    }


    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final android.app.AlertDialog mDialog = new android.app.AlertDialog.Builder(mActivity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }


}