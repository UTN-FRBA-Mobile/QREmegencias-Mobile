package com.qre.services.firebase;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qre.R;

import java.util.Map;

public class QREMessagingService extends FirebaseMessagingService {

    private static final int M_NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        if (data != null && data.containsKey("title") && data.containsKey("text")) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notif_logo)
                            .setContentTitle(data.get("title"))
                            .setAutoCancel(true)
                            .setContentText(data.get("text"));
            final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(M_NOTIFICATION_ID, mBuilder.build());
            }
        }

    }
}
