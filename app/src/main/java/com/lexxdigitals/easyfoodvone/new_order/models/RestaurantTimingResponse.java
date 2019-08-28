package com.lexxdigitals.easyfoodvone.new_order.models;

import java.io.Serializable;

public class RestaurantTimingResponse implements Serializable {

    boolean success;
    String message;
    String data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
