package com.pikopako.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pikopako.AppDelegate.BaseApplication;



public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("tag","device token: "+FirebaseInstanceId.getInstance().getToken());

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        BaseApplication.getInstance().getSession().setDeviceToken(refreshedToken);
        Log.e("TAG", "refresh token: "+refreshedToken );



    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}
