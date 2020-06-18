package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.Constant;
import com.pikopako.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SplashActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT=2000;
    ArrayList<String> permissionToAsk=new ArrayList<>();
   public static String InternetTime="";
  public static  Date DeviceTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {

                getDateAndTime();

            }
        }).start();

        callInitApi();
    }

    private void callInitApi() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckUserIsLoginOrNot();
                //
            }
        }, SPLASH_TIME_OUT);

    }

    private void getDateAndTime() {

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet("https://google.com/"));
            org.apache.http.StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                InternetTime = response.getFirstHeader("Date").getValue();
//Here I do something with the Date String
                DeviceTime = Calendar.getInstance().getTime();
                System.out.println("Current time from device => " + DeviceTime);
                System.out.println("current time from internet =>"+InternetTime);

            } else{
//Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }catch (ClientProtocolException e) {
            Log.d("Response", e.getMessage());
        }catch (IOException e) {
            Log.d("Response", e.getMessage());
        }

    }

    private void CheckUserIsLoginOrNot() {
        Intent intent=new Intent() ;
        if(BaseApplication.getInstance().getSession().isLoggedIn()){
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
        }
        else {
            intent= new Intent(SplashActivity.this, LocationOptionActivity.class);
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


}
