package com.lexxdigitals.easyfoodvone.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.NewDetailBean;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.models.OrderReportResponse;
import com.lexxdigitals.easyfoodvone.models.menu_response.Meal;
import com.lexxdigitals.easyfoodvone.models.menu_response.MenuProduct;
import com.lexxdigitals.easyfoodvone.models.menu_response.MenuProductSize;
import com.lexxdigitals.easyfoodvone.models.menu_response.Modifier;
import com.lexxdigitals.easyfoodvone.models.menu_response.OrdersDetailsTop;
import com.lexxdigitals.easyfoodvone.models.menu_response.ProductModifier;
import com.lexxdigitals.easyfoodvone.models.menu_response.SizeModifier;
import com.lexxdigitals.easyfoodvone.models.menu_response.SpecialOffer;
import com.lexxdigitals.easyfoodvone.models.menu_response.UpSells;
import com.lexxdigitals.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;
import com.lexxdigitals.easyfoodvone.utility.printerutil.AidlUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import woyou.aidlservice.jiuiv5.ICallback;

public class PrintEsayFood {
    static DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    static DateFormat timeFormat = new SimpleDateFormat("HH:mma");


    static String line20 = "--------------------------------------";//38 char
    static String line20Bold = "--------------------------------------";//38 char
    static String line22 = "----------------------------------";//34 char
    static String line22Bold = "----------------------------------";//34 char
    static String line24 = "--------------------------------";//32 char
    static String line24Bold = "--------------------------------";//32 char
    static String line26 = "-----------------------------";//29 char
    static String line26Bold = "-----------------------------";//29 char

    public void printProductBarcode(List<String> barCode, List<String> productIdAndName) {
        if (AidlUtil.getInstance().isConnect()) {
            AidlUtil.getInstance().printBarCode(barCode, productIdAndName, 8, 80, 2, 1);
        }
    }


    public void openDrawer(ICallback callback) throws RemoteException {
        AidlUtil.getInstance().openDrawer(callback);

    }

    public static void printOrderr(OrderDetailsResponse.OrderDetails printData) {
        String data = "Product     Price  Qty Dis Amt ";
        String item = createData(printData);

        AidlUtil.getInstance().printText(data, 22, false, false, 1);
        AidlUtil.getInstance().printText(item, 22, false, false, 1);

    }

    private static String createData(OrderDetailsResponse.OrderDetails printData) {
        StringBuilder data = new StringBuilder();
        int count = 0;
        List<OrderDetailsResponse.OrderDetails.Cart.Items> items;
        for (OrderDetailsResponse.OrderDetails.Cart item : printData.getCart()) {
            items = item.getItems();
            for (int i = 0; i < item.getItems().size(); i++) {
                String pName = items.get(i).getProduct_name();
                String pPrice = items.get(i).getProduct_price();
                String pQty = items.get(i).getProduct_qty();
                String pDis = "0.00";
                String pAmount = items.get(i).getTotal_amount();

                StringBuilder nName = new StringBuilder();
         /*   int loopCount = Math.round((10/pName.trim().length()));

            if(loopCount>1){
                int firstIndex = 0;
                int lastIndex = 10;
                for (int i = 0; i < loopCount; i++) {
                    nName.append(pName.substring(firstIndex,lastIndex));
                    nName.append("\n");
                    firstIndex = lastIndex;
                    lastIndex = (lastIndex+i);
                }
            }else{
                int countLoop = pName.trim().length()-10;
                nName.append(pName);
                for (int i = 0; i < countLoop; i++) {
                    nName.append(" ");
                }
            }*/
                data.append(pName != null && pName.length() > 7 ? pName.subSequence(0, 9) : nName + " ");
                data.append(" " + pPrice);
                data.append("  " + pQty + " ");
                data.append(" " + pDis + " ");
                data.append(" " + pAmount + "\n");
            }

        }
        return data.toString();
    }


    public static void printReport(Context context, Bitmap logo, LoginResponse.Data restaurantData, OrderReportResponse.Data reportData) {
        Date date = new Date();
        String _date = dateFormat.format(date);
        String _time = timeFormat.format(date);

        if (AidlUtil.getInstance().isConnect()) {
            if (logo != null)
                AidlUtil.getInstance().printBitmap(logo, 1, 1, 1);

            AidlUtil.getInstance().printText(restaurantData.getRestaurant_name(), 26, true, false, 1, 2);
            AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);
            AidlUtil.getInstance().printText("DATE: " + _date + "  TIME: " + _time, 22, true, false, 1, 1);
            AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);

            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("TOTAL ORDER", reportData.getTotal_orders(), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("TOTAL ITEMS", reportData.getTotal_items(), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("TOTAL REVENUE", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getTotal_revenue())), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("TOTAL DISCOUNT", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getTotal_discount())), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("WALLET BALANCE", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getWallet_balance())), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("CREDIT(" + reportData.getTotal_orders_by_credit_card() + ")", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getTotal_orders_by_credit_card_amount())), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("ACCEPTED(" + reportData.getTotal_orders_accepted() + ")", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getTotal_orders_accepted_amount())), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("DECLINED(" + reportData.getTotal_orders_declined() + ")", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getTotal_orders_declined_amount())), 32), 24, true, false, 1);
            AidlUtil.getInstance().printText(printSpaceBetweenTwoString("CASH(" + reportData.getTotal_orders_by_cash() + ")" + context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(reportData.getTotal_orders_by_cash_amount())), Constants.decimalFormat(Double.parseDouble(reportData.getTotal_orders_by_cash_per())) + "%", 32), 24, true, false, 1);

            AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);

            String dataReport = reportData(context, reportData.getOrder_list(), 32);
            AidlUtil.getInstance().printText(dataReport, 20, true, false, 4);

        }
    }

    private static String reportData(Context context, List<OrderReportResponse.OrdersList> item, int charCount) {
        StringBuilder data = new StringBuilder();
        int orderNoShowCount = 8;
        int postCodeCount = 7;
        int dateCount = 11;
        int itemQtyCount = 2;
        int amountCount = 6;
        for (int i = 0; i < item.size(); i++) {
            String orderId = item.get(i).getOrder_id();
            if (orderId.length() > 10) {
                data.append(orderId.substring((orderId.length() - orderNoShowCount), orderId.length()));
            } else {
                data.append(orderId);
            }
            if (item.get(i).getCustomer_post_code().length() == 0) {
                data.append("         ");

            } else {
                data.append("  " + item.get(i).getCustomer_post_code());
            }
            String[] date = item.get(i).getOrder_date().split(" ");
            data.append(" ");
            for (int j = 0; j < date.length; j++) {
                data.append(date[j]);
            }
//            data.append(" " + item.get(i).getOrder_date());
            data.append("  " + item.get(i).getTotal_items());
            if (item.get(i).getOrder_total().length() < amountCount) {
                int spacePrint = amountCount - item.get(i).getOrder_total().length();
                data.append(" ");
                for (int j = 0; j < spacePrint; j++) {
                    data.append(" ");
                }
                data.append(context.getResources().getString(R.string.currency) + item.get(i).getOrder_total() + "\n");
            } else {
                data.append(" " + context.getResources().getString(R.string.currency) + item.get(i).getOrder_total() + "\n");
            }
        }

        return data.toString();
    }

    public static void printOrderNew(Context context, Bitmap logo, LoginResponse.Data restaurantData, NewDetailBean.OrdersDetailsBean orderDetail) {
        new MakeData(context, logo, orderDetail, restaurantData).execute();
//        String cartData = makeCartData(context, orderDetail.getCart(), 34);
    }

    private static String printSpaceBetweenTwoString(String str1, String str2, int charCount) {
        String data = "";
        int stringLength = (str1.length() + str2.length());

        int spaceCount = charCount - stringLength;
        String space = "";
        for (int i = 0; i < (spaceCount - 1); i++) {
            space += " ";
        }
        data = str1 + space + str2;
        return data;
    }

    private static class MakeData extends AsyncTask<Void, Void, String> {
        Context context;
        Bitmap logo;
        NewDetailBean.OrdersDetailsBean orderDetail;
        LoginResponse.Data restaurantData;
        int charCount = 43;
        Bitmap bitmap1 = null;
        String addressFormatToPrint = "";
        String getAddress_1 = "", address_2 = "", city = "", post_code = "", country = "", AddressToPrint = "";

        public MakeData(Context context, Bitmap logo, NewDetailBean.OrdersDetailsBean orderDetail, LoginResponse.Data restaurantData) {
            this.context = context;
            this.logo = logo;
            this.orderDetail = orderDetail;
            this.restaurantData = restaurantData;
            Log.e("1111111111111", "1111111111111111");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.e("222222222222", "222222222222222");

            return makeCartData(context, charCount, orderDetail.getCart());
        }

        @Override
        protected void onPostExecute(String cartData) {
            super.onPostExecute(cartData);
            Log.e("cccccccccccc", "cccccccccccc");

            Log.e("Cart DATA\n", cartData);
            String customerName = orderDetail.getDelivery_address().getCustomer_name();
            String customerPhone = orderDetail.getDelivery_address().getPhone_number();

            String customerAddress = orderDetail.getDelivery_address().getCustomer_location();

            try {
                getAddress_1 = orderDetail.getDelivery_address().getAddress_1();
                address_2 = orderDetail.getDelivery_address().getAddress_2();
                city = orderDetail.getDelivery_address().getCity();
                post_code = orderDetail.getDelivery_address().getPost_code();
                country = orderDetail.getDelivery_address().getCountry();

                AddressToPrint = getAddress_1 + "\n" + address_2 + "\n" + city + " " + post_code + "\n" + country;
            } catch (Exception e) {
                e.printStackTrace();
            }


//            Date date = new Date();
//            String _date = dateFormat.format(date);
//            String _time = timeFormat.format(date);
            String footer = String.format("%s", "        Delivery Details        ") + "\n"
                    + String.format("%s", "--------------------------------") + "\n"
                    + String.format("%s", orderDetail.getDelivery_address().getCustomer_name()) + "\n"
                    + String.format("%s", orderDetail.getDelivery_address().getPhone_number()) + "\n"
                    + String.format("%s", orderDetail.getDelivery_address().getCustomer_location()) + "\n"
                    + String.format("Delivery Time:%s", orderDetail.getDelivery_date_time()) + "\n";
            Log.e("FOOTER >>\n", footer);

//            if (AidlUtil.getInstance().isConnect()) {
//            if (true) {
            if (true) {
                //logo = null;
                AidlUtil.getInstance().printText("easyFood.co.uk ", 36, true, false, 1, 1);
                if (logo != null) {
                    AidlUtil.getInstance().printBitmap(logo, 1, 1, 1);
                } else {
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        URL url = new URL(UserPreferences.getUserPreferences().getString(context, "LogoUrl"));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        AidlUtil.getInstance().printBitmap(myBitmap, 1, 1, 1);
                    } catch (IOException e) {
                        // Log exception
                        Log.e("IOException", e.getMessage());
                        e.printStackTrace();
                    }
                }

                if (restaurantData != null) {

                    AidlUtil.getInstance().printText(restaurantData.getRestaurant_name(), 26, true, false, 1, 2);
                    AidlUtil.getInstance().printText(customerName + "\n" + customerPhone, 36, true, false, 1, 1);

                    if (orderDetail.getDelivery_option().toUpperCase().equals("DELIVERY"))
                        addressFormatToPrint = AddressNameCorrect(customerAddress);
                    AidlUtil.getInstance().printText(AddressToPrint, 36, true, false, 1, 1);

                    AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);
                    String checkPayStatus = orderDetail.getPayment_mode();

                    try {
                        Log.e("PRINT>>\n", "UNPAID" + "\t" + orderDetail.getDelivery_option().toUpperCase());
                        if (checkPayStatus.equalsIgnoreCase("Cash")) {
                            AidlUtil.getInstance().printText("UNPAID" + "\t | \t" + orderDetail.getDelivery_option().toUpperCase(), 36, true, false, 1, 1);
                        } else {
                            AidlUtil.getInstance().printText("PAID" + "\t | \t" + orderDetail.getDelivery_option().toUpperCase(), 36, true, false, 1, 1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);

                    String timeDate = orderDetail.getOrder_date_time();
                    String dddate = Constants.getDateFromDateTime(orderDetail.getOrder_date_time(), "dd MMM yyyy hh:mm:ss", "dd-MMM") + " ";
                    String OrderTime = "", hr = "", min = "";
                    for (int i = 0; i < timeDate.length(); i++) {
                        char c = timeDate.charAt(i);
                        if (c == ' ') {
                            hr = timeDate.substring(i + 1, i + 3);
                        }
                        if (c == ':') {
                            min = timeDate.substring(i + 1, i + 3);
                            break;
                        }
                    }

                    OrderTime = dddate + hr + ":" + min;


                    AidlUtil.getInstance().printText("Order Time - " + OrderTime, 22, true, false, 1, 1);

                    String DeliveryTime1 = orderDetail.getDelivery_date_time();
                    String DeliveryTime = "", hr1 = "", min1 = "";
                    String dtime2 = Constants.getDateFromDateTime(orderDetail.getDelivery_date_time(), "dd MMM yyyy hh:mm:ss", "dd-MMM") + " ";

                    for (int j = 0; j < DeliveryTime1.length(); j++) {
                        char d = DeliveryTime1.charAt(j);
                        if (d == ' ') {
                            hr1 = DeliveryTime1.substring(j + 1, j + 3);
                        }
                        if (d == ':') {
                            min1 = DeliveryTime1.substring(j + 1, j + 3);
                            break;
                        }
                    }
                    DeliveryTime = dtime2 + hr1 + ":" + min1;

                    AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);
                    AidlUtil.getInstance().printText("Delivery Time - " + DeliveryTime, 22, true, false, 1, 1);

                    AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);

                    AidlUtil.getInstance().printText("ORDER No: " + orderDetail.getOrder_num(), 26, true, false, 1, 1);

                    AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);

                    AidlUtil.getInstance().printText(cartData, 22, true, false, 0, 1);

                    AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);

                    if (orderDetail.getOrder_notes().trim().length() > 0) {
                        AidlUtil.getInstance().printText("NOTES:", 24, true, false, 1);
//                      AidlUtil.getInstance().printText("Please knock the door as the\ndoor bell is broken", 24, true, false, 1);
                        AidlUtil.getInstance().printText(orderDetail.getOrder_notes(), 24, false, false, 1);
                        AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);
                    }

