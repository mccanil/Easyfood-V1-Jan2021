package com.lexxdigital.easyfoodvone.single_order_detail.view.impl;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lexxdigital.easyfoodvone.R;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.lexxdigital.easyfoodvone.orders.models.OrdersListResponse;
import com.lexxdigital.easyfoodvone.single_order_detail.presenter.SingleOrderDetailPresenter;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class SingleOrderDetailActivity extends AppCompatActivity
{
    OrdersListResponse.Orders orderDetail;
    EditText input;
    Button action;
    SingleOrderDetailPresenter presenter;
    @BindView(R.id.restaurant_logo)
    CircleImageView restaurantLogo;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.close)
    LinearLayout close;
    @BindView(R.id.open)
    LinearLayout open;
    @BindView(R.id.home_delevery)
    LinearLayout homeDelevery;
    @BindView(R.id.home_delevery_off)
    LinearLayout homeDeleveryOff;
    @BindView(R.id.txt_orderid)
    TextView txtOrderid;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_contact)
    TextView txtContact;
    @BindView(R.id.txt_amount)
    TextView txtAmount;
    @BindView(R.id.txt_date_time)
    TextView txtDateTime;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.btn_accept)
    Button btnAccept;
    @BindView(R.id.btn_view_order)
    TextView btnViewOrder;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.btn_change_delevery_time)
    Button btnChangeDeleveryTime;
    private boolean isOpen = true;
    LinearLayout actionLayout;
    LoginResponse.Data baseDetails;
    TextView textView4;
    boolean isHomeDelivery=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_detail);


        textView4 = findViewById(R.id.textView4);




        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_icon));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        baseDetails = (LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(this, Constants.LOGIN_RESPONSE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRestaurantDetails();
            }
        });




        //TODO: Getting intent order value......
        if (getIntent().hasExtra(Constants.ORDER_DETAIL)) {
            orderDetail = (OrdersListResponse.Orders) getIntent().getExtras().get(Constants.ORDER_DETAIL);
            if (orderDetail == null) {
                Toast.makeText(this, "Order Not Found", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                setData();
            }
        } else {
            Toast.makeText(this, "Order Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnChangeDeleveryTime.setVisibility(View.GONE);

        onClickEvents();
    }

    private void setRestaurantDetails()
    {
        if (baseDetails!=null)
        {
            Picasso.get().load(baseDetails. getRestaurant_image())
                    .placeholder(R.drawable.restaurant_icon)
                    .error(R.drawable.restaurant_icon)
                    .into(restaurantLogo);

            textView4.setText(baseDetails.getPost_code());
            restaurantName.setText(baseDetails.getRestaurant_name());



            if (baseDetails.isOpen())
            {
                open.setVisibility(View.VISIBLE);
                close.setVisibility(View.GONE);
                isOpen = true;
            }else {
                close.setVisibility(View.VISIBLE);
                open.setVisibility(View.GONE);
                isOpen = false;
            }

            if (baseDetails.isHome_delivery())
            {
                isHomeDelivery =true;
                homeDelevery.setVisibility(View.VISIBLE);
                homeDeleveryOff.setVisibility(View.GONE);

            }else {
                isHomeDelivery =false;
                homeDelevery.setVisibility(View.GONE);
                homeDeleveryOff.setVisibility(View.VISIBLE);
            }
        }
    }





    private void setData() {
        LinearLayout callLayout = findViewById(R.id.callLayout);
        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtContact.getText().toString().equals(""))
                {
                    Toast.makeText(SingleOrderDetailActivity.this, "Number not found", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtContact.getText().toString()));
                        startActivity(intent);
                    }catch (Exception ex)
                    {
                        Toast.makeText(SingleOrderDetailActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        txtOrderid.setText(orderDetail.getOrder_num());
        txtAddress.setText(orderDetail.getCustomer_location());
        txtAmount.setText(Constants.POUND + orderDetail.getOrder_total());
        txtContact.setText(orderDetail.getCustomer_contact());
        txtName.setText(orderDetail.getCustomer_name());
        txtDateTime.setText(orderDetail.getOrder_date_time());

        handleActions(orderDetail.getOrder_status());

        //TODO: Open dialer on click the phone icon...


    }

    private void handleActions(String order_status)
    {
        actionLayout = findViewById(R.id.actionLayout);
        switch (order_status) {

            case "new":
                break;
            case "accepted":
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
            case "rejected":
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
        }
    }

    public void onClickEvents() {

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (new ApiCallMethods(SingleOrderDetailActivity.this).acceptRejectOrder(orderDetail.getCustomer_id(),
                        orderDetail.getOrder_num(),baseDetails.getRestaurant_id(),"rejected","25"))
                {
                    btnAccept.setVisibility(View.GONE);
                    btnReject.setClickable(false);
                    alertDialog("You have rejected this order \n" +
                        "and customer has been \n" +
                        "notified",false);
                }else {
                    alertDialog("Order Rejection Failed",false);
                }*/
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeTimeaAertDialog("accepted");
            }
        });
        btnViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleOrderDetailActivity.this, NewOrderActivity.class);
                intent.putExtra(Constants.ORDER_DETAIL,(Serializable)orderDetail);
                startActivity(intent);
            }
        });

        btnChangeDeleveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // changeTimeaAertDialog();
            }
        });

        open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                open.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                homeDelevery.setVisibility(View.GONE);
                homeDeleveryOff.setVisibility(View.VISIBLE);
                isOpen = false;
            }
        });
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                open.setVisibility(View.VISIBLE);
                close.setVisibility(View.GONE);
                isOpen = true;
            }
        });

        homeDelevery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isOpen) {
                    homeDelevery.setVisibility(View.GONE);
                    homeDeleveryOff.setVisibility(View.VISIBLE);
                }
            }
        });
        homeDeleveryOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isOpen) {
                    homeDelevery.setVisibility(View.VISIBLE);
                    homeDeleveryOff.setVisibility(View.GONE);
                }
            }
        });
    }


    public void alertDialog(String msg, final boolean value) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this).create();
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

    public void changeTimeaAertDialog(String response) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View mDialogView = factory.inflate(R.layout.change_delevery_time_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this).create();
        mDialog.setView(mDialogView);
        final LinearLayout layoutTime = (LinearLayout) mDialogView.findViewById(R.id.set_time);
        final LinearLayout layoutNotes = (LinearLayout) mDialogView.findViewById(R.id.notes);
        TextView btnRemove = mDialogView.findViewById(R.id.txt_remove);
        TextView btnAdd = mDialogView.findViewById(R.id.btn_add);
        final TextView txtResult = mDialogView.findViewById(R.id.txt_result);
        txtResult.setText(orderDetail.getDelivery_time());

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(txtResult.getText().toString()) > Integer.parseInt(orderDetail.getDelivery_time()))
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
        CheckBox check = mDialogView.findViewById(R.id.checkBox);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                             @Override
                                             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                 if (isChecked) {
                                                     layoutTime.setVisibility(View.VISIBLE);
                                                     layoutNotes.setVisibility(View.VISIBLE);
                                                 } else {
                                                     layoutTime.setVisibility(View.GONE);
                                                     layoutNotes.setVisibility(View.GONE);
                                                 }
                                             }
                                         }
        );

//        msgText.setText("");
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();

                //TODO: Accept reject method will be here.....
              /*  if (new ApiCallMethods(SingleOrderDetailActivity.this).acceptRejectOrder(orderDetail.getCustomer_id(),
                        orderDetail.getOrder_num(),baseDetails.getRestaurant_id(),"accept",txtResult.getText().toString()))
                {
                    btnReject.setVisibility(View.GONE);
                    btnReject.setClickable(false);
                    alertDialog("The delivery time has been \n" +
                            "changed and order accepted, customer has \n" +
                            "been notified",true);
                }else {
                    alertDialog("Order Accept Failed",false);
                }*/


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
}
