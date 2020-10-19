package com.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterRatingReview;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.menu.CommonRequest;
import com.easyfoodvone.models.RatingRequest;
import com.easyfoodvone.models.RatingResponse;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;


public class RatingReviewFragment extends Fragment implements AdapterRatingReview.RatingCommentClickListner {

    @BindView(R.id.rating_list)
    RecyclerView ratingList;
    Unbinder unbinder;
    @BindView(R.id.spinner_star)
    Spinner spinnerStar;
    private Context mContext;
    private Activity mActivity;
    private View view;
    private AdapterRatingReview mAdapter;
    TextView averageRating;
    ImageView s1, s2, s3, s4, s5;
    ImageView[] averageRatings;
    AppCompatRatingBar ratingBarAverage;
    TextView tvAverage;
    PrefManager prefManager;

    @SuppressLint("ValidFragment")
    public RatingReviewFragment(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public RatingReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rating_review, container, false);

        prefManager = PrefManager.getInstance(getActivity());
        averageRating = view.findViewById(R.id.averageRating);
        tvAverage = view.findViewById(R.id.tvAverage);
        ratingBarAverage = view.findViewById(R.id.ratingBarAverage);


        s1 = view.findViewById(R.id.s1);
        s2 = view.findViewById(R.id.s2);
        s3 = view.findViewById(R.id.s3);
        s4 = view.findViewById(R.id.s4);
        s5 = view.findViewById(R.id.s5);

        averageRatings = new ImageView[]{s1, s2, s3, s4, s5};


        unbinder = ButterKnife.bind(this, view);

        spinnerStar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        getRatingReview("");
                        break;
                    case 1:
                        getRatingReview("1");
                        break;
                    case 2:
                        getRatingReview("2");
                        break;
                    case 3:
                        getRatingReview("3");
                        break;
                    case 4:
                        getRatingReview("4");
                        break;
                    case 5:
                        getRatingReview("5");
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getRatingReview(String filter) {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading details...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            RatingRequest request = new RatingRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setFilter(filter);

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getRatings(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<RatingResponse>() {
                        @Override
                        public void onSuccess(RatingResponse data) {
                            dialog.dismiss();

                            setRatings((int) Double.parseDouble(data.getData().getTotal_ratings()));

                            tvAverage.setText(data.getData().getTotal_ratings());
                            ratingBarAverage.setVisibility(View.VISIBLE);
                            ratingBarAverage.setNumStars(1);
                            ratingBarAverage.setRating(1);
                            averageRating.setText("(" + data.getData().getTotal_no_ratings() + ")");
                            mAdapter = new AdapterRatingReview(data.getData().getUser_review_ratings(), mContext, mActivity, RatingReviewFragment.this);
                            ratingList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                            ratingList.setAdapter(mAdapter);
                            Log.e("Ratings data ", data.toString());

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    void setRatings(int rating) {
        if (rating > 0)
            for (int i = 0; i < rating; i++) {
                averageRatings[i].setImageDrawable(getActivity().getResources().getDrawable(R.drawable.active_star));
            }
    }


    private void sendThankYou(String customer_id) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Sending Thank-you...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            CommonRequest request = new CommonRequest();
            request.setUser_id(customer_id);
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());


            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.sendThankyou(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            alertDialog("Your thanks has been sent");
                            Log.e("Ratings data ", data.toString());

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Sending failed!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void alertDialogReply(String msg, final int position, final RatingResponse.Data.UserRatingsList data) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_send_reply, null);
        final android.app.AlertDialog mDialog = new android.app.AlertDialog.Builder(mActivity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        final EditText editText5 = mDialogView.findViewById(R.id.editText5);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                if (TextUtils.isEmpty(editText5.getText().toString())) {
                    editText5.setError("Enter message!");
                    return;
                } else
                    reply(editText5.getText().toString(), data.getCustomer_id());

            }
        });
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void reply(String s, String customer_id) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Replying...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            CommonRequest request = new CommonRequest();
            request.setUser_id(customer_id);
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setMessage(s);


            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.reply(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            alertDialog("Your message has been sent \n" +
                                    "Successfully");
                            Log.e("Ratings data ", data.toString());

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Replying failed!", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onReplyClicked(int position, AdapterRatingReview.MyViewHolder holder, RatingResponse.Data.UserRatingsList data) {
        alertDialogReply("Type your message you want \n" +
                "to send to " + data.getCustomer_name(), position, data);
    }

    @Override
    public void onThankyouClicked(int position, AdapterRatingReview.MyViewHolder holder, RatingResponse.Data.UserRatingsList data) {
        sendThankYou(data.getCustomer_id());
    }


    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

}
