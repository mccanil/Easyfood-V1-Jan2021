package com.lexxdigitals.easyfoodvone.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantTimingRequest {

    @Expose
    @SerializedName("user_id")
    String user_id;

    @Expose
    @SerializedName("restaurant_id")
    String restaurant_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    @Override
    public String toString() {
        return "RestaurantTimingRequest{" +
                "user_id='" + user_id + '\'' +
                ", restaurant_id='" + restaurant_id + '\'' +
                '}';
    }
}
