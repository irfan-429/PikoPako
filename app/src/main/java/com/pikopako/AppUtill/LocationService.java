package com.pikopako.AppUtill;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by mukeshs on 18/5/18.
 */

public class LocationService extends Service {
    private LocationManager locationManager;
    private PowerManager.WakeLock wakeLock;
    private Location mLastLocation=null;
    private boolean isPersmissionGrant=false;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    private SharedPreferences permissionStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Google", "Service Created");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        stopSelf();
    }


    @Override
    public void onDestroy() {
        Log.e("aaa","stop service");

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        isPersmissionGrant=permissionStatus.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION,false);
        Log.e("Google", "Service Started"+isPersmissionGrant);

        try {
            Log.e("Google", "Service Started");
            mLastLocation  = null;
            Log.e("kkkk", "mLastLocation null");
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled&& !isNetworkEnabled) {
            }
            else {
                if(isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constant.UPDATE_TIME_IN_MINUTE,Constant.LOCATION_UPDATE_DISTANCE,listener);
                }
                if(isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constant.UPDATE_TIME_IN_MINUTE, Constant.LOCATION_UPDATE_DISTANCE,listener);
                }
            }
        }
        catch (Exception e){
            //  ActivityCompat.requestPermissions((ge, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            e.printStackTrace();
        }
    }
    private LocationListener listener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("Tag","service "+location.getLongitude());

        }
        @Override
        public void onProviderDisabled(String provider){
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    };
}
