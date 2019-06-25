package com.lexxdigitals.easyfoodvone.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferences {

    private static UserPreferences userPreferences = new UserPreferences();

    UserPreferences() {
    }

    public static UserPreferences getUserPreferences() {
        if (userPreferences != null) {
            return userPreferences;
        } else {
            return new UserPreferences();
        }
    }

    public SharedPreferences getSharedPref(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.MY_PRERENCES, Context.MODE_PRIVATE);
        return pref;
    }


    public void setString(Context mContext, String key, String value) {
        if (key != null) {
            SharedPreferences.Editor edit = getSharedPref(mContext).edit();
            edit.putString(key, value);
            edit.commit();
        }
    }

    public void setInt(Context mContext, String key, int value) {
        if (key != null) {
            SharedPreferences.Editor edit = getSharedPref(mContext).edit();
            edit.putInt(key, value);
            edit.commit();
        }
    }

    public void setLong(Context mContext, String key, Long value) {
        if (key != null) {
            SharedPreferences.Editor edit = getSharedPref(mContext).edit();
            edit.putLong(key, value);
            edit.commit();
        }
    }

    public void setBoolean(Context mContext, String key, boolean value) {
        if (key != null) {
            SharedPreferences.Editor edit = getSharedPref(mContext).edit();
            edit.putBoolean(key, value);
            edit.commit();
        }
    }

    public void storeByteArray(Context mContext, byte[] byteArray, String key) {
        SharedPreferences.Editor edit = getSharedPref(mContext).edit();
        edit.putString(key, Arrays.toString(byteArray));
        edit.commit();

    }

    public byte[] getByteArray(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        String stringArray = pref.getString(key, null);

        if (stringArray != null) {
            String[] split = stringArray.substring(1, stringArray.length() - 1).split(", ");
            byte[] array = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                array[i] = Byte.parseByte(split[i]);
            }

            return array;
        }
        return null;
    }

    public String getString(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        String val = null;
        try {
            if (pref.contains(key))
                val = pref.getString(key, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public int getInt(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        int val = 0;
        try {
            if (pref.contains(key)) val = pref.getInt(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public Long getLong(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        Long val = null;
        try {
            if (pref.contains(key)) val = pref.getLong(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public boolean getBoolean(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        boolean val = false;
        try {
            if (pref.contains(key)) val = pref.getBoolean(key, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }


    public boolean containkey(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        return pref.contains(key);
    }

    public void setObject(Context context, String key, List<Object> data) {
//Set the values
        Gson gson = new Gson();
        List<Object> textList = new ArrayList<>();
        textList.addAll(data);
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        String jsonText = gson.toJson(textList);
        edit.putString(key, jsonText);
        edit.apply();
    }

    public void setResponse(Context context, String key, LoginResponse.Data data) {
//Set the values
        Gson gson = new Gson();
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        String jsonText = gson.toJson(data);
        edit.putString(key, jsonText);
        edit.apply();


    }

    public Object[] getObject(Context context, String key) {
        //Retrieve the values
        Gson gson = new Gson();
        SharedPreferences pref = context.getSharedPreferences(Constants.MY_PRERENCES, Context.MODE_PRIVATE);
        String jsonText = pref.getString(key, null);
        return gson.fromJson(jsonText, Object[].class);  //EDIT: gso to gson
    }

    public Object getResponse(Context context, String key) {
        //Retrieve the values
        Gson gson = new Gson();
        SharedPreferences pref = context.getSharedPreferences(Constants.MY_PRERENCES, Context.MODE_PRIVATE);
        String jsonText = pref.getString(key, null);
        return gson.fromJson(jsonText, LoginResponse.Data.class);  //EDIT: gso to gson
    }

    public void clearPrefrences(Context context) {
        String key = getUserPreferences().getString(context, Constants.FIREBASE_TOKEN);
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PRERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear().apply();
        getUserPreferences().setString(context, Constants.FIREBASE_TOKEN, key);


    }

    public void setNotification(Context mContext, String key, boolean value) {
        if (key != null) {
            SharedPreferences.Editor edit = getSharedPref(mContext).edit();
            edit.putBoolean(key, value);
            edit.commit();
        }
    }

    public boolean getNotification(Context mContext, String key) {
        SharedPreferences pref = getSharedPref(mContext);
        boolean val = false;
        try {
            if (pref.contains(key)) val = pref.getBoolean(key, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }
}
