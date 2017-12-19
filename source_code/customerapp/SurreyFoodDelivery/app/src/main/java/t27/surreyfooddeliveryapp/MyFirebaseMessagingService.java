package t27.surreyfooddeliveryapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "MyMessagingService";
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Map<String, String> payload = remoteMessage.getData();
            Log.v(TAG, "Message data payload: " + remoteMessage.getData());
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.lancher_logo);
            if(payload.get("orderstate").equals("d")) {
                mBuilder.setContentTitle("Order is on the way");
                mBuilder.setContentText("Order detail: " + payload.get("content"));
            } else if(payload.get("orderstate").equals("f")) {
                mBuilder.setContentTitle("Order is delivered");
                mBuilder.setContentText("Order detail: " + payload.get("content"));
            } else {
                return;
            }



            Intent resultIntent = new Intent(this, CurrentOrderActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);


            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }

        if(remoteMessage.getNotification() != null){
            Log.v(TAG, "Message Notification Body: " + remoteMessage.getNotification());
        }
    }
}
