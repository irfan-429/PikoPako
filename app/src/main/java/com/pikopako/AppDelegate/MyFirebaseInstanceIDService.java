package com.pikopako.AppDelegate;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIDService extends MyFirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("bug", "Refreshed fcm token: " + refreshedToken);
        BaseApplication.getInstance().getSession().setFCMToken(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }


    //    @Override
//    public void onTokenRefresh() {
//        super.onTokenRefresh();
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.e("bug", "Refreshed fcm token: " + refreshedToken);
//        BaseApplication.getInstance().getSession().setFCMToken(refreshedToken);
//        sendRegistrationToServer(refreshedToken);
//
//    }

    private void sendRegistrationToServer(String token) {

    }
}
