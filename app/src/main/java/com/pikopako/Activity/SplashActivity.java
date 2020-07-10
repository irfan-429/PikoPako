package com.pikopako.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.ConnectivityReceiver;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SplashActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT = 2000;
    ArrayList<String> permissionToAsk = new ArrayList<>();
    public static String InternetDate = "";
    public static Date DeviceTime;

    public static ConnectivityReceiver connectivityReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        checkConnectivity();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                getDateAndTime();
//
//            }
//        }).start();
//
//        callInitApi();

    }


    private void checkConnectivity() {
//        if (!AppGlobalFunctions.isInternetConnected(context)) {
//            CustomAlertDialog.showAlertDialog(context, R.drawable.ic_arab_red, "Network Connection Error", "Please connect to the internet then try again later.", "OK", "", Splash.this, "InternetFailed");
//        } else
        if (!UiHelper.isGPSConnected(this)) {
            showDialogForGPS();
        } else {
            startConnectivityReceiver();

            new Thread(new Runnable() {
                @Override
                public void run() {


                    if (BaseApplication.getInstance().getSession().getInternetTime() == null) {
                        getDateAndTime();
                    } else {
                        if (BaseApplication.getInstance().getSession().getInternetTime()
                                .equals(formatDateTimeFromTS(System.currentTimeMillis(), "dd MMM yyyy"))) {
                            callInitApi();
                        } else getDateAndTime();

                    }

                }
            }).start();

        }
    }


    private void startConnectivityReceiver() {
        if (connectivityReceiver == null) {
            connectivityReceiver = new ConnectivityReceiver(); //creating instance
            IntentFilter filter = new IntentFilter();
//            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE"); //internet connectivity
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION); //GPS connectivity
            getApplicationContext().registerReceiver(connectivityReceiver, filter); //registering by whole app
        }
    }


    private void callInitApi() {
        CheckUserIsLoginOrNot();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //
//            }
//        }, SPLASH_TIME_OUT);

    }

    private void getDateAndTime() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = httpclient.execute(new HttpGet("https://google.com/"));

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                String dateStr = response.getFirstHeader("Date").getValue();
                //Here I do something with the Date String

                InternetDate = formatDateTimeFromString(dateStr, "EEE, dd MMM yyyy HH:mm:ss zzz", "dd MMM yyyy");

                BaseApplication.getInstance().getSession().setInternetTime(InternetDate);

                DeviceTime = Calendar.getInstance().getTime();
                System.out.println("Current time from device => " + DeviceTime);
                System.out.println("current time from internet =>" + InternetDate);

                callInitApi();


//                System.out.println(dateStr);

            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }


    private void CheckUserIsLoginOrNot() {
        Intent intent = new Intent();
        if (BaseApplication.getInstance().getSession().isLoggedIn()) {
            try {
                JSONObject jsonObject = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
//                if (jsonObject.getString("latitude").equalsIgnoreCase("null") && BaseApplication.getInstance().getSession().getDeliveryLatitude().isEmpty()) {
//                    intent = new Intent(SplashActivity.this, ConfirmLocationActivity.class);
//                    intent.putExtra(Constant.IS_SIGNUP, true);
//                } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else {
            intent = new Intent(SplashActivity.this, LocationOptionActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
    }
    /*private void askPermissions(){
        permissionToAsk.clear();
        for (String s : Constant.askForPermission)
        {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED)
                permissionToAsk.add(s);
        }
        if (!permissionToAsk.isEmpty())
            ActivityCompat.requestPermissions((Activity) this, permissionToAsk.toArray(new String[permissionToAsk.size()]), Constant.requestcodeForPermission);
        else{
            CheckUserIsLoginOrNot();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToAsk.clear();
        if (requestCode==Constant.requestcodeForPermission){
            boolean allGranted=true;
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_DENIED)
                    allGranted=false;
            }
            if (allGranted){
                CheckUserIsLoginOrNot();
            }
            else {
                finish();
            }
        }
    }*/


    private void showDialogForGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gpsTitle));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.gpsMessage));
        builder.setPositiveButton(getResources().getString(R.string.settingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                finish();
                Log.e("tag", "delivery_address :" + BaseApplication.getInstance().getSession().getDeliveryAddress());

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public String formatDateTimeFromString(String inputString, String inputStringFormat, String outputFormat) {
        SimpleDateFormat format = new SimpleDateFormat(inputStringFormat);
        Date newDate = null;
        try {
            newDate = format.parse(inputString);

        } catch (
                ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat(outputFormat);

        return format.format(newDate);
    }

    public String formatDateTimeFromTS(long timestamp, String outputFormat) {
        Date time = new Date(timestamp);
        DateFormat outPut = new SimpleDateFormat(outputFormat);
        //Hear Define your returning date formate
        return outPut.format(time);
    }


}


