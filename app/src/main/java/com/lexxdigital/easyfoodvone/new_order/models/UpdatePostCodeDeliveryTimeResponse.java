package com.lexxdigital.easyfoodvone.new_order.models;

import com.lexxdigital.easyfoodvone.login.models.LoginResponse;

import java.io.Serializable;

public class UpdatePostCodeDeliveryTimeResponse implements Serializable {

    boolean success;
    String message;



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


}
