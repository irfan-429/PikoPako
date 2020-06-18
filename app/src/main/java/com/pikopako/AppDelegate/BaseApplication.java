package com.pikopako.AppDelegate;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.pikopako.AppUtill.Constant;
import com.pikopako.LocalStorage.SessionManager;
import com.splunk.mint.Mint;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    private static ApiInterface apiService,apiService1;
    public static SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

//        try {
//            ProviderInstaller.installIfNeeded(getApplicationContext());
//            SSLContext sslContext;
//            sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(null, null, null);
//            sslContext.createSSLEngine();
//        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
//                | NoSuchAlgorithmException | KeyManagementException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            ProviderInstaller.installIfNeeded(this);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }

        if(mInstance==null){
            mInstance=this;
        }
        if (apiService == null) {
            apiService = ApiClient.getClient().create(ApiInterface.class);
        }
        if (apiService1 == null) {
            apiService1 = ApiClient.getGeocodeRestInterface().create(ApiInterface.class);
        }
        Mint.initAndStartSession(this, "7d57b38d");
    }
    public static synchronized  BaseApplication getInstance(){
        return mInstance;
    }

    public ApiInterface getApiClient() {
        return apiService;
    }
    public ApiInterface getApiClient1() {
        return apiService1;
    }
    public SessionManager getSession() {
        if (sessionManager == null) {
            sessionManager = new SessionManager(getApplicationContext());
        }
        return sessionManager;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}