package com.lexxdigitals.easyfoodvone.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.restaurant_models.RestaurantOpenCloseRequest;
import com.lexxdigitals.easyfoodvone.restaurant_models.RestaurantOpenCloseResponse;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Constants {

    public static final String REGISTRATION_COMPLETE = "Reg_comp";
    public static final String MY_PREFERENCE = "preference";
    public static final String FIREBASE_TOKEN = "firebase_token";
    public static final String LOGIN_RESPONSE = "login_response";
    public static final String POUND = "£";
    public static final String ORDER_DETAIL = "order_detail";
    public static final String MY_PRERENCES = "preferense";
    public static final String RESTAURANT_LOGO = "restaurant_logo";
    public static final String ORDER_NUMBER = "orderNumber";
    public static DialogClickedListener dialogClicked;
    public static int ORDER_COUNT = 0;
    private static DecimalFormat decimalFormat = new DecimalFormat("#0.00");


    public interface DialogClickedListener {
        void onDialogClicked();

        void onDialogRejectClicked();
    }

    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String NOTIFICATION_TYPE_ACCEPTED = "new";
    public static final String NOTIFICATION_CHARITY_STATUS = "charity_status_update";
    public static final String NOTIFICATION_TYPE_CANCELED = "cancel";
    public static final String CHARITY_STATUS_INTENT = "charity_status_update";


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_actionbar);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    public static void switchActivity(Activity fromActivity, Class<?> toActivity) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
        fromActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }


    public static LoginResponse.Data getStoredData(Context context) {
        return (LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(context, Constants.LOGIN_RESPONSE);
    }

    public static Date getDateFromString(String date, String _format) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(_format);
        return format.parse(date);
    }


    public static void alertDialog(String msg, Activity activity, DialogClickedListener dialogClickedListener) {
        Constants.dialogClicked = dialogClickedListener;
        LayoutInflater factory = LayoutInflater.from(activity);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(activity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                dialogClicked.onDialogClicked();
            }
        });

        mDialog.show();
    }

    public static void alertDialogReject(String msg, Activity activity, DialogClickedListener dialogClickedListener) {
        Constants.dialogClicked = dialogClickedListener;
        LayoutInflater factory = LayoutInflater.from(activity);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(activity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                mDialog.dismiss();
                dialogClicked.onDialogRejectClicked();
            }
        });

        mDialog.show();
    }


    public static String decimalFormat(Double aDouble) {
        return decimalFormat.format(aDouble);
    }


    public static void dateSelector1(final TextView editText, final Activity activity) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        CharSequence strDate = null;
                        Time chosenDate = new Time();
                        chosenDate.set(dayOfMonth, monthOfYear, year);
                        long dtDob = chosenDate.toMillis(true);

                        strDate = DateFormat.format("yyyy-MM-dd", dtDob);

                        editText.setText(strDate);
                    }
                };

                DatePickerDialog d = new DatePickerDialog(activity, dpd, mYear, mMonth, mDay);
                d.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                d.show();

            }
        });


    }

    public static void endDateSelector(final TextView editText, final Activity activity, final String startDate) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                String myDate = startDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = sdf.parse(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long millis = date.getTime();


                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        CharSequence strDate = null;
                        Time chosenDate = new Time();
                        chosenDate.set(dayOfMonth, monthOfYear, year);
                        long dtDob = chosenDate.toMillis(true);

                        strDate = DateFormat.format("yyyy-MM-dd", dtDob);

                        editText.setText(strDate);
                    }
                };

                DatePickerDialog d = new DatePickerDialog(activity, dpd, mYear, mMonth, mDay);
                d.getDatePicker().setMinDate(millis - 1000);
                d.show();

            }
        });


    }

    public static void dateSelectorWithFormattedDate(final TextView editText, final Activity activity) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        CharSequence strDate = null;
                        Time chosenDate = new Time();
                        chosenDate.set(dayOfMonth, monthOfYear, year);
                        long dtDob = chosenDate.toMillis(true);

                        strDate = DateFormat.format("dd-MMM-yyyy", dtDob);

                        editText.setText(strDate);
                    }
                };

                DatePickerDialog d = new DatePickerDialog(activity, dpd, mYear, mMonth, mDay);
                d.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                d.show();

            }
        });
    }

    public static void dateSelectorWithFormattedDateForReport(final TextView editText, final Activity activity) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        CharSequence strDate = null;
                        Time chosenDate = new Time();
                        chosenDate.set(dayOfMonth, monthOfYear, year);
                        long dtDob = chosenDate.toMillis(true);

                        strDate = DateFormat.format("dd-MMM-yyyy", dtDob);

                        editText.setText(strDate);
                    }
                };

                DatePickerDialog d = new DatePickerDialog(activity, dpd, mYear, mMonth, mDay);
                d.show();

            }
        });
    }

    public static String getDateFromDateTime(String input, String formatFrom, String formatTo) {
        String strDate = null;
        SimpleDateFormat mFormatFrom = new SimpleDateFormat(formatFrom);
        SimpleDateFormat mFormatTo = new SimpleDateFormat(formatTo);
        if (null == input) {
            return null;
        }
        try {
            Date date = mFormatFrom.parse(input);
            strDate = mFormatTo.format(date);
        } catch (ParseException e) {
            strDate = input;
        }


        return strDate;
    }


    public static void selectTime(final TextView editText, final Context activity) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                editText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public static String getYesterdayDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getYesterdayDateString1() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static String getCurrentDateString1() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    //TODO: generating barcode
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static void showNewOrder(Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);

        if (dialog.isShowing())
            dialog.dismiss();

        Constants.ORDER_COUNT = Constants.ORDER_COUNT + 1;
        dialog.setContentView(R.layout.order_alert_dialog_layout);
        TextView txtmsg = dialog.findViewById(R.id.txtmsg);
        TextView feednow = dialog.findViewById(R.id.feednow);
        TextView later = dialog.findViewById(R.id.later);

        txtmsg.setText((ORDER_COUNT) + " New order arrived");
        feednow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Constants.ORDER_COUNT = 0;
                    }
                });

        if (!(activity.isFinishing())) {
            dialog.show();
        }


    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static String changeStringDateFormat(String input, String formatFrom, String formatTo) {
        String strDate = null;
        SimpleDateFormat mFormatFrom = new SimpleDateFormat(formatFrom);
        SimpleDateFormat mFormatTo = new SimpleDateFormat(formatTo);
        if (null == input) {
            return null;
        }
        try {
            Date date = mFormatFrom.parse(input);
            strDate = mFormatTo.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public static String getDayMonth(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date readDate = null;
        try {
            readDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayMonth = readDate.toString().substring(0, 10);

        return dayMonth;
    }


}
