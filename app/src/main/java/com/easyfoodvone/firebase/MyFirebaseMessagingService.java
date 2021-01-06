package com.easyfoodvone.firebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.easyfoodvone.utility.PrefManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.easyfoodvone.charity.webservice.responsebean.NewDetailBean;
import com.easyfoodvone.helper.PrintEsayFood;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.login.view.impl.LoginActivity;
import com.easyfoodvone.notificationUtils.NotificationUtils;
import com.easyfoodvone.orders.view.impl.OrdersActivity;
import com.easyfoodvone.utility.ApplicationContext;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.UserPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.easyfoodvone.utility.Constants.CHARITY_STATUS_INTENT;
import static com.easyfoodvone.utility.Helper.isInternetOn;
import static com.easyfoodvone.utility.UserContants.AUTH_TOKEN;

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
                String res_status=remoteMessage.getData().get("res_status");


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
                       // playNotificationSound();
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    } else if (notif_type.equals(Constants.NOTIFICATION_CHARITY_STATUS)) {
                        Intent charityIntent = new Intent(CHARITY_STATUS_INTENT);
                        if (getApplicationContext() != null) {
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(charityIntent);
                        }
                    } else if (notif_type.equalsIgnoreCase("restautant_open_status") || notif_type.equalsIgnoreCase("restautant_open_status")) {
                        Intent intent = new Intent("local");
                        intent.putExtra("type", res_status);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        resultIntent = new Intent(this, OrdersActivity.class);
                        if (res_status.equalsIgnoreCase("open")) {
                            showNotificationMessage(getApplicationContext(), "Hey! ItsRestaurant Opening Time ", message, timestamp, resultIntent);
                            LoginResponse.Data loginResponse = (LoginResponse.Data) userPreferences.getResponse(this, Constants.LOGIN_RESPONSE);
                            loginResponse.setIs_open(true);
                            userPreferences.setResponse(this, Constants.LOGIN_RESPONSE, loginResponse);

                        } else if (res_status.equalsIgnoreCase("closed")) {
                            LoginResponse.Data loginResponse = (LoginResponse.Data) userPreferences.getResponse(this, Constants.LOGIN_RESPONSE);
                            loginResponse.setIs_open(false);
                            userPreferences.setResponse(this, Constants.LOGIN_RESPONSE, loginResponse);

                            showNotificationMessage(getApplicationContext(), "Hey! Its Restaurant Closing Time ", message, timestamp, resultIntent);
                        }
                       // playNotificationSound();
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

   /* private void getOrderDetail(String ordersNumber) {
        try {
            OrderDetailsRequest request = new OrderDetailsRequest();
            request.setOrder_number(ordersNumber);

            ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getOrderDetails(prefManager.getPreference(AUTH_TOKEN,""),request)
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

    }*/

  /*  private void orderDetailsNew(final String ordersNumber) {

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

    }*/


    private void getOrederDetails(String orderNumber) {
        if (isInternetOn(this)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("order_number", orderNumber);
            com.easyfoodvone.charity.webservice.ApiInterface apiInterface = com.easyfoodvone.charity.webservice.ApiClient.getClient().create(com.easyfoodvone.charity.webservice.ApiInterface.class);
            Call<NewDetailBean> call = apiInterface.getOrderDetail(PrefManager.getInstance(getApplicationContext()).getPreference(AUTH_TOKEN,""),jsonObject);

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
