package t27.surreyfooddeliverycompany;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
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
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.user_preference), Context.MODE_PRIVATE);
        String type = preferences.getString("loginType", null);

        if(remoteMessage.getData().size() > 0){

            Map<String, String> payload = remoteMessage.getData();
            Log.v(TAG, "Message data payload: " + remoteMessage.getData());
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

            if(type!=null&&type.equals("driver")) {
                if(payload.get("message")!=null&&payload.get("message").equals("taken")) {
                    mBuilder.setContentTitle("Your account was detected in another device.");
                    mBuilder.setContentText("Login again to get notifications.");
                } else {
                    mBuilder.setContentTitle("New Order Assigned.   Payment method: " + payload.get("paym"));
                    mBuilder.setContentText("To: " + payload.get("address"));
                }
                //for driver
                Intent intent = new Intent(this, DriverHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                mBuilder.setSmallIcon(R.mipmap.launcher_icon);

                        mBuilder.setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            } else {
                //dispatcher
                if(payload.get("orderstate")!=null&&payload.get("orderstate").equals("p")) {
                    mBuilder.setSmallIcon(R.mipmap.launcher_icon);
                    mBuilder.setContentTitle("New order.   Type:" + payload.get("orderType"));
                    mBuilder.setContentText("Order detail: "+ payload.get("content"));
                } else if(payload.get("orderstate")!=null&&payload.get("orderstate").equals("f")) {
                    mBuilder.setSmallIcon(R.mipmap.launcher_icon);
                    mBuilder.setContentTitle("Order is delivered.");
                    mBuilder.setContentText("Order detail: "+ payload.get("content"));
                } else {
                    return;
                }

                Intent intent = new Intent(this, DispatcherNewOrdersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager
                        .TYPE_NOTIFICATION);

                        mBuilder.setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        if(remoteMessage.getNotification() != null){
            Log.v(TAG, "Message Notification Body: " + remoteMessage.getNotification());
        }
    }
}
