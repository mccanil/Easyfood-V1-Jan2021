package com.lexxdigitals.easyfoodvone.firebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.NewDetailBean;
import com.lexxdigitals.easyfoodvone.helper.PrintEsayFood;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.login.view.impl.LoginActivity;
import com.lexxdigitals.easyfoodvone.models.menu_response.OrdersDetailsResponseNew;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsRequest;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigitals.easyfoodvone.notificationUtils.NotificationUtils;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigitals.easyfoodvone.utility.ApplicationContext;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lexxdigitals.easyfoodvone.utility.Constants.CHARITY_STATUS_INTENT;
import static com.lexxdigitals.easyfoodvone.utility.Helper.isInternetOn;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    UserPreferences userPreferences;
    Ringtone r;
    Handler handler = new Handler();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        userPreferences = UserPreferences.getUserPreferences();

       /* Intent intent = new Intent();
        intent.setAction(SESSION_EXPIRED);
        sendBroadcast(intent);
*/
        if (remoteMessage == null)
            return;
        else {

            try {
                String message = remoteMessage.getData().get("message");
                String timestamp = remoteMessage.getData().get("timestamp");
                String notif_type = remoteMessage.getData().get("type");
                String order_number = remoteMessage.getData().get("order_number");


                Intent resultIntent = null;

                if (notif_type != null)
                    if (notif_type.equals(Constants.NOTIFICATION_TYPE_ACCEPTED)) {
                        Intent intent = new Intent(Constants.NOTIFICATION_TYPE_ACCEPTED);
                        intent.putExtra("message", message);
                        intent.putExtra(Constants.ORDER_NUMBER, order_number);

                        if (userPreferences.getResponse(this, Constants.LOGIN_RESPONSE) != null) {
                            resultIntent = new Intent(this, OrdersActivity.class);
                            if (!TextUtils.isEmpty(order_number)) {
                                //orderDetailsNew(order_number);
                                getOrederDetails(order_number);
                            }

                        } else {
                            resultIntent = new Intent(this, LoginActivity.class);
                        }

                        resultIntent.putExtra(Constants.NOTIFICATION_TYPE_ACCEPTED, Constants.NOTIFICATION_TYPE_ACCEPTED);

                        resultIntent.putExtra("message", message);
                        showNotificationMessage(getApplicationContext(), "New Orders!", message, timestamp, resultIntent);
                        playNotificationSound();
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    } else if (notif_type.equals(Constants.NOTIFICATION_CHARITY_STATUS)) {
                        Intent charityIntent = new Intent(CHARITY_STATUS_INTENT);
                        if (getApplicationContext() != null) {
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(charityIntent);
                        }
                    } else if (notif_type.equalsIgnoreCase("closed") || notif_type.equalsIgnoreCase("open")) {
                        Intent intent = new Intent("local");
                        intent.putExtra("type", notif_type);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        resultIntent = new Intent(this, OrdersActivity.class);
                        if (notif_type.equalsIgnoreCase("open")) {
                            showNotificationMessage(getApplicationContext(), "Hey! ItsRestaurant Opening Time ", message, timestamp, resultIntent);
                            LoginResponse.Data loginResponse = (LoginResponse.Data) userPreferences.getResponse(this, Constants.LOGIN_RESPONSE);
                            loginResponse.setIs_open(true);
                            userPreferences.setResponse(this, Constants.LOGIN_RESPONSE, loginResponse);

                        } else if (notif_type.equalsIgnoreCase("closed")) {
                            LoginResponse.Data loginResponse = (LoginResponse.Data) userPreferences.getResponse(this, Constants.LOGIN_RESPONSE);
                            loginResponse.setIs_open(false);
                            userPreferences.setResponse(this, Constants.LOGIN_RESPONSE, loginResponse);

                            showNotificationMessage(getApplicationContext(), "Hey! Its Restaurant Closing Time ", message, timestamp, resultIntent);
                        }
                        playNotificationSound();
                    } else if (notif_type.equals(Constants.NOTIFICATION_TYPE_CANCELED)) {

                    }

            } catch (Exception ex) {
                Log.e("Exception in mess : ", ex.toString());
            }
        }
    }

    public void playNotificationSound() {

        ApplicationContext.getInstance().playNotificationSound();
       /* try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/notification");
            r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();


            handler.postDelayed(runnable, 1000);


        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (r != null && r.isPlaying())
                r.stop();
        }
    };

    private void showNotificationMessage(Context context, String title, String message, String
            timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void getOrderDetail(String ordersNumber) {
        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrderDetails(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrderDetailsResponse>() {
                        @Override
                        public void onSuccess(OrderDetailsResponse data) {
                            UserPreferences userPreferences = UserPreferences.getUserPreferences();
                            LoginResponse.Data ss = (LoginResponse.Data) userPreferences.getResponse(getApplicationContext(), Constants.LOGIN_RESPONSE);

                            byte[] decodedString = Base64.decode(ss.getLogo(), Base64.DEFAULT);
                            Bitmap logo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            OrderDetailsResponse.OrderDetails orderDetailsResponse = data.getOrders_details();
                            PrintEsayFood.printOrder1(logo, orderDetailsResponse, getApplicationContext());

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error ", e.getLocalizedMessage());
                        }
                    }));

        } catch (Exception e) {

            Log.e("Exception ", e.getLocalizedMessage());
        }

    }

    private void orderDetailsNew(final String ordersNumber) {

        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.orderDetailsNew(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrdersDetailsResponseNew>() {
                        @Override
                        public void onSuccess(OrdersDetailsResponseNew data) {
                            Log.e("PrintHere", "" + ordersNumber);

                            byte[] logoByte = UserPreferences.getUserPreferences().getByteArray(getApplicationContext(), Constants.RESTAURANT_LOGO);
                            Bitmap logo = null;
                            if (logoByte != null) {
                                logo = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
                                //logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), 80, false);
                            }
                            // PrintEsayFood.printOrderNew(getApplicationContext(), logo, Constants.getStoredData(getApplicationContext()), data.getOrdersDetails());

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());

        }

    }


    private void getOrederDetails(String orderNumber) {
        if (isInternetOn(this)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("order_number", orderNumber);
            com.lexxdigitals.easyfoodvone.charity.webservice.ApiInterface apiInterface = com.lexxdigitals.easyfoodvone.charity.webservice.ApiClient.getClient().create(com.lexxdigitals.easyfoodvone.charity.webservice.ApiInterface.class);
            Call<NewDetailBean> call = apiInterface.getOrderDetail(jsonObject);

            call.enqueue(new Callback<NewDetailBean>() {
                @Override
                public void onResponse(@NonNull Call<NewDetailBean> call, @NonNull Response<NewDetailBean> response) {
                    try {
                        if (response.isSuccessful()) {
                            NewDetailBean newDetailBean = response.body();
                            if (newDetailBean.isSuccess()) {
                                byte[] logoByte = UserPreferences.getUserPreferences().getByteArray(getApplicationContext(), Constants.RESTAURANT_LOGO);
                                Bitmap logo = null;
                                if (logoByte != null) {
                                    logo = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
                                }
                                if (newDetailBean.getOrders_details() != null) {
                                    PrintEsayFood.printOrderNew(getApplicationContext(), logo, Constants.getStoredData(getApplicationContext()), newDetailBean.getOrders_details());
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<NewDetailBean> call, @NonNull Throwable t) {
                    Log.e("Print", "" + t.getMessage());
                    //  swipeRefresh.setRefreshing(false);
                    // showSnackBar(view, getString(R.string.msg_please_try_later));
                }
            });
        }
    }

}