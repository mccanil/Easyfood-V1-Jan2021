package com.lexxdigital.easyfoodvone.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.menu.CommonRequest;
import com.lexxdigital.easyfoodvone.models.RatingRequest;
import com.lexxdigital.easyfoodvone.models.RatingResponse;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AdapterRatingReview extends RecyclerView.Adapter<AdapterRatingReview.MyViewHolder> {

    List<RatingResponse.Data.UserRatingsList> user_review_ratings;
    public Context mContext;
    private Activity mActivity;
    RatingCommentClickListner ratingCommentClickListner;


    public interface RatingCommentClickListner{
        void onReplyClicked(int position, MyViewHolder holder,RatingResponse.Data.UserRatingsList data);
        void onThankyouClicked(int position, MyViewHolder holder,RatingResponse.Data.UserRatingsList data);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView[] foodQuality;
        ImageView[] onTime;
        ImageView[] againFrom;
        ImageView[] recommend;
        ImageView[] averageRating;
        TextView name,postcode_time,date;


        public Button btnThanks, btnReply;
        ImageView s1, s2, s3, s4, s5,
                s11, s21, s31, s41, s51,
                s12, s22, s32, s42, s52,
                s13, s23, s33, s43, s53,
                s14, s24, s34, s44, s54;

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


        }
    }


    public AdapterRatingReview(List<RatingResponse.Data.UserRatingsList> user_review_ratings, Context mContext, Activity mActivity,RatingCommentClickListner ratingCommentClickListner) {
        this.user_review_ratings = user_review_ratings;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.ratingCommentClickListner =ratingCommentClickListner;
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

        holder.btnThanks.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ratingCommentClickListner.onThankyouClicked(position,holder,user_review_ratings.get(position));

//
            }
        });
        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                ratingCommentClickListner.onReplyClicked(position,holder,user_review_ratings.get(position));
//
            }
        });


        holder.name.setText(user_review_ratings.get(position).getCustomer_name());
        holder.date.setText(user_review_ratings.get(position).getRating_date());
        holder.postcode_time.setText(user_review_ratings.get(position).getCustomer_post_code()+"|"+user_review_ratings.get(position).getRating_time());

        setRatings(holder.recommend,(int)Double.parseDouble(user_review_ratings.get(position).getRecommendation_rating()));
        setRatings(holder.foodQuality,(int)Double.parseDouble(user_review_ratings.get(position).getFood_quality_rating()));
        setRatings(holder.againFrom,(int)Double.parseDouble(user_review_ratings.get(position).getOrder_again_rating()));
        setRatings(holder.averageRating,(int)Double.parseDouble(user_review_ratings.get(position).getOverall_rating()));
        setRatings(holder.onTime,(int)Double.parseDouble(user_review_ratings.get(position).getDelivery_rating()));




    }






    private void setRatings(ImageView[] rating, int ratings)
    {
        if (ratings>0)
        for (int i = 0; i <ratings ; i++) {
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