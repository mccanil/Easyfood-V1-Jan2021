package com.lexxdigital.easyfoodvone.firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lexxdigital.easyfoodvone.api_handler.ApiClient;
import com.lexxdigital.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigital.easyfoodvone.helper.PrintEsayFood;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.login.view.impl.LoginActivity;
import com.lexxdigital.easyfoodvone.new_order.adapter.CartAdapter;
import com.lexxdigital.easyfoodvone.new_order.models.OrderDetailsRequest;
import com.lexxdigital.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigital.easyfoodvone.new_order.view.impl.NewOrderActivity;
import com.lexxdigital.easyfoodvone.notificationUtils.NotificationUtils;
import com.lexxdigital.easyfoodvone.orders.view.impl.OrdersActivity;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.LoadingDialog;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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

                        if (userPreferences.getResponse(this, Constants.LOGIN_RESPONSE) != null) {
                            resultIntent = new Intent(this, OrdersActivity.class);
                            if (!TextUtils.isEmpty(order_number))
                                getOrderDetail(order_number);

                        } else {
                            resultIntent = new Intent(this, LoginActivity.class);
                        }

                        resultIntent.putExtra(Constants.NOTIFICATION_TYPE_ACCEPTED, Constants.NOTIFICATION_TYPE_ACCEPTED);

                        resultIntent.putExtra("message", message);
                        showNotificationMessage(getApplicationContext(), "New Orders!", message, timestamp, resultIntent);
                        playNotificationSound();
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/notification");
            r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();


            handler.postDelayed(runnable, 10000);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (r != null && r.isPlaying())
                r.stop();
        }
    };

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
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
                            PrintEsayFood.printOrder1(logo,orderDetailsResponse,getApplicationContext());

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


}