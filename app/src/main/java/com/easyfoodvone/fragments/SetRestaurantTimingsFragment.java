package com.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterRestaurantTimings;
import com.easyfoodvone.adapters.TimingAdapter;
import com.easyfoodvone.api_handler.ApiClient;
import com.easyfoodvone.api_handler.ApiInterface;
import com.easyfoodvone.dialogs.AddTimingDialog;
import com.easyfoodvone.dialogs.DeleteTimingDialog;
import com.easyfoodvone.dialogs.EditTimingDialog;
import com.easyfoodvone.flat_discount_amount_offer.view.impl.FlatDiscountAmountOfferActivity;
import com.easyfoodvone.menu.CommonRequest;
import com.easyfoodvone.models.AddNewTimingRequest;
import com.easyfoodvone.models.AllDaysRestaurantTiming;
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


public class SetRestaurantTimingsFragment extends Fragment implements AdapterRestaurantTimings.OnAdapterItemClickListener,
        AddTimingDialog.DialogClickListener, TimingAdapter.OnAdapterItemClickListener, EditTimingDialog.EditTimingDialogClickListener, DeleteTimingDialog.DialogButtonsClickListener {

    @BindView(R.id.list_timings)
    RecyclerView listTimings;
    Unbinder unbinder;
    private Context mContext;
    private Activity mActivity;

    private AdapterRestaurantTimings mAdapter;
    DeleteTimingDialog delDialog;
    PrefManager prefManager;


    public SetRestaurantTimingsFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @SuppressLint("ValidFragment")
    public SetRestaurantTimingsFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_restaurant_timings, container, false);
        unbinder = ButterKnife.bind(this, view);
        prefManager = PrefManager.getInstance(getActivity());
        getRestaurantTiming(Constants.getStoredData(getActivity()).getRestaurant_id());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void getRestaurantTiming(String restaurantId) {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(restaurantId);
            request.setUser_id(Constants.getStoredData(getActivity()).getUser_id());
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getRestaurantTiming(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<AllDaysRestaurantTiming>() {
                        @Override
                        public void onSuccess(AllDaysRestaurantTiming data) {

                            dialog.dismiss();
                            if (data.isSuccess()) {
                                listTimings.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

                                mAdapter = new AdapterRestaurantTimings(mContext, SetRestaurantTimingsFragment.this, SetRestaurantTimingsFragment.this, data.getData());
                                listTimings.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
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

    //TODO: Called from AdapterRestaurantTiming....
    @Override
    public void onAddClick(int position, AllDaysRestaurantTiming.Data timings, AdapterRestaurantTimings.MyViewHolder holder) {
        AddTimingDialog dialog = new AddTimingDialog(getActivity(), SetRestaurantTimingsFragment.this, timings);
        dialog.show();
    }


    //TODO: Called from AdapterRestaurantTiming....
    public void addTiming(String isOpen, String day, String openingTime, String deliveryTime, String collectionTiming) {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Adding new timing...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            AddNewTimingRequest request = new AddNewTimingRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setUser_id(Constants.getStoredData(getActivity()).getUser_id());
            request.setCollection_time(collectionTiming);
            request.setDelivery_time(deliveryTime);
            request.setOpen_close(isOpen);
            request.setOpen_close_time(openingTime);
            request.setDay(day);


            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.addNewTiming(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                Toast.makeText(mContext, "Timing added successfully", Toast.LENGTH_SHORT).show();
                                getRestaurantTiming(Constants.getStoredData(getActivity()).getRestaurant_id());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Adding failed ", Toast.LENGTH_LONG).show();
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


    //TODO: Called from AdapterRestaurantTiming....
    @Override
    public void onOkButtonClicked(String isOpen, String day, String openingTime, String deliveryTime, String collectionTime) {
        addTiming(isOpen, day, openingTime, deliveryTime, collectionTime);

    }


    //TODO; Called from edit dialog.........
    @Override
    public void onUpdateButtonClicked(String id, String isOpen, String day, String openingTime, String deliveryTime, String collectionTiming) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Updating timing...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            AddNewTimingRequest request = new AddNewTimingRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setId(id);
            request.setCollection_time(collectionTiming);
            request.setDelivery_time(deliveryTime);
            request.setOpen_close(isOpen);
            request.setOpen_close_time(openingTime);


            String s1 = String.valueOf(day.charAt(0));
            request.setDay(s1.toUpperCase() + day.substring(1, 3));


            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.updateTiming(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                getRestaurantTiming(Constants.getStoredData(getActivity()).getRestaurant_id());
                                Toast.makeText(mContext, "Timing updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
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


    //TODO: Called from TimingAdapter....
    @Override
    public void onEditClick(int position, AllDaysRestaurantTiming.Data.TimingData timings, TimingAdapter.TimingViewHolder holder) {
        EditTimingDialog dialog = new EditTimingDialog(getActivity(), SetRestaurantTimingsFragment.this, timings);
        dialog.show();

    }


    //TODO: Called from TimingAdapter....
    @Override
    public void onDeleteClick(int position, AllDaysRestaurantTiming.Data.TimingData timings, TimingAdapter.TimingViewHolder holder) {
        delDialog = new DeleteTimingDialog(position, getActivity(), SetRestaurantTimingsFragment.this, timings.getId());
        delDialog.setCancelable(false);
        delDialog.show();
    }


    //TODO: Called from delete confrmation dialog
    @Override
    public void onDeleteDilogOkClicked(final int position, String id) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Deleting timing...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setId(id);


            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.deleteTiming(prefManager.getPreference(AUTH_TOKEN,""),request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                delDialog.dismiss();
                                mAdapter.timingAdapter.removeItem(position);
                                Toast.makeText(mContext, "Timing deleted successfully", Toast.LENGTH_SHORT).show();
                                getRestaurantTiming(Constants.getStoredData(getActivity()).getRestaurant_id());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
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


}
