package com.pikopako.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.R;



public class DialogUtils extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        /*on the basis of intent value, show relative error dialog*/
        String connErrorType = getIntent().getExtras().getString("ConnErrorType"); //get activity intent
        if (connErrorType.equals("GPSFailed")) {
            showErrorDialog("GPS Connection Failed", "Please turn your location on then try again later.", "GPSFailed");
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
            finish();
        } else if (!isGpsEnabled && !isNetworkEnabled) {
            showErrorDialog("GPS Connection Failed", "Please turn your location on then try again later.", "GPSFailed");
        }

    }

    private void showErrorDialog(String errorTitle, String errorDesc, String errorType) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.gpsTitle));
            builder.setCancelable(false);
            builder.setMessage(getString(R.string.gpsMessage));
            builder.setPositiveButton(getResources().getString(R.string.settingButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());
//                    checkGPSConnectivity(context);

//                    finish();
                }
            });
//            builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
////                    BaseApplication.getInstance().getSession().setExit("Exit");
////                dialog.dismiss();
////                Intent intent= new Intent(LocationOptionActivity.this, LoginActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//
//
//                }
//            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkGPSConnectivity(context);

    }
}