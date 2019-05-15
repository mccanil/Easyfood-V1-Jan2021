package com.lexxdigital.easyfoodvone.api_handler;

import com.lexxdigital.easyfoodvone.contact_support.models.SupportRequest;
import com.lexxdigital.easyfoodvone.contact_support.models.SupportResponse;
import com.lexxdigital.easyfoodvone.login.models.LoginRequest;
import com.lexxdigital.easyfoodvone.login.models.LoginResponse;
import com.lexxdigital.easyfoodvone.menu.CommonRequest;
import com.lexxdigital.easyfoodvone.menu.MenuCategoryList;
import com.lexxdigital.easyfoodvone.menu_details.models.MenuCategoryItemsResponse;
import com.lexxdigital.easyfoodvone.menu_details.models.MenuProductDetails;
import com.lexxdigital.easyfoodvone.models.AddNewTimingRequest;
import com.lexxdigital.easyfoodvone.models.AllDaysRestaurantTiming;
import com.lexxdigital.easyfoodvone.models.ChangePasswordRequest;
import com.lexxdigital.easyfoodvone.models.CreateComboOfferRequest;
import com.lexxdigital.easyfoodvone.models.DiscountWithPercentageRequest;
import com.lexxdigital.easyfoodvone.models.MenuProducts;
import com.lexxdigital.easyfoodvone.models.OffersResponse;
import com.lexxdigital.easyfoodvone.models.OrderReportRequest;
import com.lexxdigital.easyfoodvone.models.OrderReportResponse;
import com.lexxdigital.easyfoodvone.models.RatingRequest;
import com.lexxdigital.easyfoodvone.models.RatingResponse;
import com.lexxdigital.easyfoodvone.models.RevenueReportRequest;
import com.lexxdigital.easyfoodvone.models.RevenueReportResponse;
import com.lexxdigital.easyfoodvone.models.ServeStyleResponse;
import com.lexxdigital.easyfoodvone.models.SetServeStyleRequest;
import com.lexxdigital.easyfoodvone.models.UpdateMenuCategoryRequest;
import com.lexxdigital.easyfoodvone.models.UpdateMenuProductRequest;
import com.lexxdigital.easyfoodvone.models.DeletePostCodeDeliveryTimeRequest;
import com.lexxdigital.easyfoodvone.models.DeleverySetting;
import com.lexxdigital.easyfoodvone.models.DeliverySettingRequest;
import com.lexxdigital.easyfoodvone.models.UpdateMenuCategoryRequest;
import com.lexxdigital.easyfoodvone.models.UpdateMenuProductRequest;
import com.lexxdigital.easyfoodvone.models.UpdatePostCodeDeliveryTimeRequest;
import com.lexxdigital.easyfoodvone.new_order.models.AcceptRejectOrderRequest;
import com.lexxdigital.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigital.easyfoodvone.new_order.models.DeletePostCodeDeliveryTimeResponse;
import com.lexxdigital.easyfoodvone.new_order.models.DeleverySettingResponse;
import com.lexxdigital.easyfoodvone.new_order.models.DeliverySettingResponse;
import com.lexxdigital.easyfoodvone.new_order.models.OrderDetailsRequest;
import com.lexxdigital.easyfoodvone.new_order.models.OrderDetailsResponse;
import com.lexxdigital.easyfoodvone.new_order.models.UpdatePostCodeDeliveryTimeResponse;
import com.lexxdigital.easyfoodvone.orders.models.OrdersListResponse;
import com.lexxdigital.easyfoodvone.orders.models.OrdersRequest;
import com.lexxdigital.easyfoodvone.restaurant_models.RestaurantOpenCloseRequest;
import com.lexxdigital.easyfoodvone.restaurant_models.RestaurantOpenCloseResponse;
import com.lexxdigital.easyfoodvone.spend_x_get_x_discount.SpendXgetXdiscountRequest;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST(ApiConstants.LOGIN)
    Single<LoginResponse> login(@Body LoginRequest request);

    @POST(ApiConstants.LOGOUT)
    Single<CommonResponse> logout(@Body CommonRequest request);

    @POST(ApiConstants.RESTAURANT_OPEN_CLOSE)
    Single<RestaurantOpenCloseResponse> openCloseRestaurant(@Body RestaurantOpenCloseRequest request);

    @POST(ApiConstants.HOME_DELIVERY_OPTION)
    Single<RestaurantOpenCloseResponse> openCloseHomeDelivery(@Body RestaurantOpenCloseRequest request);

    @POST(ApiConstants.SET_SERVE_STYLE)
    Single<ServeStyleResponse> setServeStyle(@Body SetServeStyleRequest request);

    @POST(ApiConstants.GET_SERVE_STYLE)
    Single<ServeStyleResponse> getServerStyle(@Body SetServeStyleRequest request);

    @POST(ApiConstants.CONTACT_SUPPORT)
    Single<SupportResponse> contactSupport(@Body SupportRequest request);

    @POST(ApiConstants.ALL_ORDERS)
    Single<OrdersListResponse> getOrders(@Body OrdersRequest request);

    @POST(ApiConstants.GET_ORDER_DETAILS)
    Single<OrderDetailsResponse> getOrderDetails(@Body OrderDetailsRequest request);

    @POST(ApiConstants.FORGOT_PASSWORD)
    Single<LoginResponse> forgotPassword(@Body LoginRequest request);

    @POST(ApiConstants.ORDER_ACCEPT_REJECT)
    Single<CommonResponse> acceptRejectOrders(@Body AcceptRejectOrderRequest request);

    @POST(ApiConstants.SEND_EMAIL)
    Single<CommonResponse> sendEmail(@Body AcceptRejectOrderRequest request);

    @POST(ApiConstants.GET_ALL_MENU_CATEGORIES)
    Single<MenuCategoryList> getMenuCategories(@Body CommonRequest request);

    @POST(ApiConstants.GET_MENU_CATEGORY_ITEM)
    Single<MenuCategoryItemsResponse> getMenuCategoryItems(@Body CommonRequest request);

    @POST(ApiConstants.SET_MENU_CATEGORY_ITEM_POSITION)
    Single<MenuCategoryItemsResponse> getMenuCategoryItemsPositin(@Body CommonRequest request);

    @POST(ApiConstants.ACTIVE_DEACTIVE_MENU_PRODUCT)
    Single<MenuCategoryItemsResponse> activeDeactiveMenuProduct(@Body CommonRequest request);

    @POST(ApiConstants.ACTIVE_DEACTIVE_MENU)
    Single<CommonResponse> activeDeactiveMenu(@Body CommonRequest request);

    @POST(ApiConstants.UPDAET_MENU_CATEGORY)
    Single<CommonResponse> updateMenuCategory(@Body UpdateMenuCategoryRequest request);

    @POST(ApiConstants.UPDATE_MENU_PRODUCT)
    Single<CommonResponse> updateMenuProduct(@Body UpdateMenuProductRequest request);

    @POST(ApiConstants.GET_DELEVERY_SETTING)
    Single<DeleverySettingResponse> getDeleverySetting(@Body DeleverySetting deleverySetting);

    @POST(ApiConstants.UPDATE_POSTCODE_DELIVERY_TIME)
    Single<UpdatePostCodeDeliveryTimeResponse> updatePostCodeDeliveryTime(@Body UpdatePostCodeDeliveryTimeRequest updateMenuCategoryRequest);

    @POST(ApiConstants.DELETE_POSTCODE_DELIVERY_TIME)
    Single<DeletePostCodeDeliveryTimeResponse> deletePostCodeDeliveryTime(@Body DeletePostCodeDeliveryTimeRequest deletePostCodeDeliveryTimeRequest);
    /*@POST(ApiConstants.ACTIVE_DEACTIVE_MENU_PRODUCT)
    Single<MenuCategoryItemsResponse> activeDeactiveMenuProduct(@Body CommonRequest request);

    @POST(ApiConstants.ACTIVE_DEACTIVE_MENU)
    Single<CommonResponse> activeDeactiveMenu(@Body CommonRequest request);

    @POST(ApiConstants.UPDAET_MENU_CATEGORY)
    Single<CommonResponse> updateMenuCategory(@Body UpdateMenuCategoryRequest request);

    @POST(ApiConstants.UPDATE_MENU_PRODUCT)
    Single<CommonResponse> updateMenuProduct(@Body UpdateMenuProductRequest request);*/

    @POST(ApiConstants.OFFER_X_SPEND_X_DISCOUNT)
    Single<CommonResponse> createSpendXGetXDiscount(@Body SpendXgetXdiscountRequest request);

    @POST(ApiConstants.GET_RESTAURANT_TIMING)
    Single<AllDaysRestaurantTiming> getRestaurantTiming(@Body CommonRequest request);

    @POST(ApiConstants.ADD_NEW_TIMING)
    Single<CommonResponse> addNewTiming(@Body AddNewTimingRequest request);

    @POST(ApiConstants.UPDATE_ALL_POSTCODE_DELIVERY_TIME)
    Single<UpdatePostCodeDeliveryTimeResponse> updatAllPostCodeDelivery(@Body UpdatePostCodeDeliveryTimeRequest updatallCategoryRequest);

    @POST(ApiConstants.DELIVERY_SETTING)
    Single<DeliverySettingResponse> deliverySettingResponse(@Body DeliverySettingRequest deliverySettingRequest);

    @POST(ApiConstants.DELETE_TIMING)
    Single<CommonResponse> deleteTiming(@Body CommonRequest request);

    @POST(ApiConstants.UPDATE_TIMING)
    Single<CommonResponse> updateTiming(@Body AddNewTimingRequest request);

    @POST(ApiConstants.GET_REVENUE_REPORT)
    Single<RevenueReportResponse> getRevenueReport(@Body RevenueReportRequest request);

    @POST(ApiConstants.GET_REVENUE_REPORT_BY_DATE)
    Single<RevenueReportResponse> getRevenueReportByDate(@Body RevenueReportRequest request);

    @POST(ApiConstants.GET_REVENUE_REPORT_BETWEEN_DATE)
    Single<RevenueReportResponse> getRevenueReportBetweenDate(@Body RevenueReportRequest request);

    @POST(ApiConstants.GET_ORDER_REPORT)
    Single<OrderReportResponse> getOrderReport(@Body OrderReportRequest request);

    @POST(ApiConstants.GET_ORDER_REPORT_BY_DATE)
    Single<OrderReportResponse> getOrderReportByDate(@Body OrderReportRequest request);

    @POST(ApiConstants.GET_ORDER_REPORT_BETWEEN_DATE)
    Single<OrderReportResponse> getOrderReportBetweenDate(@Body OrderReportRequest request);

    @POST(ApiConstants.CHANGE_PASSWORD)
    Single<CommonResponse> changePassword(@Body ChangePasswordRequest request);

    @POST(ApiConstants.THANK_YOU)
    Single<CommonResponse> sendThankyou(@Body CommonRequest request);

    @POST(ApiConstants.REPLY)
    Single<CommonResponse> reply(@Body CommonRequest request);

    @POST(ApiConstants.GET_RATINGS)
    Single<RatingResponse> getRatings(@Body RatingRequest request);

    @POST(ApiConstants.GET_PRODUCTS_LIST)
    Single<MenuProducts> getProducts(@Body CommonRequest request);

    @POST(ApiConstants.CREATE_OFFER_WITH_PERCENTAGE)
    Single<CommonResponse> createOfferWithPercentage(@Body DiscountWithPercentageRequest request);

    @POST(ApiConstants.CREATE_COMBO_OFFER)
    Single<CommonResponse> createComboOffer(@Body CreateComboOfferRequest request);

    @POST(ApiConstants.CREATE_OFFER_WITH_AMT)
    Single<CommonResponse> createOfferWithAmt(@Body DiscountWithPercentageRequest request);

    @POST(ApiConstants.GET_OFFERS)
    Single<OffersResponse> getOffers(@Body CommonRequest request);

    @POST(ApiConstants.DELETE_OFFER)
    Single<CommonResponse> deleteOffer(@Body CommonRequest request);

    @POST(ApiConstants.GET_MENU_ITEM_DETAILS)
    Single<MenuProductDetails> getMenuDetails(@Body CommonRequest request);

    @POST(ApiConstants.UPDATE_MENU_ITEM_DETAILS)
    Single<CommonResponse> saveMenuDetails(@Body MenuProductDetails request);



}
