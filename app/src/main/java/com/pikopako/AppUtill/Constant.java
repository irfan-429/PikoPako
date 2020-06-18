package com.pikopako.AppUtill;

import android.Manifest;



public class Constant {

    //Location update request
    public final static int UPDATE_TIME_IN_MINUTE = 2*1000;        //milliseconds*60=1 minute
    public final static int LOCATION_UPDATE_DISTANCE = 10;    //metres
    public final static int UPDATE_TIME_IN_MINUTEFORTIMER =1000*15;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_ID = "id";
    public static final String IS_LOGIN = "isLogin";
    public static final String PROFILE_DATA = "profile_data";
    public static final String TOKEN = "token";
    public static final String DELIVERY_ADDRESS = "delivery_address";
    public static final String DELIVERY_LATITUDE = "delivery_latitude";
    public static final String DELIVERY_LONGITUDE = "delivery_longitude";
    public static final String CART_ITEM = "Cart_items";
    public static String Simplified = "simplified";

    public static final String PREFS = "PrefSession";
    public static final String SUCCESS = "success";
    public static final String DIVICE_TYPE = "android";
    public static final String POS_DASHBOARD = "POS_DASHBOARD";
    public static final String POS_ACCOUNT = "POS_ACCOUNT";
    public static final String POS_CART = "POS_CART";
    public static final String POS_EXPLORER = "POS_EXPLORER";
    public static final String POS_NEAR_YOU = "POS_NEAR_YOU";
    public static final String POS_ABOUT_US = "POS_ABOUT_US";
    public static final String POS_TERMS_CONDITION = "POS_TERMS_CONDITION";
    public static final String POS_PRIVACY_POLICY = "POS_PRIVACY_POLICY";
    public static final String POS_CONTACT_US = "POS_CONTACT_US";
    public static final String POS_LOGOUT = "POS_LOGOUT ";
    public static final String BASE_URL = "http://pikopako.cellyclouds.com/api/customer/";
    public final static int requestcodeForPermission = 11;
    public static final String ADD_ERR = "Cant retrieve address";
    public static String GEOCODER_API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";


    public static final String CHANNEL_ID = "my_channel_01";
    public static final String CHANNEL_NAME = "Simplified Coding Notification";
    public static final String CHANNEL_DESCRIPTION = "www.simplifiedcoding.net";

    //intent key name
    public static final String IS_SIGNUP = "is_signup";
    public final static String [] askForLocationPermission = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    public final static String [] askForPermission = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static boolean isComingFromLogin=false;
    public static boolean isComingFromRegister=false;



}
