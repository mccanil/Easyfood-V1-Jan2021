package com.lexxdigital.easyfoodvone.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.models.OrderReportResponse;
import com.lexxdigital.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigital.easyfoodvone.utility.Constants;
import com.lexxdigital.easyfoodvone.utility.UserPreferences;
import com.lexxdigital.easyfoodvone.utility.printerutil.AidlUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import woyou.aidlservice.jiuiv5.ICallback;

public class PrintEsayFood {
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


    public static void printOrder(Bitmap logo, OrderDetailsResponse.OrderDetails orderDetail, Activity activity) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mma");

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
    }

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
                + String.format("%s", orderDetail.getDelivery_address().getCustomer_address_id()) + "\n";

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
            AidlUtil.getInstance().printText(alignString(totalChar, "Note", ""), 22, false, false, 1);
            AidlUtil.getInstance().printText(orderDetail.getOrder_notes() + "", 20, false, false, 1);

            AidlUtil.getInstance().printText(footer, 24, false, false, 1);
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
    }

    private static String createItem(int totalChar, List<OrderDetailsResponse.OrderDetails.Cart> cartItems) {
        StringBuilder data = new StringBuilder();
        int _totalChar = totalChar - 17;

        for (OrderDetailsResponse.OrderDetails.Cart _item : cartItems) {
            for (OrderDetailsResponse.OrderDetails.Cart.Items item : _item.getItems()) {
                String _name = item.getProduct_name();
                String _qty = item.getProduct_qty();
                String _price = item.getProduct_price();
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

    public static void printCompletedOrder(Bitmap logo, OrderDetailsResponse.OrderDetails orderDetail, Context activity) {

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
            AidlUtil.getInstance().printText("Data: " + Date + " " + "Time: " + Time, 22, true, false, 1);

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


        /*for (int j = 0; j <orderDetail.getCart().size() ; j++) {

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
        }*/



        /*String amt =
                "\n \n \n"
                        + padLeftSpaces("Sub total =", Double.parseDouble()) + "\n"
                        + padLeftSpaces("Discount =", Double.parseDouble() + "\n"
                        + padLeftSpaces("Total amount =", Double.parseDouble()));

        bill = bill + amt;
        System.out.println(bill);*/
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
    }


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
    }
}
