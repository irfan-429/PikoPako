package com.pikopako.LocalStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.pikopako.AppUtill.Constant;

import org.json.JSONObject;



public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(Constant.PREFS, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getUsername() {
        String flag = pref.getString(Constant.KEY_USERNAME, "");
        editor.commit();
        return flag;
    }


    public int getID() {
        int flag = pref.getInt(Constant.KEY_ID, 0);
        editor.commit();
        return flag;
    }

    public void setID(String user_id) {
        editor.putString(Constant.KEY_ID, user_id);
        editor.commit();
    }



    //get QR code
    public void setUsername(String userName) {
        editor.putString(Constant.KEY_USERNAME, userName);
        editor.commit();

    }
    public void setCustomerProfileImage(String s) {
        editor.putString("customerUserImage",s);
        editor.commit();
    }
    public String getUserCustomerProfileImage() {
        String flag=pref.getString("customerUserImage","");
        editor.commit();
        return flag;
    }

    public Boolean isLoggedIn() {
        Boolean isLonged = pref.getBoolean(Constant.IS_LOGIN, false);
        return isLonged;

    }

    public void setIsLoggedIn() {
        editor.putBoolean(Constant.IS_LOGIN, true);
    }


    public String getProfileData() {
        String flag = pref.getString(Constant.PROFILE_DATA, "");
        editor.commit();
        return flag;
    }

    public void setProfileData(String data) {
        editor.putString(Constant.PROFILE_DATA, data);
        editor.commit();
    }

    public String getDeviceToken() {
        String flag = pref.getString("device_token", "");
        editor.commit();
        return flag;
    }

    public void setDeviceToken(String data) {
        editor.putString("device_token", data);
        editor.commit();
    }

    public void setToken(String data) {
        editor.putString(Constant.TOKEN, data);
        editor.commit();
    }

    public String getToken() {
        String flag = pref.getString(Constant.TOKEN, "");
        editor.commit();
        return flag;
    }

    public void setDeliveryAddress(String address) {
        editor.putString(Constant.DELIVERY_ADDRESS, address);
        editor.commit();
    }

    public String getDeliveryAddress() {
        String flag = pref.getString(Constant.DELIVERY_ADDRESS, "");
        editor.commit();
        return flag;
    }

    public void setDeliveryLatitude(String address) {
        editor.putString(Constant.DELIVERY_LATITUDE, address);
        editor.commit();
    }

    public String getDeliveryLatitude() {
        String flag = pref.getString(Constant.DELIVERY_LATITUDE, "");
        editor.commit();
        return flag;
    }
    public void setDeliveryLongitude(String address) {
        editor.putString(Constant.DELIVERY_LONGITUDE, address);
        editor.commit();
    }

    public String getDeliveryLongitude() {
        String flag = pref.getString(Constant.DELIVERY_LONGITUDE, "");
        editor.commit();
        return flag;
    }


    public void setAddress(String jsonObject){
        editor.putString("adres",jsonObject);
        editor.commit();
    }
    public String getAddress(){
        String flag=pref.getString("adres","");
        editor.commit();
        return flag;
    }
    //Cart Items
    public String getCartItems(){
        return pref.getString(Constant.CART_ITEM,"");
    }

    public void setCartItmes(String cartItmesJson){
        editor.putString(Constant.CART_ITEM,cartItmesJson);
        editor.commit();
    }

    //simplified
    public void setSimplifiedCartData(String simplifiedJson){
        editor.putString(Constant.Simplified,simplifiedJson);
        editor.commit();
    }

    public String getSimplifiedCartData(){
        return pref.getString(Constant.Simplified,"");
    }

    // clear all shared preference data after logout
    public void clearSession() {
        editor.clear();
        editor.commit();

    }

    public void setFilter(String bFilter){
        editor.putString("Filter",bFilter);
        editor.commit();
    }

    public String getFilter(){
        String flag=pref.getString("Filter","");
        return  flag;
    }

    public void setCoupon(float discount, String couponCOde){
        editor.putFloat("discount",discount);
        editor.putString("code",couponCOde);
        editor.commit();
    }
    public  float getCoupon(){
        float flag=pref.getFloat("discount",0);
        return flag;
    }
    public String getCouponCode(){
        String code=pref.getString("code","");
        return  code;
    }


    public void setRestroImage(String image){
        editor.putString("image",image);
        editor.commit();
    }

    public  String getRestroImage(){
        String image=pref.getString("image","");
        return image;
    }

    public void setRestroName(String name){
        editor.putString("name",name);
        editor.commit();
    }

    public  String getRestroName(){
        String image=pref.getString("name","");
        return image;
    }

    public void setRestroLocation(String location){
        editor.putString("location",location);
        editor.commit();
    }

    public  String getRestroLocation(){
        String image=pref.getString("location","");
        return image;
    }

    public void setRestroStatus(String status){
        editor.putString("status",status);
        editor.commit();
    }

    public  String getRestroStatus(){
        String image=pref.getString("status","");
        return image;
    }

    public void setRestroId(String restro_id){
        Log.e("Session", "setRestroId: "+restro_id );
        editor.putString("restro_id",restro_id);
        editor.commit();
    }

    public  String getRestroId(){
        String image=pref.getString("restro_id","");
        return image;
    }


    public void setCoordinateId(String coordinate_id){
        editor.putString("coordinate_id",coordinate_id);
        editor.commit();
    }

    public  String getCoordinateId(){
        String image=pref.getString("coordinate_id","");
        return image;
    }
    public void setExit(String bFilter){
        editor.putString("Exit",bFilter);
        editor.commit();
    }

    public String getExit(){
        String flag=pref.getString("Exit","");
        return  flag;
    }

    public void setDeliveryLatitudeSet(String address) {
        editor.putString("lat", address);
        editor.commit();
    }

    public String getDeliveryLatitudeSet() {
        String flag = pref.getString("lat", "");
        editor.commit();
        return flag;
    }
    public void setDeliveryLongitudeSet(String address) {
        editor.putString("longi", address);
        editor.commit();
    }

    public void setminimum_order_amount(String minimum_order_amount) {
        editor.putString("minimum_order_amount", minimum_order_amount);
        editor.commit();
    }

    public String getminimum_order_amount() {
        String flag = pref.getString("minimum_order_amount", "");
        editor.commit();
        return flag;
    }

    public void setPreviousLocationTitle(String address) {
        editor.putString("PREVIOUSLOCATIONTITLE", address);
        editor.commit();
    }

    public String getPreviousLocationTitle() {
        String flag = pref.getString("PREVIOUSLOCATIONTITLE", "");
        editor.commit();
        return flag;
    }

    public String getDeliveryLongitudeSet() {
        String flag = pref.getString("longi", "");
        editor.commit();
        return flag;
    }

    public void setFCMToken(String token) {
        editor.putString("fcmtoken", token);
        editor.commit();
    }

    public String getFCMToken() {
        return pref.getString("fcmtoken", "");
    }





    public void setIsToppingAvble ( boolean flag){
        editor.putBoolean("topping",flag);
        editor.commit();
    }

    public  boolean getIsToppingAvble(){
        boolean topping=pref.getBoolean("topping",false);
        return topping;
    }

    public void setInternetTime(String s) {
        editor.putString("time", s);
        editor.commit();
    }

    public String getInternetTime() {
        return pref.getString("time", null);
    }


}

