package com.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterOfferList;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.create_combo_meals_offer.view.impl.CreateComboMealsOfferActivity;
import com.easyfoodvone.discount_offer_with_percentage.view.impl.DiscountOfferWithPercentageActivity;
import com.easyfoodvone.flat_discount_amount_offer.view.impl.FlatDiscountAmountOfferActivity;
import com.easyfoodvone.menu.CommonRequest;
import com.easyfoodvone.models.OffersResponse;
import com.easyfoodvone.new_order.models.CommonResponse;
import com.easyfoodvone.orders.view.impl.OrdersActivity;
import com.easyfoodvone.spend_x_get_x_discount.view.impl.SpendXgetXdiscountActivity;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.LoadingDialog;
import com.easyfoodvone.utility.PrefManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;

public class ListOfOffersFragment extends Fragment implements AdapterOfferList.OnActionButtonClick {
    Unbinder unbinder;
    @BindView(R.id.running_offers)
    TextView runningOffers;
    @BindView(R.id.expired_offers)
    TextView expiredOffers;
    @BindView(R.id.add_new_offer)
    TextView addNewOffer;
    @BindView(R.id.discount_percentage)
    TextView discountPercentage;
    @BindView(R.id.discount_amount)
    TextView discountAmount;
    @BindView(R.id.combo_meal)
    TextView comboMeal;
    LinearLayout combo_offer, odp, oda;
    AdapterOfferList mAdapter;

    private Context mContext;
    private Activity mActivity;
    RecyclerView offersList;
    private PrefManager prefManager;

    public ListOfOffersFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ListOfOffersFragment(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_offers, container, false);
        prefManager=PrefManager.getInstance(getActivity());
        combo_offer = view.findViewById(R.id.combo_offer);
        offersList = view.findViewById(R.id.offers_list);
        offersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        oda = view.findViewById(R.id.oda);
        odp = view.findViewById(R.id.odp);
        unbinder = ButterKnife.bind(this, view);
        clickEvents();
        getOffers("", "");
        return view;
    }


    public void clickEvents() {
        runningOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffers("running", "");
            }
        });
        expiredOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffers("expired", "");
            }
        });
        addNewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
        odp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffers("", "discount_percentage");
            }
        });
        oda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffers("", "flat_offer");
            }
        });
        combo_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffers("", "combo_offer");
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void alertDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.popup_offer_list, null);
        final AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
        mDialog.setCancelable(true);
        mDialog.setView(mDialogView);

        mDialogView.findViewById(R.id.offer_spend_x_discount_x).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                Intent i = new Intent(mContext, SpendXgetXdiscountActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((OrdersActivity) getActivity()).startActivity(i);
                mActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        mDialogView.findViewById(R.id.offering_flat_amount_discount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                Intent i = new Intent(mContext, FlatDiscountAmountOfferActivity.class);
                ((OrdersActivity) getActivity()).startActivity(i);
                mActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        mDialogView.findViewById(R.id.discount_offer_with_percentage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                Intent i = new Intent(mContext, DiscountOfferWithPercentageActivity.class);
                ((OrdersActivity) getActivity()).startActivity(i);
                mActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        mDialogView.findViewById(R.id.combo_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                Intent i = new Intent(mContext, CreateComboMealsOfferActivity.class);
                ((OrdersActivity) getActivity()).startActivity(i);
                mActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        mDialog.show();
    }


    @Override
    public void onActionButtonClicked(int whichButton, final int position, AdapterOfferList.MyViewHolder myViewHolder, final OffersResponse.Data.Offers offersList) {
        switch (whichButton) {
            case 1:

                break;
            case 2:
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View mDialogView = factory.inflate(R.layout.conf_deialog, null);
                final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
                mDialog.setView(mDialogView);
                TextView msgText = mDialogView.findViewById(R.id.txt_message);
                msgText.setText("Are you sure \n you want to delete " + offersList.getOffer_title());
                mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic
                        mDialog.dismiss();
                        deleteOffers(offersList.getId(), position);

                    }
                });
                mDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic
                        mDialog.dismiss();
                    }
                });

                mDialog.show();
                break;
        }

    }


    private void getOffers(String filter, String offerType) {
        final LoadingDialog dialog;
        if (filter.equals("running") && offerType.equals(""))
            dialog = new LoadingDialog(getActivity(), "Loading running offers");
        else if (filter.equals("expired") && offerType.equals(""))
            dialog = new LoadingDialog(getActivity(), "Loading expired offers");
        else if (filter.equals("") && offerType.equals("discount_percentage"))
            dialog = new LoadingDialog(getActivity(), "Loading discount with percentage offers");
        else if (filter.equals("") && offerType.equals("flat_offer"))
            dialog = new LoadingDialog(getActivity(), "Loading discount with amount offers");
        else if (filter.equals("") && offerType.equals("combo_offer"))
            dialog = new LoadingDialog(getActivity(), "Loading combo offers");
        else
            dialog = new LoadingDialog(getActivity(), "Loading offers");

        dialog.setCancelable(false);
        dialog.show();
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setFilter(filter);
            request.setOffer_type(offerType);

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOffers(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OffersResponse>() {
                        @Override
                        public void onSuccess(OffersResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess() && data.getData() != null) {

                                runningOffers.setText(data.getData().getTotal_running_offers() != null || data.getData().getTotal_running_offers().equals("") ? data.getData().getTotal_running_offers() + " Running\nOffers" : "0 Running\nOffers");
                                expiredOffers.setText(data.getData().getTotal_expired_offers() != null || data.getData().getTotal_expired_offers().equals("") ? data.getData().getTotal_expired_offers() + " Offers Not\nRunning or Expired" : "0 Offers Not\nRunning or Expired");
                                discountPercentage.setText(data.getData().getTotal_percentage_discount_offers() != null || data.getData().getTotal_percentage_discount_offers().equals("") ? data.getData().getTotal_percentage_discount_offers() : "0");
                                discountAmount.setText(data.getData().getTotal_flat_discount_offers() != null || data.getData().getTotal_flat_discount_offers().equals("") ? data.getData().getTotal_flat_discount_offers() : "0");
                                comboMeal.setText(data.getData().getTotal_combo_discount_offers() != null || data.getData().getTotal_combo_discount_offers().equals("") ? data.getData().getTotal_combo_discount_offers() : "0");

                                mAdapter = new AdapterOfferList(data.getData().getOffers(), ListOfOffersFragment.this, mContext);
                                offersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                                offersList.setAdapter(mAdapter);

                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Products not found", Toast.LENGTH_LONG).show();
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

    private void deleteOffers(String offerId, final int position) {
        try {
            CommonRequest request = new CommonRequest();
            request.setOffer_id(offerId);

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.deleteOffer(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {

                            mAdapter.remove(position);
                            Toast.makeText(getActivity(), "Offer deleted", Toast.LENGTH_LONG).show();
                            getOffers("", "");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Try after sometime", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getOffers("", "");
    }
}
