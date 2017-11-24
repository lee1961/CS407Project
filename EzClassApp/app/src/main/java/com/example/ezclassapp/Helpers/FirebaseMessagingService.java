package com.example.ezclassapp.Helpers;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.example.ezclassapp.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Avinash Prabhakar on 11/24/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon4)
                        .setContentTitle("Review update foreground")
                        .setContentText(notification_message);
        // Sets an ID for the notification
        int mNotificationId = (int)System.currentTimeMillis();
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
