package com.lexxdigital.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.orders.adapter.AdapterOrderList;
import com.lexxdigital.easyfoodvone.orders.models.OrdersListResponse;
import com.lexxdigital.easyfoodvone.orders.models.OrdersRequest;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class OrderListFragment extends Fragment implements AdapterOrderList.OnItemClickListener, Constants.DialogClickedListener {
    View view;
    @BindView(R.id.tab_new)
    TextView tabNew;
    @BindView(R.id.tab_new_count)
    TextView tabNewCount;
    TextView tabAccepted;
    TextView tabAcceptedCount;
    @BindView(R.id.tab_rejected)
    TextView tabRejected;
    @BindView(R.id.tab_rejected_count)
    TextView tabRejectedCount;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    LinearLayout newTab, acceptedTab, rejectedTab;

    @BindView(R.id.recyclerview_orders)
    RecyclerView recyclerviewOrders;
    Unbinder unbinder;
    private AdapterOrderList mAdapter;
    private boolean isOpen = true;
    private Context mContext;
    UserPreferences userPreferences;
    Context context;
    public static String limitFrom = "0";
    public static String limitTo = "100";
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner statusSpinner;
    CompositeDisposable disposable;

    int itemPosition = 0;
    static OrderListFragment orderListFragment;


    //TODO: 1 = new orders , 2= accepted orders, 3 = rejected orders
    int whichTabActive = 1;


    //TODO Loading first time ==
    boolean firstTimeLoading = true;

    public static OrderListFragment newInstance() {

        return orderListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public OrderListFragment() {
    }

    @SuppressLint("ValidFragment")
    public OrderListFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderListFragment = this;
    }

    public void callFromActivity() {
        Log.e("11111111", "11111111");
        getNewOrder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        newTab = view.findViewById(R.id.newTab);
        acceptedTab = view.findViewById(R.id.acceptedTab);
        rejectedTab = view.findViewById(R.id.rejectedTab);

        tabAcceptedCount = view.findViewById(R.id.tab_accepted_count);
        tabAccepted = view.findViewById(R.id.tab_accepted);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleTabs(whichTabActive);
            }
        });
        mAdapter = new AdapterOrderList(mContext, this);

        unbinder = ButterKnife.bind(this, view);
        recyclerviewOrders.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerviewOrders.setAdapter(mAdapter);
        onTabClickEvents();

        return view;
    }


    private void getAllOrders(final String orders) {
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "");
        dialog.setCancelable(false);
        dialog.show();
        try {
            firstTimeLoading = false;
            OrdersRequest request = new OrdersRequest();
            request.setRestaurant_id(((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(context, Constants.LOGIN_RESPONSE)).getRestaurant_id());
            request.setLimit(limitTo);
            request.setOffset(limitFrom);
            request.setStatus(orders);


            ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrders(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrdersListResponse>() {
                        @Override
                        public void onSuccess(OrdersListResponse data) {
                            dialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            if (data.isSuccess()) {
                                tabAcceptedCount.setText(data.getData().getTotal_accepted_order() + "");
                                tabRejectedCount.setText(data.getData().getTotal_rejected_order() + "");
                                tabNewCount.setText(data.getData().getTotal_new_order() + "");
                                mAdapter.addAllItems(data.getData().getOrders(), orders);

                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(context, "Server connection failed", Toast.LENGTH_SHORT).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            Log.e("Exception ", e.toString());
            Toast.makeText(context, "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void getNewOrder() {
        try {
            firstTimeLoading = false;
            OrdersRequest request = new OrdersRequest();
            request.setRestaurant_id(((LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(context, Constants.LOGIN_RESPONSE)).getRestaurant_id());
            request.setLimit(limitTo);
            request.setOffset(limitFrom);
            request.setStatus("new");

            ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrders(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrdersListResponse>() {
                        @Override
                        public void onSuccess(OrdersListResponse data) {
                            if (data.isSuccess()) {
                                tabAcceptedCount.setText(data.getData().getTotal_accepted_order() + "");
                                tabRejectedCount.setText(data.getData().getTotal_rejected_order() + "");
                                tabNewCount.setText(data.getData().getTotal_new_order() + "");
                                OrdersListResponse.Orders newOrder = data.getData().getOrders().get(0);
                                newOrder.setNewOrder(true);
                                mAdapter.addItems(newOrder);

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {

            Log.e("Exception ", e.toString());
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onTabClickEvents() {

        tabNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whichTabActive = 1;
                handleTabs(1);
            }
        });

        tabAccepted.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whichTabActive = 2;
                handleTabs(2);
            }
        });

        tabRejected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whichTabActive = 3;
                handleTabs(3);
            }
        });


    }


    private void handleTabs(int whichTabActive) {
        switch (whichTabActive) {
            case 1:
                mAdapter.removeAll();
                getAllOrders("new");

                this.whichTabActive = 1;

                newTab.setBackground(getResources().getDrawable(R.color.bg_login_end));
                rejectedTab.setBackground(getResources().getDrawable(R.color.white));
                acceptedTab.setBackground(getResources().getDrawable(R.color.white));

                break;
            case 2:
                mAdapter.removeAll();
                getAllOrders("accepted");

                this.whichTabActive = 2;
                newTab.setBackground(getResources().getDrawable(R.color.white));
                rejectedTab.setBackground(getResources().getDrawable(R.color.white));
                acceptedTab.setBackground(getResources().getDrawable(R.color.bg_login_end));

                break;

            case 3:
                mAdapter.removeAll();
                getAllOrders("rejected");
                this.whichTabActive = 3;
                newTab.setBackground(getResources().getDrawable(R.color.white));
                rejectedTab.setBackground(getResources().getDrawable(R.color.bg_login_end));
                acceptedTab.setBackground(getResources().getDrawable(R.color.white));

                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (firstTimeLoading) {
            getAllOrders("new");
        } else
            handleTabs(whichTabActive);

    }


    @Override
    public void onAcceptClick(final OrdersListResponse.Orders orderDetail, final int position) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View mDialogView = factory.inflate(R.layout.change_delevery_time_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(mDialogView);
        final LinearLayout layoutTime = (LinearLayout) mDialogView.findViewById(R.id.set_time);
        final LinearLayout layoutNotes = (LinearLayout) mDialogView.findViewById(R.id.notes);
        TextView btnRemove = mDialogView.findViewById(R.id.txt_remove);
        TextView btnAdd = mDialogView.findViewById(R.id.btn_add);
        final TextView txtResult = mDialogView.findViewById(R.id.txt_result);

        txtResult.setText(orderDetail.getAverage_delivery_time() + "");

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(txtResult.getText().toString()) > orderDetail.getAverage_delivery_time())
                    txtResult.setText(String.valueOf(Integer.parseInt(txtResult.getText().toString()) - 5));
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(txtResult.getText().toString()) < 90)
                    txtResult.setText(String.valueOf(Integer.parseInt(txtResult.getText().toString()) + 5));
            }
        });

        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                acceptRejectOrder("accepted", orderDetail, txtResult.getText().toString(), "", position);

            }
        });
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

            }
        });

        mDialog.show();
    }

    int selectPos = 0;

    @Override
    public void onRejectClick(final OrdersListResponse.Orders orderDetail, final int position) {
        {
            final String[] reasons = new String[]{"Select reason", "Too busy to fulfill order", "Items not in stock", "Delivery driver unavailable ", "Closing soon", "collection only", "Other"};
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View mDialogView = factory.inflate(R.layout.reject_confirmation_dialog, null);


            final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
            mDialog.setView(mDialogView);
            final EditText notes = (EditText) mDialogView.findViewById(R.id.notes);
            final Spinner spinnerMainItem = mDialogView.findViewById(R.id.spinner_main_item);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, reasons);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMainItem.setAdapter(spinnerArrayAdapter);

            spinnerMainItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectPos = position;
                    if (position == 0) {
                        notes.setVisibility(View.GONE);
                        notes.setText("");
                    } else if (position == 6) {
                        notes.setText("");
                        notes.setVisibility(View.VISIBLE);
                    } else {
                        notes.setVisibility(View.GONE);
                        notes.setText(reasons[position]);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectPos == 0) {
                        Toast.makeText(mContext, "Please select rejection reason", Toast.LENGTH_SHORT).show();
                    } else {
                        acceptRejectOrder("rejected", orderDetail, "", notes.getText().toString(), position);
                        mDialog.dismiss();
                    }

                }
            });
            mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                }
            });

            mDialog.show();


        }
    }

    public void acceptRejectOrder(final String acceptReject, OrdersListResponse.Orders orderDetail, String deliveryTime, String notes, final int position) {
        itemPosition = position;
        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        try {
            AcceptRejectOrderRequest request = new AcceptRejectOrderRequest();
            request.setCustomer_id(orderDetail.getCustomer_id());
            request.setOrder_number(orderDetail.getOrder_num());
            request.setOrder_response(acceptReject);
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setDelivey_time(deliveryTime);
            request.setNotes(notes);

            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.acceptRejectOrders(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {
                            dialog.dismiss();
                            if (data.isSuccess()) {
                                if (acceptReject.equals("accepted")) {
                                    Toast.makeText(mContext, "Order Accepted", Toast.LENGTH_SHORT).show();
                                    mAdapter.deleteItem(position);
                                    if (Integer.parseInt(tabNewCount.getText().toString()) > 0) {
                                        tabNewCount.setText((Integer.parseInt(tabNewCount.getText().toString()) - 1) + "");
                                    }
                                    if (!TextUtils.isEmpty(tabAcceptedCount.getText().toString())) {
                                        tabAcceptedCount.setText((Integer.parseInt(tabAcceptedCount.getText().toString()) + 1) + "");
                                    }


                                } else {
                                    Constants.alertDialogReject("You have rejected this order. \n" +
                                            " Customer has been \n" +
                                            "notified", getActivity(), OrderListFragment.this);

                                    if (!TextUtils.isEmpty(tabRejectedCount.getText().toString())) {
                                        tabRejectedCount.setText((Integer.parseInt(tabRejectedCount.getText().toString()) + 1) + "");
                                    }
                                    if (Integer.parseInt(tabNewCount.getText().toString()) > 0) {
                                        tabNewCount.setText((Integer.parseInt(tabNewCount.getText().toString()) - 1) + "");
                                    }


                                }


                            } else {
                                alertDialog(data.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Failed! Try again.", Toast.LENGTH_LONG).show();
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


    @Override
    public void onDialogClicked() {
        mAdapter.deleteItem(itemPosition);
        if (Integer.parseInt(tabNewCount.getText().toString()) != 0) {
            tabNewCount.setText(String.valueOf(Integer.parseInt(tabNewCount.getText().toString()) - 1));
        }
    }

    @Override
    public void onDialogRejectClicked() {
        mAdapter.deleteItem(itemPosition);
        if (Integer.parseInt(tabNewCount.getText().toString()) != 0) {
            tabNewCount.setText(String.valueOf(Integer.parseInt(tabNewCount.getText().toString()) - 1));
        }
    }


}
