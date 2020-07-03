package com.pikopako.AppUtill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.pikopako.Activity.DialogUtils;


public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (UiHelper.isAppOnForeground(context, "com.pikopako")) { //app is in foreground
            String action = intent.getAction();
            if (("android.net.conn.CONNECTIVITY_CHANGE").equals(action)) {
                checkInternetConnectivity(context);
            } else if (("android.location.PROVIDERS_CHANGED").equals(action)) {
                checkGPSConnectivity(context);
            }

        }
    }


    private void checkInternetConnectivity(Context context) {
        /*initializing ConnectivityManager, WIFI, Mobile network, GPS, network*/
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        /*check if wifi/mobile net connected or not*/
        if (wifi.isConnected() || mobile.isConnected()) {
            //if connected, Do something
        } else {
            showDialog(context, "InternetFailed");
        }

    }

    private void checkGPSConnectivity(Context context) {
        LocationManager locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = false;
        boolean isNetworkEnabled = false;

        try {
            isGpsEnabled = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }

        try {
            isNetworkEnabled = locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        if (isGpsEnabled && isNetworkEnabled) {
            //if connected, Do something
        } else if (!isGpsEnabled && !isNetworkEnabled) {
            showDialog(context, "GPSFailed");
        }

    }

    private void showDialog(Context context, String errorType) {
        Intent intent = new Intent(context, DialogUtils.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("ConnErrorType", errorType);
        context.startActivity(intent);
    }


}