//                    AidlUtil.getInstance().printText(footer, 24, true, false, 1);

                    AidlUtil.getInstance().printText(printSpaceBetweenTwoString("DISCOUNT", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(orderDetail.getDiscount_amount())), 32), 24, false, false, 1);
                    if (orderDetail.getDelivery_option().toUpperCase().equals("DELIVERY")) {
                        AidlUtil.getInstance().printText(printSpaceBetweenTwoString("DELIVERY CHARGE", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(orderDetail.getDelivery_charge())), 32), 24, false, false, 1);
                    }
                    AidlUtil.getInstance().printText(printSpaceBetweenTwoString("PAYMENT METHOD", orderDetail.getPayment_mode().toUpperCase(), 32), 24, true, false, 1);
                    AidlUtil.getInstance().printText(printSpaceBetweenTwoString("TOTAL", context.getResources().getString(R.string.currency) + Constants.decimalFormat(Double.parseDouble(orderDetail.getOrder_total())), 32), 24, true, false, 5);

                }

            }
            Log.e("ddddddddddddd", "ddddddddddddd");
        }

    }


    private static String makeCartData(Context context, int paperRowCharCount, NewDetailBean.OrdersDetailsBean.Cart data) {
        StringBuilder orderItemData = new StringBuilder();
        int charCount = paperRowCharCount;
        int serialNoCount = 0;
        int qtyCharCount = 0;
        int priceCharCount = 8;
        int charCountNew = (charCount - serialNoCount) - qtyCharCount;
        charCountNew = (charCountNew - priceCharCount) - priceCharCount;
        int slNo = 0;


        //String productName = data.getMenu().get(i).getQty() + "x " + data.getMenu().get(i).getName();

        // Menu menu = data.getMenu().get(i);
        // title.setText(data.getMenu().get(i).getQty() + "x " + data.getMenu().get(i).getName());
        // price.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getPrice());


        // Menu menu = data.getMenu().get(i);
        // title.setText(data.getMenu().get(i).getQty() + "x " + data.getMenu().get(i).getName());
        //price.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getPrice());
      /*  for (PreviousOrderItem.OrderItemTable orderedItemsModel : products) {
            if (orderedItemsModel.getProductDetails() != null) {
                slNo++;

                if (slNo == 10) {
                    charCountNew = (((charCount - 3) - qtyCharCount) - priceCharCount) - priceCharCount;
                }

                String productName = orderedItemsModel.getProductDetails().getProductName();
                String productFirstLine = null;
                String productSecondLine = null;
                if (productName.length() > (charCountNew * 2)) {
                    productFirstLine = productName.substring(0, (charCountNew));
                    productSecondLine = productName.substring(charCountNew, ((charCountNew * 2) - 3)) + "... ";
                } else {
                    if (productName.length() > charCountNew) {
                        productFirstLine = productName.substring(0, (charCountNew));
                        productSecondLine = productName.substring((charCountNew), productName.length());
                    } else {
                        productFirstLine = productName;
                        for (int j = 0; j < (charCountNew - productName.length()); j++) {
                            productFirstLine += " ";
                        }
                    }
                }
                if (productSecondLine == null) {
                    //String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                    String amountSpace = createPriceSpace(priceCharCount, orderedItemsModel.getNet_amount());
                   // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                    orderItemData.append(
                             productFirstLine
                                    + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", orderedItemsModel.getNet_amount())
                                    + "\n"
                    );
                } else {
//                String _productName = createProductNameString(charCountNew, orderedItemsModel.getProductDetails().getProductName());
                   // String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                    String amountSpace = createPriceSpace(priceCharCount, orderedItemsModel.getNet_amount());
                   // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                    orderItemData.append( productFirstLine

                                    + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", orderedItemsModel.getNet_amount())
                                    + "\n"
                    );
                    orderItemData.append("    " + productSecondLine + "\n");
                }
            }
        }*/


        for (int i = 0; i < data.getMenu().size(); i++) {
            String productFirstLine = null;
            String productSecondLine = null;


            if (data.getMenu().get(i) != null) {
                String productName = data.getMenu().get(i).getQty() + "x " + data.getMenu().get(i).getName();
                if (productName.length() > (charCountNew * 2)) {
                    productFirstLine = productName.substring(0, (charCountNew));
                    productSecondLine = productName.substring(charCountNew, ((charCountNew * 2) - 3)) + "... ";
                } else {
                    if (productName.length() > charCountNew) {
                        productFirstLine = productName.substring(0, (charCountNew));
                        productSecondLine = productName.substring((charCountNew), productName.length());
                    } else {
                        productFirstLine = productName;
                        for (int j = 0; j < (charCountNew - productName.length()); j++) {
                            productFirstLine += " ";
                        }
                    }
                }
                if (productSecondLine == null) {
                    //String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                    String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getPrice());
                    // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                    orderItemData.append(
                            productFirstLine
                                    + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getPrice())
                                    + "\n"
                    );
                } else {
//                String _productName = createProductNameString(charCountNew, orderedItemsModel.getProductDetails().getProductName());
                    // String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                    String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getPrice());
                    // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                    orderItemData.append(productFirstLine

                            + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getPrice())
                            + "\n"
                    );
                    orderItemData.append("    " + productSecondLine + "\n");
                }
            }

            /*--------------Menu Product Modifiers----------------------------------------*/
            //orderItemListView.addView(root);

            if (data.getMenu().get(i).getOptions().getProductModifiers() != null) {
                //  List<ProductModifier> productModifiers = data.getMenu().get(i).getOptions().getProductModifiers();
                for (int j = 0; j < data.getMenu().get(i).getOptions().getProductModifiers().size(); j++) {
                    for (int k = 0; k < data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().size(); k++) {

                        //  titleModi.setText(data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getProductName());
                        //priceModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getModifierProductPrice());
                        //orderItemListView.addView(rootModi);


                        if (data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k) != null) {
                            String productModiProductName = data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getProductName();
                            if (productModiProductName.length() > (charCountNew * 2)) {
                                productFirstLine = productModiProductName.substring(0, (charCountNew));
                                productSecondLine = productModiProductName.substring(charCountNew, ((charCountNew * 2) - 3)) + "... ";
                            } else {
                                if (productModiProductName.length() > charCountNew) {
                                    productFirstLine = productModiProductName.substring(0, (charCountNew));
                                    productSecondLine = productModiProductName.substring((charCountNew), productModiProductName.length());
                                } else {
                                    productFirstLine = productModiProductName;
                                    for (int b = 0; b < (charCountNew - productModiProductName.length()); b++) {
                                        productFirstLine += " ";
                                    }
                                }
                            }
                            if (productSecondLine == null) {
                                //String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                                String amountSpace = createPriceSpace(priceCharCount, Double.parseDouble(data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getModifierProductPrice()));
                                // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                                orderItemData.append(
                                        productFirstLine
                                                + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getModifierProductPrice())
                                                + "\n"
                                );
                            } else {
//                String _productName = createProductNameString(charCountNew, orderedItemsModel.getProductDetails().getProductName());
                                // String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                                String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getPrice());
                                // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                                orderItemData.append(productFirstLine

                                        + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getModifierProductPrice())
                                        + "\n"
                                );
                                orderItemData.append("    " + productSecondLine + "\n");
                            }
                        }

                    }
                }
            }

            /*  --------------Menu Meal Products----------------------------------------*/
            if (data.getMenu().get(i).getOptions().getMealProducts() != null) {
                //List<MealProduct> mealProducts = data.getMenu().get(i).getOptions().getMealProducts();
                for (int j = 0; j < data.getMenu().get(i).getOptions().getMealProducts().size(); j++) {
                    // List<SizeModifier> sizeModifiers = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers();
                    for (int k = 0; k < data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().size(); k++) {
                        //  List<SizeModifierProduct> sizeModifierProducts = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts();
                        for (int l = 0; l < data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().size(); l++) {
                           /* LayoutInflater inflaterSizeModi = LayoutInflater.from(context);
                            View rootSizeModi = inflaterSizeModi.inflate(R.layout.order_list_item_modifiers, null);
                            TextView titleModi = rootSizeModi.findViewById(R.id.title);
                            TextView priceModi = rootSizeModi.findViewById(R.id.price);

                            titleModi.setText(data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getQuantity() + "x " + data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getProductName());
                            priceModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getAmount());
                            //orderItemListView.addView(rootSizeModi);

*/


                            if (data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k) != null) {
                                String productModiProductName = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getQuantity() + "x " + data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getProductName();
                                if (productModiProductName.length() > (charCountNew * 2)) {
                                    productFirstLine = productModiProductName.substring(0, (charCountNew));
                                    productSecondLine = productModiProductName.substring(charCountNew, ((charCountNew * 2) - 3)) + "... ";
                                } else {
                                    if (productModiProductName.length() > charCountNew) {
                                        productFirstLine = productModiProductName.substring(0, (charCountNew));
                                        productSecondLine = productModiProductName.substring((charCountNew), productModiProductName.length());
                                    } else {
                                        productFirstLine = productModiProductName;
                                        for (int b = 0; b < (charCountNew - productModiProductName.length()); b++) {
                                            productFirstLine += " ";
                                        }
                                    }
                                }
                                if (productSecondLine == null) {
                                    //String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                                    String amountSpace = createPriceSpace(priceCharCount, Double.parseDouble(data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getAmount()));
                                    // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                                    orderItemData.append(
                                            productFirstLine
                                                    + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getAmount())
                                                    + "\n"
                                    );
                                } else {
//                String _productName = createProductNameString(charCountNew, orderedItemsModel.getProductDetails().getProductName());
                                    // String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                                    String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getPrice());
                                    // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                                    orderItemData.append(productFirstLine

                                            + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getAmount())
                                            + "\n"
                                    );
                                    orderItemData.append("    " + productSecondLine + "\n");
                                }
                            }

                        }
                    }
                }
            }
            /* --------------Menu Size----------------------------------------*/

            if (data.getMenu().get(i).getOptions().getSize() != null && data.getMenu().get(i).getOptions().getSize().getProductSizeName() != null) {

                /*LayoutInflater sizeModiInflater = LayoutInflater.from(context);
                View rootSize = sizeModiInflater.inflate(R.layout.order_list_item_modifiers, null);
                TextView titleSizeModi = rootSize.findViewById(R.id.title);
                TextView priceSizeModi = rootSize.findViewById(R.id.price);

                titleSizeModi.setText(data.getMenu().get(i).getOptions().getSize().getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getProductSizeName());
                priceSizeModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getSize().getProductSizePrice());
                //  orderItemListView.addView(rootSize);*/
                if (data.getMenu().get(i).getOptions().getSize() != null) {
                    String productModiProductName = data.getMenu().get(i).getOptions().getSize().getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getProductSizeName();
                    if (productModiProductName.length() > (charCountNew * 2)) {
                        productFirstLine = productModiProductName.substring(0, (charCountNew));
                        productSecondLine = productModiProductName.substring(charCountNew, ((charCountNew * 2) - 3)) + "... ";
                    } else {
                        if (productModiProductName.length() > charCountNew) {
                            productFirstLine = productModiProductName.substring(0, (charCountNew));
                            productSecondLine = productModiProductName.substring((charCountNew), productModiProductName.length());
                        } else {
                            productFirstLine = productModiProductName;
                            for (int b = 0; b < (charCountNew - productModiProductName.length()); b++) {
                                productFirstLine += " ";
                            }
                        }
                    }
                    if (productSecondLine == null) {
                        //String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                        String amountSpace = createPriceSpace(priceCharCount, Double.parseDouble(data.getMenu().get(i).getOptions().getSize().getProductSizePrice()));
                        // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                        orderItemData.append(
                                productFirstLine
                                        + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", Double.parseDouble(data.getMenu().get(i).getOptions().getSize().getProductSizePrice()))
                                        + "\n"
                        );
                    } else {
//                String _productName = createProductNameString(charCountNew, orderedItemsModel.getProductDetails().getProductName());
                        // String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                        String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getPrice());
                        // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                        orderItemData.append(productFirstLine

                                + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", Double.parseDouble(data.getMenu().get(i).getOptions().getSize().getProductSizePrice()))
                                + "\n"
                        );
                        orderItemData.append("    " + productSecondLine + "\n");
                    }
                }


                for (int j = 0; j < data.getMenu().get(i).getOptions().getSize().getSizemodifiers().size(); j++) {
                    // List<SizeModifierProduct> sizeModifierProducts = data.getMenu().get(i).getOptions().getSizeBeans().getSizemodifiers().get(j).getSizeModifierProducts();
                    for (int k = 0; k < data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().size(); k++) {

                       /* LayoutInflater inflaterSizeModi = LayoutInflater.from(context);
                        View rootSizeModi = inflaterSizeModi.inflate(R.layout.order_list_item_modifiers, null);
                        TextView titleModi = rootSizeModi.findViewById(R.id.title);
                        TextView priceModi = rootSizeModi.findViewById(R.id.price);

                        titleModi.setText(data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getProductName());
                        priceModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getAmount());
                 */       // orderItemListView.addView(rootSizeModi);


                        if (data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k) != null) {
                            String productModiProductName = data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getProductName();
                            if (productModiProductName.length() > (charCountNew * 2)) {
                                productFirstLine = productModiProductName.substring(0, (charCountNew));
                                productSecondLine = productModiProductName.substring(charCountNew, ((charCountNew * 2) - 3)) + "... ";
                            } else {
                                if (productModiProductName.length() > charCountNew) {
                                    productFirstLine = productModiProductName.substring(0, (charCountNew));
                                    productSecondLine = productModiProductName.substring((charCountNew), productModiProductName.length());
                                } else {
                                    productFirstLine = productModiProductName;
                                    for (int b = 0; b < (charCountNew - productModiProductName.length()); b++) {
                                        productFirstLine += " ";
                                    }
                                }
                            }
                            if (productSecondLine == null) {
                                //String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                                String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getAmount());
                                // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                                orderItemData.append(
                                        productFirstLine
                                                + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getAmount())
                                                + "\n"
                                );
                            } else {
//                String _productName = createProductNameString(charCountNew, orderedItemsModel.getProductDetails().getProductName());
                                // String eachSpace = createPriceSpace(priceCharCount, orderedItemsModel.getCost());
                                String amountSpace = createPriceSpace(priceCharCount, data.getMenu().get(i).getPrice());
                                // String qty = createQtyString(qtyCharCount, orderedItemsModel.getProduct_qty());
                                orderItemData.append(productFirstLine

                                        + String.format(amountSpace + context.getResources().getString(R.string.currency) + "%.2f", data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getAmount())
                                        + "\n"
                                );
                                orderItemData.append("    " + productSecondLine + "\n");
                            }
                        }
                    }


                }

            }

        }

        return orderItemData.toString();
    }



   /* private static String makeCartData(Context context, NewDetailBean.OrdersDetailsBean.Cart data, int charCount) {
        int qtyCharCount = 4;
        int priceCharCount = 7;
        int charCountNew = charCount - qtyCharCount;
        charCountNew = charCountNew - priceCharCount;
        int charCountName=20;
        StringBuilder str = new StringBuilder();
        Log.e("3333333333", "3333333333333");


        *//*--------------Menu Item----------------------------------------*//*
            for (int i = 0; i < data.getMenu().size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View root = inflater.inflate(R.layout.order_list_item, null);
                TextView title = root.findViewById(R.id.title);
                TextView price = root.findViewById(R.id.price);
                // Menu menu = data.getMenu().get(i);
                title.setText(data.getMenu().get(i).getQty() + "x " + data.getMenu().get(i).getName());
                price.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getPrice());

                *//*--------------Menu Product Modifiers----------------------------------------*//*
                //orderItemListView.addView(root);

                if (data.getMenu().get(i).getOptions().getProductModifiers() != null) {
                    //  List<ProductModifier> productModifiers = data.getMenu().get(i).getOptions().getProductModifiers();
                    for (int j = 0; j < data.getMenu().get(i).getOptions().getProductModifiers().size(); j++) {
                        for (int k = 0; k < data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().size(); k++) {
                            LayoutInflater inflaterModi = LayoutInflater.from(context);
                            View rootModi = inflaterModi.inflate(R.layout.order_list_item_modifiers, null);
                            TextView titleModi = rootModi.findViewById(R.id.title);
                            TextView priceModi = rootModi.findViewById(R.id.price);

                            titleModi.setText(data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getProductName());
                            priceModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getProductModifiers().get(j).getModifierProducts().get(k).getModifierProductPrice());
                            //orderItemListView.addView(rootModi);
                        }
                    }
                }

                *//*--------------Menu Meal Products----------------------------------------*//*
                if (data.getMenu().get(i).getOptions().getMealProducts() != null) {
                    //List<MealProduct> mealProducts = data.getMenu().get(i).getOptions().getMealProducts();
                    for (int j = 0; j < data.getMenu().get(i).getOptions().getMealProducts().size(); j++) {
                        // List<SizeModifier> sizeModifiers = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers();
                        for (int k = 0; k < data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().size(); k++) {
                            //  List<SizeModifierProduct> sizeModifierProducts = data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts();
                            for (int l = 0; l < data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().size(); l++) {
                                LayoutInflater inflaterSizeModi = LayoutInflater.from(context);
                                View rootSizeModi = inflaterSizeModi.inflate(R.layout.order_list_item_modifiers, null);
                                TextView titleModi = rootSizeModi.findViewById(R.id.title);
                                TextView priceModi = rootSizeModi.findViewById(R.id.price);

                                titleModi.setText(data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getQuantity() + "x " + data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getProductName());
                                priceModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getMealProducts().get(j).getSizeModifiers().get(k).getSizeModifierProducts().get(l).getAmount());
                                //orderItemListView.addView(rootSizeModi);
                            }
                        }
                    }
                }
                *//*--------------Menu Size----------------------------------------*//*

                if (data.getMenu().get(i).getOptions().getSize() != null && data.getMenu().get(i).getOptions().getSize().getProductSizeName() != null) {

                    LayoutInflater sizeModiInflater = LayoutInflater.from(context);
                    View rootSize = sizeModiInflater.inflate(R.layout.order_list_item_modifiers, null);
                    TextView titleSizeModi = rootSize.findViewById(R.id.title);
                    TextView priceSizeModi = rootSize.findViewById(R.id.price);

                    titleSizeModi.setText(data.getMenu().get(i).getOptions().getSize().getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getProductSizeName());
                    priceSizeModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getSize().getProductSizePrice());
                  //  orderItemListView.addView(rootSize);

                    for (int j = 0; j < data.getMenu().get(i).getOptions().getSize().getSizemodifiers().size(); j++) {
                        // List<SizeModifierProduct> sizeModifierProducts = data.getMenu().get(i).getOptions().getSizeBeans().getSizemodifiers().get(j).getSizeModifierProducts();
                        for (int k = 0; k < data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().size(); k++) {

                            LayoutInflater inflaterSizeModi = LayoutInflater.from(context);
                            View rootSizeModi = inflaterSizeModi.inflate(R.layout.order_list_item_modifiers, null);
                            TextView titleModi = rootSizeModi.findViewById(R.id.title);
                            TextView priceModi = rootSizeModi.findViewById(R.id.price);

                            titleModi.setText(data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getQuantity() + "x " + data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getProductName());
                            priceModi.setText(String.valueOf(R.string.currency) + data.getMenu().get(i).getOptions().getSize().getSizemodifiers().get(j).getSizeModifierProducts().get(k).getAmount());
                           // orderItemListView.addView(rootSizeModi);
                        }


                    }
                }


            }







        *//*if (cart.getMenu() != null) {
            for (int m = 0; m < cart.getMenu().size(); m++) {
                Log.e("4444444444", "44444444444444444");

                if (cart.getMenu().get(m).getOptions().getSize() != null) {
                    Log.e("55555555", "55555555555555");

                    for (int z = 0; z < cart.getMenu().get(m).getOptions().getSize().getSizemodifiers().size(); z++) {
                        Log.e("6666666666", "666666666666");

                        MenuProduct menuProduct = cart.getMenu().get(m).getMenuProducts().get(z);
                        int itemQty = menuProduct.getOriginalQuantity();
                        String productName = "";
                        Double price = 0d;
                        if (menuProduct.getMenuProductSize() != null && menuProduct.getMenuProductSize().size() > 0) {
                            productName = (menuProduct.getMenuProductSize().get(0).getProductSizeName() + " " + menuProduct.getProductName());
                            price = (itemQty * Double.parseDouble(menuProduct.getMenuProductSize().get(0).getProductSizePrice()));

                        } else {
                            productName = menuProduct.getProductName().trim();
                            price = (itemQty * Double.parseDouble(menuProduct.getMenuProductPrice()));
                        }

                        if (productName.length()>charCountNew){
                            productName=AddressNameCorrect(productName);
                            int restSpaceCount=(charCountName*2)-productName.length();

                            for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                productName=productName+" ";
                            }
                            String priceSpace = createPriceSpace(priceCharCount, price);
                            str.append(itemQty + "x " + productName + String.format(priceSpace + context.getResources().getString(R.string.currency) + "%.2f", price) + "\n");

                        }else {
                            productName = createProductNameString(charCountNew, productName);
                            String priceSpace = createPriceSpace(priceCharCount, price);
                            str.append(itemQty + "x " + productName + String.format(priceSpace + context.getResources().getString(R.string.currency) + "%.2f", price) + "\n");

                        }

                        if (menuProduct.getMenuProductSize() != null) {
                            Log.e("77777777777", "77777777777777");

                            for (MenuProductSize menuProductSize1 : menuProduct.getMenuProductSize()) {
                                if (menuProductSize1.getSelected()) {
                                    for (SizeModifier sizeModifier : menuProductSize1.getSizeModifiers()) {

                                        if (sizeModifier.getModifierType().equalsIgnoreCase("free")) {
                                            int maxAllowFree = sizeModifier.getMaxAllowedQuantity();
                                            int free = 0;
                                            for (int i = 0; i < sizeModifier.getModifier().size(); i++) {
                                                int qty = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                                qty = (qty * itemQty);
                                                String _productName = "";
                                                Double _price = 0d;
                                                String _priceSpace = "";
                                                if (free == maxAllowFree) {

                                                    _productName=sizeModifier.getModifier().get(i).getProductName().trim();
                                                    if (_productName.length()>charCountNew){
                                                        _productName=AddressNameCorrect(_productName);
                                                        int restSpaceCount=(charCountName*2)-_productName.length();

                                                        for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                            _productName=_productName+" ";
                                                        }
                                                    }else {
                                                        _productName="";
                                                        _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                    }

                                                    _price = (qty * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()));
                                                    _priceSpace = createPriceSpace(priceCharCount, _price);
                                                    str.append(qty + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");

                                                } else {
                                                    int qtyy = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                                    if (qtyy >= maxAllowFree) {
                                                        int nQty = qtyy - maxAllowFree;
                                                        free = maxAllowFree;
                                                        int _qtyy = (nQty * itemQty);

                                                        if (nQty == 0) {
                                                        ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                        ((TextView) view.findViewById(R.id.tv_price)).setText("Free");

                                                            _productName=sizeModifier.getModifier().get(i).getProductName().trim();
                                                            if (_productName.length()>charCountNew){
                                                                _productName=AddressNameCorrect(_productName);
                                                                int restSpaceCount=(charCountName*2)-_productName.length();

                                                                for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                    _productName=_productName+" ";
                                                                }
                                                            }else {
                                                                _productName="";
                                                                _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                            }
                                                            _price = 0.00d;
                                                            _priceSpace = createPriceSpace(priceCharCount, _price);
                                                            str.append(qty + "x " + _productName + String.format(_priceSpace + "Free") + "\n");

                                                        } else if (nQty > 0) {
                                                            _productName=sizeModifier.getModifier().get(i).getProductName().trim();
                                                            if (_productName.length()>charCountNew){
                                                                _productName=AddressNameCorrect(_productName);
                                                                int restSpaceCount=(charCountName*2)-_productName.length();

                                                                for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                    _productName=_productName+" ";
                                                                }
                                                            }else {
                                                                _productName="";
                                                                _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                            }

                                                            _price = (_qtyy * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()));
                                                            _priceSpace = createPriceSpace(priceCharCount, _price);
                                                            str.append(_qtyy + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");


                                                            _productName="";
                                                            _productName = sizeModifier.getModifier().get(i).getProductName().trim();
                                                            if (_productName.length()>charCountNew){
                                                                _productName=AddressNameCorrect(_productName);
                                                                int restSpaceCount=(charCountName*2)-_productName.length();

                                                                for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                    _productName=_productName+" ";
                                                                }
                                                            }else {
                                                                _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                            }

                                                            _price = 0.00d;
                                                            _priceSpace = createPriceSpace(priceCharCount, _price);
                                                            str.append(maxAllowFree + "x " + _productName + String.format(_priceSpace + "Free") + "\n");
                                                        }

                                                    } else {
                                                        _productName="";
                                                        _productName = sizeModifier.getModifier().get(i).getProductName().trim();
                                                        if (_productName.length()>charCountNew){
                                                            _productName=AddressNameCorrect(_productName);
                                                            int restSpaceCount=(charCountName*2)-_productName.length();

                                                            for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                _productName=_productName+" ";
                                                            }
                                                        }else {
                                                            _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                        }

                                                        _price = 0.00d;
                                                        _priceSpace = createPriceSpace(priceCharCount, _price);
                                                        str.append(qty + "x " + _productName + String.format(_priceSpace + "Free") + "\n");
                                                        free++;
                                                    }
                                                }
                                            }
                                        } else {
                                            for (Modifier modifier : sizeModifier.getModifier()) {
                                                int qty = Integer.parseInt(modifier.getOriginalQuantity());
                                                qty = (qty * itemQty);
                                                String _productName = "";
                                                Double _price = 0d;
                                                String _priceSpace = "";

                                                _productName = modifier.getProductName().trim();
                                                if (_productName.length()>charCountNew){
                                                    _productName=AddressNameCorrect(_productName);
                                                    int restSpaceCount=(charCountName*2)-_productName.length();

                                                    for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                        _productName=_productName+" ";
                                                    }
                                                }else {

                                                    _productName = createProductNameString(charCountNew, modifier.getProductName().trim());
                                                }


                                                _price = (qty * Double.parseDouble(modifier.getModifierProductPrice()));
                                                _priceSpace = createPriceSpace(priceCharCount, _price);
                                                str.append(qty + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (menuProduct.getProductModifiers() != null) {
                            Log.e("88888888", "8888888888888");

                            for (ProductModifier productModifier : menuProduct.getProductModifiers()) {
                                String _productName = "";
                                Double _price = 0d;
                                String _priceSpace = "";
                                if (productModifier.getModifierType().equalsIgnoreCase("free")) {
                                    int maxAllowFree = productModifier.getMaxAllowedQuantity();
                                    int free = 0;
                                    for (int i = 0; i < productModifier.getModifier().size(); i++) {
                                        int qty = Integer.parseInt(productModifier.getModifier().get(i).getOriginalQuantity());
                                        qty = (qty * itemQty);


                                        _productName=productModifier.getModifier().get(i).getProductName().trim();
                                        if (_productName.length()>charCountNew){
                                            _productName=AddressNameCorrect(_productName);
                                            int restSpaceCount=(charCountName*2)-_productName.length();

                                            for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                _productName=_productName+" ";
                                            }
                                        }else {

                                            _productName = createProductNameString(charCountNew, productModifier.getModifier().get(i).getProductName().trim());
                                        }

                                        if (free == maxAllowFree) {
                                            _price = (qty * Double.parseDouble(productModifier.getModifier().get(i).getModifierProductPrice()));
                                            _priceSpace = createPriceSpace(priceCharCount, _price);
                                            str.append(qty + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");

                                        } else {
                                            int qtyy = Integer.parseInt(productModifier.getModifier().get(i).getOriginalQuantity());
                                            if (qtyy > maxAllowFree) {
                                                int nQty = qtyy - maxAllowFree;
                                                free = maxAllowFree;
                                                int _qtyy = (nQty * itemQty);

                                                _price = (_qtyy * Double.parseDouble(productModifier.getModifier().get(i).getModifierProductPrice()));
                                                _priceSpace = createPriceSpace(priceCharCount, _price);
                                                str.append(_qtyy + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");


                                                _price = 0.00d;
                                                _priceSpace = createPriceSpace(priceCharCount, _price);
                                                str.append(maxAllowFree + "x " + _productName + String.format(_priceSpace + "Free") + "\n");
                                            } else {
                                                _price = 0.00d;
                                                _priceSpace = createPriceSpace(priceCharCount, _price);
                                                str.append(maxAllowFree + "x " + _productName + String.format(_priceSpace + "Free") + "\n");
                                                free++;
                                            }
                                        }

                                    }
                                } else {
                                    for (Modifier modifier : productModifier.getModifier()) {
                                        int qty = Integer.parseInt(modifier.getOriginalQuantity());
                                        qty = (qty * itemQty);


                                        _productName =  modifier.getProductName().trim();

                                        if (_productName.length()>charCountNew){
                                            _productName=AddressNameCorrect(_productName);
                                            int restSpaceCount=(charCountName*2)-_productName.length();

                                            for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                _productName=_productName+" ";
                                            }
                                        }else {

                                            _productName = createProductNameString(charCountNew, modifier.getProductName().trim());
                                        }
                                        _price = (qty * Double.parseDouble(modifier.getModifierProductPrice()));
                                        _priceSpace = createPriceSpace(priceCharCount, _price);
                                        str.append(qty + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");

                                    }
                                }
                            }
                        }

                        if (menuProduct.getMealProducts() != null) {
                            Log.e("9999999999999", "999999999999999");

                            for (int p = 0; p < menuProduct.getMealProducts().size(); p++) {

                                String _productName1 = menuProduct.getMealProducts().get(p).getProductName()+ " " + menuProduct.getMealProducts().get(p).getProductSizeName();

                                if (_productName1.length()>charCountNew){
                                    _productName1=AddressNameCorrect(_productName1);
//                                    int restSpaceCount=(charCountNew*2)-_productName1.length();
//
//                                    for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
//                                        _productName1=_productName1+" ";
//                                    }
                                    str.append(_productName1 + "\n");

                                }else {

                                    str.append(createProductNameString(charCountNew, (menuProduct.getMealProducts().get(p).getProductName() + " " + menuProduct.getMealProducts().get(p).getProductSizeName())) + "\n");
                                }


                                if (menuProduct.getMealProducts().get(p).getMenuProductSize() != null && menuProduct.getMealProducts().get(p).getMenuProductSize().size() > 0) {
                                    for (MenuProductSize menuProductSize1 : menuProduct.getMealProducts().get(p).getMenuProductSize()) {
                                        if (menuProductSize1.getSelected()) {
                                            for (SizeModifier sizeModifier : menuProductSize1.getSizeModifiers()) {
                                                if (sizeModifier.getModifierType().equalsIgnoreCase("free")) {
                                                    int maxAllowFree = sizeModifier.getMaxAllowedQuantity();
                                                    int free = 0;
                                                    for (int i = 0; i < sizeModifier.getModifier().size(); i++) {
                                                        int qty = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                                        qty = (qty * itemQty);
                                                        String _productName = "";
                                                        Double _price = 0d;
                                                        String _priceSpace = "";
                                                        if (free == maxAllowFree) {

                                                            _productName = sizeModifier.getModifier().get(i).getProductName().trim();

                                                            if (_productName.length()>charCountNew){
                                                                _productName=AddressNameCorrect(_productName);
                                                                int restSpaceCount=(charCountName*2)-_productName.length();

                                                                for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                    _productName=_productName+" ";
                                                                }
                                                            }else {

                                                                _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                            }
                                                            _price = (qty * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()));
                                                            _priceSpace = createPriceSpace(priceCharCount, _price);
                                                            str.append(qty + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");

                                                        } else {
                                                            int qtyy = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                                            if (qtyy >= maxAllowFree) {
                                                                int nQty = qtyy - maxAllowFree;
                                                                free = maxAllowFree;
                                                                int _qtyy = (nQty * itemQty);

                                                                if (nQty == 0) {

                                                                    _productName = sizeModifier.getModifier().get(i).getProductName().trim();

                                                                    if (_productName.length()>charCountNew){
                                                                        _productName=AddressNameCorrect(_productName);
                                                                        int restSpaceCount=(charCountName*2)-_productName.length();

                                                                        for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                            _productName=_productName+" ";
                                                                        }
                                                                    }else {

                                                                        _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                                    }
                                                                    _price = 0.00d;
                                                                    _priceSpace = createPriceSpace(priceCharCount, _price);
                                                                    str.append(qty + "x " + _productName + String.format(_priceSpace + "Free") + "\n");

                                                                } else if (nQty > 0) {
                                                                    _productName = sizeModifier.getModifier().get(i).getProductName().trim();

                                                                    if (_productName.length()>charCountNew){
                                                                        _productName=AddressNameCorrect(_productName);
                                                                        int restSpaceCount=(charCountName*2)-_productName.length();

                                                                        for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                            _productName=_productName+" ";
                                                                        }
                                                                    }else {

                                                                        _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                                    }

                                                                    _price = (_qtyy * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()));
                                                                    _priceSpace = createPriceSpace(priceCharCount, _price);
                                                                    str.append(_qtyy + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");


                                                                    _productName = sizeModifier.getModifier().get(i).getProductName().trim();

                                                                    if (_productName.length()>charCountNew){
                                                                        _productName=AddressNameCorrect(_productName);
                                                                        int restSpaceCount=(charCountName*2)-_productName.length();

                                                                        for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                            _productName=_productName+" ";
                                                                        }
                                                                    }else {

                                                                        _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                                    }
                                                                    _price = 0.00d;
                                                                    _priceSpace = createPriceSpace(priceCharCount, _price);
                                                                    str.append(maxAllowFree + "x " + _productName + String.format(_priceSpace + "Free") + "\n");
                                                                }

                                                            } else {

                                                                _productName = sizeModifier.getModifier().get(i).getProductName().trim();

                                                                if (_productName.length()>charCountNew){
                                                                    _productName=AddressNameCorrect(_productName);
                                                                    int restSpaceCount=(charCountName*2)-_productName.length();

                                                                    for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                                                        _productName=_productName+" ";
                                                                    }
                                                                }else {
                                                                    _productName = createProductNameString(charCountNew, sizeModifier.getModifier().get(i).getProductName().trim());
                                                                }
                                                                _price = 0.00d;
                                                                _priceSpace = createPriceSpace(priceCharCount, _price);
                                                                str.append(qty + "x " + _productName + String.format(_priceSpace + "Free") + "\n");
                                                                free++;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    for (Modifier modifier : sizeModifier.getModifier()) {
                                                        int qty = Integer.parseInt(modifier.getOriginalQuantity());
                                                        qty = (qty * itemQty);
                                                        String _productName = "";
                                                        Double _price = 0d;
                                                        String _priceSpace = "";

                                                        _productName =  modifier.getProductName().trim();

                                                        if (_productName.length()>charCountNew){
                                                            _productName=AddressNameCorrect(_productName);
//                                                            if (_productName.length()>charCountNew){
//                                                                _productName=ItemLengthCheck(_productName);
//                                                            }
                                                            int restSpaceCount=(charCountName*2)-_productName.length();

                                                            for (int s=0;s<restSpaceCount;s++){
                                                                _productName=_productName+" ";
                                                            }

                                                        }else {
                                                            _productName = createProductNameString(charCountNew, modifier.getProductName().trim());
                                                        }
                                                        _price = (qty * Double.parseDouble(modifier.getModifierProductPrice()));
                                                        _priceSpace = createPriceSpace(priceCharCount, _price);
                                                        str.append(qty + "x " + _productName + String.format(_priceSpace + context.getResources().getString(R.string.currency) + "%.2f", _price) + "\n");

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                if (cart.getMenu().getMenuCategory().get(m).getMenuSubCategory() != null) {
                    Log.e("aaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaa");

                    for (int j = 0; j < cart.getMenu().getMenuCategory().get(m).getMenuSubCategory().size(); j++) {

                        for (int i = 0; i < cart.getMenu().getMenuCategory().get(m).getMenuSubCategory().get(j).getMenuProducts().size(); i++) {
                            MenuProduct menuProduct = cart.getMenu().getMenuCategory().get(m).getMenuSubCategory().get(j).getMenuProducts().get(i);
                            int itemQty = menuProduct.getOriginalQuantity();
                            String productName = "";
                            Double price = 0d;
                            if (menuProduct.getMenuProductSize() != null && menuProduct.getMenuProductSize().size() > 0) {
                                productName = (menuProduct.getMenuProductSize().get(0).getProductSizeName() + " " + menuProduct.getProductName());
                                price = (itemQty * Double.parseDouble(menuProduct.getMenuProductSize().get(0).getProductSizePrice()));
                            } else {
                                productName = (menuProduct.getProductName());
                                price = (itemQty * Double.parseDouble(menuProduct.getMenuProductPrice()));
                            }

                            if (productName.trim().length()>charCountNew){
                                productName=AddressNameCorrect(productName.trim());

                                int restSpaceCount=(charCountName*2)-productName.length();

                                for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                                    productName=productName+" ";
                                }
                            }else {
                                productName = createProductNameString(charCountNew, productName.trim());
                            }
                            String priceSpace = createPriceSpace(priceCharCount, price);
                            str.append(itemQty + "x " + productName + String.format(priceSpace + context.getResources().getString(R.string.currency) + "%.2f", price) + "\n");
                        }

                    }
                }
                if (cart.getMenu().getMenuCategory().get(m).getMeal() != null) {
                    for (int i = 0; i < cart.getMenu().getMenuCategory().get(m).getMeal().size(); i++) {
                        Meal meal = cart.getMenu().getMenuCategory().get(m).getMeal().get(i);


                    }
                }
            }
        }

        if (cart.getMenu().getSpecialOffers() != null) {
            for (SpecialOffer specialOffers : cart.getMenu().getSpecialOffers()) {
                String productName = specialOffers.getOfferTitle().trim();

                if (productName.length()>charCountNew){
                    productName=AddressNameCorrect(productName);
                    int restSpaceCount=(charCountName*2)-productName.length();

                    for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                        productName=productName+" ";
                    }
                }else {
                    productName = createProductNameString(charCountNew,  specialOffers.getOfferTitle().trim());
                }

                Double price = (specialOffers.getQuantity() * Double.parseDouble(specialOffers.getOfferPrice()));
                String priceSpace = createPriceSpace(priceCharCount, price);
                str.append(specialOffers.getQuantity() + "x " + productName + String.format(priceSpace + context.getResources().getString(R.string.currency) + "%.2f", price) + "\n");
            }
        }

        if (cart.getMenu().getUpSellProducts() != null) {
            for (UpSells upSells : cart.getMenu().getUpSellProducts()) {
                String productName = upSells.getProductName().trim();
                Double price = (Integer.parseInt(upSells.getQuantity()) * upSells.getProductPrice());
                String priceSpace = createPriceSpace(priceCharCount, price);

                if (productName.length()>charCountNew){
                    productName=AddressNameCorrect(productName);
                    int restSpaceCount=(charCountName*2)-productName.length();

                    for (int s=0;s<restSpaceCount+(qtyCharCount*2);s++){
                        productName=productName+" ";
                    }
                }else {
                    productName = createProductNameString(charCountNew, upSells.getProductName().trim());
                }

                str.append(upSells.getQuantity() + "x " + productName + String.format(priceSpace + context.getResources().getString(R.string.currency) + "%.2f", price) + "\n");
            }
        }*//*

        Log.e("bbbbbbbbbbbb", str.toString());

        return str.toString();
    }*/

    private static String createPriceSpace(int priceCharCount, Double price) {
        String priceSpace = "";
        if (Constants.decimalFormat(price).length() < (priceCharCount - 1)) {
            for (int j = 0; j < ((priceCharCount - 1) - String.valueOf(Constants.decimalFormat(price)).length()); j++) {
                priceSpace += " ";
            }
        }

        try {
            priceSpace = priceSpace.substring(0, priceSpace.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return priceSpace;
    }

   /* private static String createProductNameString(int charCountNew, String _productName) {
        String productName = _productName;
        int totalProductNameLength = productName.length();
        if (charCountNew < totalProductNameLength) {
            productName = productName.substring(0, (charCountNew - 3));
            productName += "...";
        } else {
            for (int j = 0; j < (charCountNew - (totalProductNameLength - 1)) - 1; j++) {
                productName += " ";
            }
        }

//        //change by Adarsh
//        if (charCountNew < totalProductNameLength) {
//            String FirstHalf=productName.substring(0,20);
//            String SecondHalf=productName.substring(21,totalProductNameLength);
//            productName=FirstHalf+"\n"+SecondHalf;
//        } else {
//            for (int j = 0; j < (charCountNew - (totalProductNameLength - 2)); j++) {
//                productName += " ";
//            }
//        }
        return productName;
    }

    public static String ItemLengthCheck(String LengthCheck) {
        String ItemLengthCheckS = "";
        int charCountAddress = 21, spacec = 0;

        for (int i = 0; i < charCountAddress; i++) {
            char c = LengthCheck.charAt(i);
            if (c == ' ') {
                spacec = i;
            }
        }
        String first = LengthCheck.substring(0, spacec);
        String second = LengthCheck.substring(spacec + 1, LengthCheck.length());
        ItemLengthCheckS = first + "\n   " + second;

        return ItemLengthCheckS;
    }*/

    public static String AddressNameCorrect(String Address) {
        String AddressToPrint = "", FirstAdd = "", SecondAdd = "", ThirdAdd = "", FourthAdd = "";
        int charCountAddress = 19;

        ArrayList<String> addressStrings = new ArrayList<>();
        addressStrings.clear();
        int pickChar = 0;

        try {
            Address = Address + " ";
            for (int j = 0; j < Address.length(); j++) {
                char spaceS = Address.charAt(j);
                if (spaceS == ' ' || spaceS == ',') {
                    String aadd = Address.substring(pickChar, j);
                    addressStrings.add(aadd.trim());
                    pickChar = j + 1;
                }

            }

            String AddressToPrintCheck = "";
            for (int k = 0; k < addressStrings.size(); k++) {

                //AddressToPrintCheck=AddressToPrintCheck+" "+addressStrings.get(k);
                try {
                    if (FirstAdd.length() > charCountAddress) {

                        try {
                            if (SecondAdd.length() > charCountAddress) {
                                try {
                                    if (ThirdAdd.length() > charCountAddress) {
                                        FourthAdd = FourthAdd + " " + addressStrings.get(k);
                                    } else {
                                        ThirdAdd = ThirdAdd + " " + addressStrings.get(k);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                SecondAdd = SecondAdd + " " + addressStrings.get(k);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        FirstAdd = FirstAdd + " " + addressStrings.get(k);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


//            try{
//                AddressToPrintCheck="";
//                AddressToPrintCheck=AddressToPrintCheck+addressStrings.get(k);
//
//                if (AddressToPrintCheck.length()>0){
//                    if (AddressToPrintCheck.length()>charCountAddress){
//
//                        try{
//                            AddressToPrintCheck="";
//                            AddressToPrintCheck=AddressToPrintCheck+addressStrings.get(k);
//
//                            if (AddressToPrintCheck.length()>0){
//                                if (AddressToPrintCheck.length()>charCountAddress){
//
//                                    FourthAdd=FourthAdd+" "+addressStrings.get(k);
//
//                                }else {
//                                    ThirdAdd=ThirdAdd+" "+addressStrings.get(k);
//                                }
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                    }else {
//                        SecondAdd=SecondAdd+" "+addressStrings.get(k);
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //58 Wake Green Road, B13 9PG, Birmingham, England   B13 9PG
        try {
            if (FirstAdd != null && FirstAdd.length() > 0) {
                AddressToPrint = FirstAdd.trim();

                if (SecondAdd != null && SecondAdd.length() > 0) {
                    AddressToPrint = AddressToPrint + "\n   " + SecondAdd.trim();

                    if (ThirdAdd != null && ThirdAdd.length() > 0) {
                        AddressToPrint = AddressToPrint + "\n   " + ThirdAdd.trim();

                        if (FourthAdd != null && FourthAdd.length() > 0) {
                            AddressToPrint = AddressToPrint + "\n   " + FourthAdd.trim();
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return AddressToPrint + " ";
    }

   /* private static String makeCartData(Context context, List<OrderDetailsResponse.OrderDetails.Cart> cart, int charCount) {
        int qtyCharCount = 4;
        int priceCharCount = 6;
        int charCountNew = charCount - qtyCharCount;
        charCountNew = charCountNew - priceCharCount;
        StringBuilder str = new StringBuilder();
//        str.append("\n");
        for (int m = 0; m < cart.size(); m++) {
            List<OrderDetailsResponse.OrderDetails.Cart.Items> cartData = cart.get(m).getItems();


            for (int i = 0; i < cartData.size(); i++) {
                String productName = cartData.get(i).getProduct_name().trim();
                int qty = Integer.parseInt(cartData.get(i).getProduct_qty());


                int totalProductNameLength = productName.length();
                if (charCountNew < totalProductNameLength) {
                    productName = productName.substring(0, (charCountNew - 3));
                    productName += "... ";
                } else {
                    for (int j = 0; j < (charCountNew - (totalProductNameLength - 1)); j++) {
                        productName += " ";
                    }
                }
                Double price = (qty * Double.parseDouble(cartData.get(i).getProduct_price()));

                String priceSpace = "";
                if (Constants.decimalFormat(price).length() < (priceCharCount - 1)) {
                    for (int j = 0; j < ((priceCharCount - 1) - String.valueOf(Constants.decimalFormat(price)).length()); j++) {
                        priceSpace += " ";
                    }
                }


                str.append(qty + "x " + productName + String.format(priceSpace + context.getResources().getString(R.string.currency) + "%.2f", price) + "\n");
                if (cartData.get(i).getModifiers() != null && cartData.get(i).getModifiers().size() > 0) {
                    for (int j = 0; j < cartData.get(i).getModifiers().size(); j++) {
                        int modifierQty = (qty * Integer.parseInt(cartData.get(i).getModifiers().get(j).getProduct_qty()));

                        Double modifierPrice = (modifierQty * Double.parseDouble(cartData.get(i).getModifiers().get(j).getProduct_price()));
                        String modifierPriceSpace = "";
                        if (Constants.decimalFormat(modifierPrice).length() < (priceCharCount - 1)) {
                            for (int k = 0; k < ((priceCharCount - 1) - Constants.decimalFormat(modifierPrice).length()); k++) {
                                modifierPriceSpace += " ";
                            }
                        }

                        String modifierName = cartData.get(i).getModifiers().get(j).getProduct_name().trim();
                        int totalModifierProductNameLength = modifierName.length();
                        int modifierStartSpace = charCountNew - 2;

                        if (modifierStartSpace < totalModifierProductNameLength) {
                            modifierName = modifierName.substring(0, (totalModifierProductNameLength - 8));
                            modifierName += "...";
                        } else {
                            for (int k = 0; k < (modifierStartSpace - (totalModifierProductNameLength - 1)); k++) {
                                modifierName += " ";
                            }
                        }

                        str.append("  " + modifierQty + "x " + modifierName + String.format(modifierPriceSpace + context.getResources().getString(R.string.currency) + "%.2f", modifierPrice) + "\n");

//                    modifierName += "(" + String.format(context.getResources().getString(R.string.currency) + "%.2f", modifierPrice) + ")";
//                    str.append("  " + modifierQty + "x " + modifierName + "\n");
                    }
                }
            }
        }

        return str.toString();
    }

    public static void printOrder(Bitmap logo, OrderDetailsResponse.OrderDetails orderDetail, Context activity) {
       *//* if (true){
            return;
        }*//*


        String restaurantName = Constants.getStoredData(activity).getRestaurant_name();
        String contact = Constants.getStoredData(activity).getPhone_number();
        String address = Constants.getStoredData(activity).getAddress();
        //get current date time with Date()
        Date date = new Date();
        Date time = new Date();
        String _date = dateFormat.format(date);
        String _time = timeFormat.format(time);

        String header = String.format("%s", restaurantName) + "\n"
                + String.format("%s", contact) + "\n"
                + String.format("%s", address) + "\n"
                + String.format("Date: %s", _date) + String.format("  Time: %s", _time) + "\n"
                + String.format("%s", "            Delivery            ") + "\n"
                + String.format("%s", "--------------------------------");

        int totalChar = 34;
        String orderId = String.format("Order ID: %s", orderDetail.getOrder_num());
        String orderDate = String.format("Order Date: %s", orderDetail.getOrder_date_time()) + "\n"
                + String.format("%s", "----------------------------------");
        String data = String.format("%s", "----------------------------------") + "\n"
                + String.format("%s", alignString(totalChar, "Payment Method", orderDetail.getPayment_mode())) + "\n"
//                + String.format("%s", alignString(totalChar, "Requested For", orderDetail.getDelivery_date_time())) + "\n"
                + String.format("%s", alignString(totalChar, "Sub total", "£" + orderDetail.getSub_total())) + "\n"
                + String.format("%s", alignString(totalChar, "Discount", "£" + orderDetail.getDiscount_amount())) + "\n"
                + String.format("%s", alignString(totalChar, "Delivery Charge", "£" + orderDetail.getDelivery_charge())) + "\n"
                + String.format("%s", alignString(totalChar, "Total amount", (orderDetail.getOrder_total() != null) ? "£" + orderDetail.getOrder_total() : "£")) + "\n"
                + String.format("%s", "----------------------------------") + "\n";

//        String item = createItem(totalChar, orderDetail.getCart().get(0).getItems());
        String item = createItem(totalChar, orderDetail.getCart());

        Log.e("PRINT>>\n", header + "\n" + orderId + "\n" + item + "\n" + orderDate + "\n" + data);

        if (AidlUtil.getInstance().isConnect()) {
            if (logo != null)
                AidlUtil.getInstance().printBitmap(logo);

            AidlUtil.getInstance().printText(header, 24, false, false, 1);
            AidlUtil.getInstance().printText(orderId, 22, false, false, 1);
            AidlUtil.getInstance().printText(orderDate, 22, false, false, 1);
            AidlUtil.getInstance().printText(item, 22, false, false, 0);
            AidlUtil.getInstance().printText(data, 22, false, false, 1);


            try {
                AidlUtil.getInstance().printBitmap(Constants.encodeAsBitmap(orderDetail.getOrder_num(), BarcodeFormat.CODABAR, 250, 80));
            } catch (WriterException e) {
                e.printStackTrace();
            }

            AidlUtil.getInstance().printText("\n\n           Thank You            ", 22, false, false, 5);

        } else {
            Message msg = new Message();
            msg.obj = "Printer not attached";
            Toast.makeText(activity, "Printer not attached", Toast.LENGTH_SHORT).show();
        }
    }*/

    public static void printOrder1(Bitmap logo, OrderDetailsResponse.OrderDetails orderDetail, Context activity) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mma");

//        String restaurantName = Constants.getStoredData(activity).getRestaurant_name();
//        String contact = Constants.getStoredData(activity).getPhone_number();
//        String address = Constants.getStoredData(activity).getAddress();
        //get current date time with Date()

        UserPreferences userPreferences = UserPreferences.getUserPreferences();
        LoginResponse.Data ss = (LoginResponse.Data) userPreferences.getResponse(activity, Constants.LOGIN_RESPONSE);

        String restaurantName = ss.getRestaurant_name();
        String address = ss.getAddress();
        String contact = ss.getPhone_number();


        Date date = new Date();
        Date time = new Date();
        String _date = dateFormat.format(date);
        String _time = timeFormat.format(time);

        String header = String.format("%s", restaurantName) + "\n"
                + String.format("%s", contact) + "\n"
                + String.format("%s", address) + "\n"
                + String.format("Date: %s", _date) + String.format("  Time: %s", _time) + "\n"
                + String.format("%s", "            Delivery            ") + "\n"
                + String.format("%s", "--------------------------------");

        String footer = String.format("%s", "        Delivery Details        ") + "\n"
                + String.format("%s", "--------------------------------") + "\n"
                + String.format("%s", orderDetail.getDelivery_address().getCustomer_name()) + "\n"
                + String.format("%s", orderDetail.getDelivery_address().getPhone_number()) + "\n"
                + String.format("%s", orderDetail.getDelivery_address().getCustomer_location()) + "\n"
                + String.format("Delivery Time:%s", orderDetail.getDelivery_date_time()) + "\n";

        int totalChar = 34;
        String orderId = String.format("Order ID: %s", orderDetail.getOrder_num());
        String orderDate = String.format("Order Date: %s", orderDetail.getOrder_date_time()) + "\n"
                + String.format("%s", "----------------------------------");
        String data;
        if (orderDetail.getDelivery_option().toUpperCase().equals("DELIVERY")) {
            data = String.format("%s", "----------------------------------") + "\n"
                    + String.format("%s", alignString(totalChar, "Payment Method", orderDetail.getPayment_mode())) + "\n"
//                + String.format("%s", alignString(totalChar, "Requested For", orderDetail.getDelivery_date_time())) + "\n"
                    + String.format("%s", alignString(totalChar, "Sub total", "£" + orderDetail.getSub_total())) + "\n"
                    + String.format("%s", alignString(totalChar, "Discount", "£" + orderDetail.getDiscount_amount())) + "\n"
                    + String.format("%s", alignString(totalChar, "Delivery Charge", "£" + orderDetail.getDelivery_charge())) + "\n"
                    + String.format("%s", alignString(totalChar, "Total amount", (orderDetail.getOrder_total() != null) ? "£" + orderDetail.getOrder_total() : "£")) + "\n"
                    + String.format("%s", "----------------------------------") + "\n";
        } else {
            data = String.format("%s", "----------------------------------") + "\n"
                    + String.format("%s", alignString(totalChar, "Payment Method", orderDetail.getPayment_mode())) + "\n"
//                + String.format("%s", alignString(totalChar, "Requested For", orderDetail.getDelivery_date_time())) + "\n"
                    + String.format("%s", alignString(totalChar, "Sub total", "£" + orderDetail.getSub_total())) + "\n"
                    + String.format("%s", alignString(totalChar, "Discount", "£" + orderDetail.getDiscount_amount())) + "\n"
                    + String.format("%s", alignString(totalChar, "Total amount", (orderDetail.getOrder_total() != null) ? "£" + orderDetail.getOrder_total() : "£")) + "\n"
                    + String.format("%s", "----------------------------------") + "\n";
        }


//        String item = createItem(totalChar, orderDetail.getCart().get(0).getItems());
        String item = createItem(totalChar, orderDetail.getCart());

        Log.e("PRINT>>\n", header + "\n" + orderId + "\n" + item + "\n" + orderDate + "\n" + data);
        if (AidlUtil.getInstance().isConnect()) {
            if (logo != null)
                AidlUtil.getInstance().printBitmap(logo);

            AidlUtil.getInstance().printText(header, 24, false, false, 1);
            AidlUtil.getInstance().printText(orderId, 22, false, false, 1);
            AidlUtil.getInstance().printText(orderDate, 22, false, false, 1);
            AidlUtil.getInstance().printText(item, 22, true, false, 0);
            AidlUtil.getInstance().printText(data, 22, false, false, 1);
            AidlUtil.getInstance().printText(alignString(totalChar, "Note", ""), 22, false, false, 1);
            AidlUtil.getInstance().printText(orderDetail.getOrder_notes() + "", 20, false, false, 1);

            AidlUtil.getInstance().printText(footer, 22, true, false, 1);
            if (orderDetail.getOrder_notes() != null && orderDetail.getOrder_notes().trim().length() > 0) {
                AidlUtil.getInstance().printText("NOTES:", 24, true, false, 1);
//                AidlUtil.getInstance().printText("Please knock the door as the\ndoor bell is broken", 24, true, false, 1);
                AidlUtil.getInstance().printText(orderDetail.getOrder_notes(), 24, false, false, 1);
                AidlUtil.getInstance().printText(line24Bold, 24, true, false, 1);
            }
            try {
                AidlUtil.getInstance().printBitmap(Constants.encodeAsBitmap(orderDetail.getOrder_num(), BarcodeFormat.CODABAR, 400, 80));
            } catch (WriterException e) {
                e.printStackTrace();
            }

            AidlUtil.getInstance().printText("\n\n           Thank You            ", 22, true, false, 5);

        } else {
            Message msg = new Message();
            msg.obj = "Printer not attached";
            Toast.makeText(activity, "Printer not attached", Toast.LENGTH_SHORT).show();
        }
    }

    private static String createItem(int totalChar, List<OrderDetailsResponse.OrderDetails.Cart> cartItems) {
        StringBuilder data = new StringBuilder();
        int _totalChar = totalChar - 17;

        for (OrderDetailsResponse.OrderDetails.Cart _item : cartItems) {
            for (OrderDetailsResponse.OrderDetails.Cart.Items item : _item.getItems()) {
                String _name = item.getProduct_name();
                String _qty = item.getProduct_qty();
                String _price = String.format("%.2f", Float.parseFloat(item.getProduct_price()));
                String _amount = item.getTotal_amount();
                Log.e("IS > 1111", "" + (_name.toString().length() > _totalChar));
                if (_name.toString().length() > _totalChar) {
                    _name = _name.substring(0, (_totalChar - 4));
                    _name += "...";
                } else {
                    int loopCount = (_totalChar - _name.toString().length());
                    Log.e("22222222", _totalChar + " > " + _name.toString().length() + " > " + loopCount);
                    for (int i = 1; i < loopCount; i++) {
                        _name += " ";
                    }
                }

                if (_price.toString().length() < 5) {
                    int spaceCount = (5 - _price.toString().length());
                    for (int i = 0; i < spaceCount; i++) {
                        _price += " ";
                    }
                }

                totalChar = (totalChar - _amount.toString().length());
                data.append(_qty + "x " + _name + " " + "£" + _price + "  " + "£" + _amount + "\n");
            }
        }
        return data.toString();
    }

    private static String alignString(int totalChar, String title, String contant) {
        StringBuilder data = new StringBuilder();
        int remainingChar = (totalChar - title.toString().length());
        if (contant != null)
            remainingChar = (remainingChar - contant.length());
        data.append(title);
        for (int i = 0; i < remainingChar; i++) {
            data.append(" ");
        }
        if (contant != null)
            data.append(contant);
        return data.toString();
    }

 /*   public static void printCompletedOrder(Bitmap logo, OrderDetailsResponse.OrderDetails orderDetail, Context activity) {

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm a");
        //get current date time with Date()
        Date date = new Date();
        Date time = new Date();
        String Date = dateFormat.format(date);
        String Time = timeFormat.format(time);

        UserPreferences userPreferences = UserPreferences.getUserPreferences();
        LoginResponse.Data ss = (LoginResponse.Data) userPreferences.getResponse(activity, Constants.LOGIN_RESPONSE);

        String restaurantName = ss.getRestaurant_name();
        String address = ss.getAddress();
        String contact = ss.getPhone_number();
        if (AidlUtil.getInstance().isConnect()) {
            if (logo != null)
                AidlUtil.getInstance().printBitmap(getResizedBitmap(logo, 40));

            AidlUtil.getInstance().setFont(" gh ");
//            AidlUtil.getInstance().printText(bill, 20, false, false, 3);
//            AidlUtil.getInstance().printBarCode("8968626227", 8, 80, 2, 1);

            AidlUtil.getInstance().printText(restaurantName, 22, true, false, 1);
            AidlUtil.getInstance().printText(contact, 22, true, false, 1);
            AidlUtil.getInstance().printText(address, 22, true, false, 1);
            AidlUtil.getInstance().printText("OrdersDetailsTop: " + Date + " " + "Time: " + Time, 22, true, false, 1);

            AidlUtil.getInstance().printText("            Delivery            ", 20, true, false, 1);
            AidlUtil.getInstance().printText("--------------------------------", 24, true, false, 1);
            AidlUtil.getInstance().printText("Order ID     " + orderDetail.getOrder_num(), 20, true, false, 1);
            AidlUtil.getInstance().printText("Order Date   " + orderDetail.getOrder_date_time().substring(0, orderDetail.getOrder_date_time().length() - 3) + "", 20, true, false, 1);
            AidlUtil.getInstance().printText("--------------------------------", 24, true, false, 1);
            AidlUtil.getInstance().printText("Order Type                " + orderDetail.getDelivery_option(), 20, true, false, 1);
            AidlUtil.getInstance().printText("ORF          " + orderDetail.getDelivery_date_time().substring(0, orderDetail.getDelivery_date_time().length() - 3) + "", 20, true, false, 1);
            AidlUtil.getInstance().printText("Payment Method                   " + orderDetail.getPayment_mode() + "", 20, true, false, 1);

            AidlUtil.getInstance().printText("Items                         GBP(£)", 20, true, false, 1);
            AidlUtil.getInstance().printText("                       Each    Total", 20, true, false, 1);


            for (int i = 0; i < orderDetail.getCart().size(); i++) {
                AidlUtil.getInstance().printText(orderDetail.getCart().get(i).getCategoryName() + "                           ", 22, true, false, 1);
                if (orderDetail.getCart().get(i).getItems() != null && orderDetail.getCart().get(i).getItems().size() > 0) {
                    for (int j = 0; j < orderDetail.getCart().get(i).getItems().size(); j++) {
                        AidlUtil.getInstance().printText(orderDetail.getCart().get(i).getItems().get(j).getProduct_qty() + "x" + (orderDetail.getCart().get(i).getItems().get(j).getProduct_name().length() < 10 ? orderDetail.getCart().get(i).getItems().get(j).getProduct_name().substring(0, 12) + "" : orderDetail.getCart().get(i).getItems().get(j).getProduct_name()) + "    " + Constants.POUND + orderDetail.getCart().get(i).getItems().get(j).getProduct_price() + "     " + Constants.POUND + orderDetail.getCart().get(i).getItems().get(j).getTotal_amount() + "", 20, true, false, 1);

                    }
                }
            }


            AidlUtil.getInstance().printText("--------------------------------", 24, true, false, 1);

            AidlUtil.getInstance().printText("Sub total               " + orderDetail.getSub_total(), 24, true, false, 1);
            AidlUtil.getInstance().printText("Delivery Charges        " + orderDetail.getDelivery_charge(), 24, true, false, 1);
            AidlUtil.getInstance().printText("Discount                " + orderDetail.getDiscount_amount(), 24, true, false, 1);
            AidlUtil.getInstance().printText("Total amount            " + orderDetail.getOrder_total() + "\n\n", 24, true, false, 1);
            AidlUtil.getInstance().printText("Note                                ", 24, true, false, 1);
            AidlUtil.getInstance().printText(orderDetail.getOrder_notes() + "", 20, false, false, 1);

            AidlUtil.getInstance().printText("Delivery Details                         ", 24, true, false, 1);
            AidlUtil.getInstance().printText("" + orderDetail.getDelivery_address().getCustomer_name() + "                ", 20, true, false, 1);
            AidlUtil.getInstance().printText("" + orderDetail.getDelivery_address().getPhone_number() + "            ", 20, true, false, 1);
            AidlUtil.getInstance().printText("" + orderDetail.getDelivery_address().getCustomer_location() + "        ", 20, false, false, 1);

            try {
                AidlUtil.getInstance().printBitmap(Constants.encodeAsBitmap(orderDetail.getOrder_num(), BarcodeFormat.CODE_128, 140, 50));
            } catch (WriterException e) {
                e.printStackTrace();
            }
            ;

            AidlUtil.getInstance().printText("                   THANK YOU!               \n\n", 24, true, false, 1);
            AidlUtil.getInstance().printText("  ", 24, true, false, 3);


        } else {
            Message msg = new Message();
            msg.obj = "Printer not attached";
            Toast.makeText(activity, "Printer not attached", Toast.LENGTH_SHORT).show();
        }


//        String Header =
//
//
//                "Date: " + Date + "     Time: " + Time + "\n"
//
//                        + "---------------------------------------------------------\n"
//
//                        + "S.No     Name                    Qty   Each       Amount\n"
//
//                        + "---------------------------------------------------------\n";
//
//        Float tax = 0.0f;
//
//        String bill = Header;
//        int i = 1;


        *//*for (int j = 0; j <orderDetail.getCart().size() ; j++) {

        }
        for (OrderDetailsResponse.OrderDetails.Cart  orderedItemsModel : orderDetail.getCart()) {
            String name = new String();
            String qty = new String();
            String each = new String();
            String discount = new String();
            String priceBatch = new String();

            if (orderedItemsModel.getCategoryName().length() > 24)
            {
                name = orderedItemsModel.getCategoryName().substring(0, 24) + "   ";
            } else {
                int length = 24 - orderedItemsModel.getCategoryName().length();
                name = orderedItemsModel.getCategoryName();
                for (int j = 0; j <= length; j++) {
                    name += " ";
                }
                name = name + "  ";
            }

            for (int j = 0; j <orderDetail.getCart().get() ; j++) {

            }


            if (Integer.parseInt(orderedItemsModel.getProduct_qty()) <= 5) {
                int length = 5 - Integer.parseInt(orderedItemsModel.getProduct_qty());
                qty = orderedItemsModel.getProduct_qty();
                for (int j = 0; j <= length; j++) {
                    qty += " ";
                }
            }
            if (String.valueOf(orderedItemsModel.getCost()).length() <= 7) {
                int length = 7 - String.valueOf(orderedItemsModel.getCost()).length();
                each = String.valueOf(orderedItemsModel.getCost());
                String spaces = new String();
                for (int j = 0; j <= length; j++) {
                    spaces += " ";
                }
                each = String.format(Locale.getDefault(), "%.2f%s ", Float.valueOf(each), spaces);
            }
            if (String.valueOf(orderedItemsModel.getNet_amount()).length() <= 7) {
                int length = 6 - String.valueOf(orderedItemsModel.getNet_amount()).length();
                priceBatch = String.valueOf(orderedItemsModel.getNet_amount());
                for (int j = 0; j <= length; j++) {
                    priceBatch += " ";
                }

            }

            if (String.valueOf(i).length() < 5) {
                int length = 5 - String.valueOf(i).length();
                String srNo = String.valueOf(i);

                for (int j = 0; j <= length; j++) {
                    srNo += " ";
                }
                String items =
                        srNo + name + qty +
                                each
//                                String.format(Locale.getDefault(), "%.2f", Float.parseFloat(each))
                                +
                                String.format(Locale.getDefault(), "%.2f", Float.parseFloat(priceBatch))
                                +
                                "\n";
                System.out.print(items);
                bill = bill + items;
            }


            i++;
        }*//*



     *//*String amt =
                "\n \n \n"
                        + padLeftSpaces("Sub total =", Double.parseDouble()) + "\n"
                        + padLeftSpaces("Discount =", Double.parseDouble() + "\n"
                        + padLeftSpaces("Total amount =", Double.parseDouble()));

        bill = bill + amt;
        System.out.println(bill);*//*
//        if (AidlUtil.getInstance().isConnect()) {
//            if (logo != null)
//                AidlUtil.getInstance().printBitmap(logo);
//            AidlUtil.getInstance().setFont(" gh ");
//            AidlUtil.getInstance().printText(bill, 20, false, false, 3);
////            AidlUtil.getInstance().printBarCode("8968626227", 8, 80, 2, 1);
//
//
//        } else {
//            Message msg = new Message();
//            msg.obj = "Printer not attached";
//            Toast.makeText(activity, "Printer not attached", Toast.LENGTH_SHORT).show();
//        }
    }

    public static void printBarcode(String barcodeText, Bitmap headerBitmap, Bitmap footerBitmap) {
        if (AidlUtil.getInstance().isConnect()) {
            if (headerBitmap != null)
                AidlUtil.getInstance().printBitmap(headerBitmap);

            AidlUtil.getInstance().printBarCode(barcodeText, 8, 80, 2, 1, false);
            AidlUtil.getInstance().printQr(barcodeText, 8, 8);

            if (footerBitmap != null)
                AidlUtil.getInstance().printBitmap(footerBitmap);
            AidlUtil.getInstance().cutPaper();
        }
    }*/

/*
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private static String padLeftSpaces(String LeftStr, String rightStr, int totalChar) {
        int totalSpaces = totalChar - LeftStr.length();
        totalSpaces = totalSpaces - rightStr.length();
        String str = "";
        for (int i = 0; i < totalSpaces; i++) {
            str += " ";
        }
//        String s = LeftStr+str+rightStr;
        return LeftStr + str + rightStr;
    }


    public static String padLeftSpaces(String trailingHeader, Float str) {
//        return String.format("%1$" + calculateSpaces(String.valueOf(str) + trailingHeader) + "s", trailingHeader + String.format(Locale.getDefault(), "£%.2f", str));
        return String.format("%1$" + 55 + "s", trailingHeader + String.format(Locale.getDefault(), "£%.2f", str));
    }

    public static String padLeftSpaces(String trailingHeader, Double str) {
//        return String.format("%1$" + calculateSpaces(String.valueOf(str) + trailingHeader) + "s", trailingHeader + String.format(Locale.getDefault(), "£%.2f", str));
        return String.format("%1$" + 55 + "s", trailingHeader + String.format(Locale.getDefault(), "£%.2f", str));
    }*/
}