package com.pikopako.AppDelegate;
import com.google.gson.JsonObject;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by mukeshs on 15/9/17.
 */

public interface ApiInterface {

    @GET("json")
    Call<JsonObject> geocode(@Query("latlng") String latlng, @Query("key") String key);

    @POST("users/login")
    Call<JsonObject>login(@Header("token") String token, @Body JsonObject jsonObject);

    @POST("register/register")
    Call<JsonObject>register(@Body JsonObject jsonObject);

    @POST("register/register_without_password")
    Call<JsonObject>register_without_password(@Body JsonObject jsonObject);

    @POST("users/setDeliveryAddress")
    Call<JsonObject>setDeliveryAddress(@Header("token")  String token,@Body JsonObject jsonObject);


    @POST("users/forgot_password")
    Call<JsonObject> fotgotPassword(@Body JsonObject jsonObject);

    @POST("users/logout")
    Call<JsonObject> logout(@Header("token") String token,@Body JsonObject jsonObject);

    @GET("users/getUserDetail")
    Call<JsonObject> getUserDetail(@Header("token") String token);

    /*@POST("users/today_offer")
    Call<JsonObject> todayOffers(@Header("token") String token);

    @POST("users/total_orders")
    Call<JsonObject> totatlOffers(@Header("token") String token);

    @POST("users/wallet")
    Call<JsonObject> wallet(@Header("token") String token);

    @POST("users/oder_details")
    Call<JsonObject> orderDetails(@Header("token") String token,@Body JsonObject jsonObject);*/

    //edit profile
    @POST("users/editProfile")
    Call<JsonObject> editProfile(@Header("token") String token,@Body JsonObject jsonObject);


    //get ingredients
    @POST("food/getFoodDetail")
    Call<JsonObject> getIngredients(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/getRestaurant")
    Call<JsonObject> getRestaurantDetail(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("users/updateProfileImage/")
    Call<JsonObject> UpdateUserProfileImage(@Header("token") String token,@Body RequestBody requestBody);


    @POST("users/changePassword")
    Call<JsonObject> changePassword(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("users/contact_us")
    Call<JsonObject> contact_us(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/getRestaurantDetail")
    Call<JsonObject> getRestaurantServices(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("food/add_favourite")
    Call<JsonObject> addFavourite(@Header("token") String token,@Body JsonObject jsonObject);


    @POST("food/getFavouriteFoods")
    Call<JsonObject> getFavouriteFood(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/getDiscountCoupons")
    Call<JsonObject> getOfferDetail(@Header("token") String token,@Body JsonObject jsonObject);


    @POST("order/checkValidCoupon")
    Call<JsonObject> applyCoupon(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/getDiscountCoupons")
    Call<JsonObject> getAllCouponDetails(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/getRestaurantCategories")
    Call<JsonObject> getRestaurantCategories(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/add_rating")
    Call<JsonObject> addRating(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/paypal_payment")
    Call<JsonObject> apiPayPalPayment(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/skip_rating")
    Call<JsonObject> skip_rating(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/checkDeliveryLocation")
    Call<JsonObject> checkDeliveryLocation(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/getRestaurantReviews")
    Call<JsonObject> getReviews(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/add_cart")
    Call<JsonObject> addCart(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/getPastOrders")
    Call<JsonObject> getOrders(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/getOrders")
    Call<JsonObject> getOrderId(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/getPastOrderDetail")
    Call<JsonObject> getOrdersDetail(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("restaurant/checkRestaurantStatus")
    Call<JsonObject> checkRestaurantStatus(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("order/checkRatingPending")
    Call<JsonObject> checkRatingPending(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("register/guest_user_register")
    Call<JsonObject>postGuestUserRegister(@Body JsonObject jsonObject);

    @POST("register/verify_otp")
    Call<JsonObject>postVerifyOtp(@Body JsonObject jsonObject);

    @POST("users/verify_otp")
    Call<JsonObject>postVerifyOtpForRegister(@Body JsonObject jsonObject);

    @POST("users/resend_otp")
    Call<JsonObject>postResendOtpForRegister(@Body JsonObject jsonObject);

    @GET("users/getTotalDonatedWater")
    Call<JsonObject>getTotalDonatedWater(@Header("token") String token);

}
