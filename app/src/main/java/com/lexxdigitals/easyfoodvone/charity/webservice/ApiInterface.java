package com.lexxdigitals.easyfoodvone.charity.webservice;


import com.google.gson.JsonObject;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CharityInfoBean;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.CommonResponseBean;
import com.lexxdigitals.easyfoodvone.charity.webservice.responsebean.NewDetailBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiInterface {

    @POST(ApiRequestUrl.CHARITY_DETAIL_URL)
    Call<CharityInfoBean> getCharityDetail(@Body JsonObject jsonObject);

    @POST(ApiRequestUrl.DONATE_MEAL_URL)
    Call<CommonResponseBean> donateMeal(@Body JsonObject jsonObject);

    @POST(ApiRequestUrl.UPDATE_MEAL_REQUEST_URL)
    Call<CommonResponseBean> updateMealStatus(@Body JsonObject jsonObject);


    @POST(ApiRequestUrl.ORDER_DETAIL_URL)
    Call<NewDetailBean> getOrderDetail(@Body JsonObject jsonObject);
}
