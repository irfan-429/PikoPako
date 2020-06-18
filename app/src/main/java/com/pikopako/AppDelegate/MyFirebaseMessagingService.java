package com.pikopako.AppDelegate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pikopako.Activity.My_orders;
import com.pikopako.AppUtill.Constant;
import com.pikopako.R;

public class MyFirebaseMessagingService  extends FirebaseMessagingService{
    String body = "";
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if ( true) {

            } else {
                handleNow();
            }
        }

        if (remoteMessage.getData() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getData().get("message"));
        }

    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, My_orders.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = Constant.CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.pikopako_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                getResources(), R.mipmap.pikopako_logo))
                        .setContentTitle("PIKOPAKO")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(messageBody)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        notification.sound = defaultSoundUri;
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(defaultSoundUri, null);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notification);
        }
    }
}
