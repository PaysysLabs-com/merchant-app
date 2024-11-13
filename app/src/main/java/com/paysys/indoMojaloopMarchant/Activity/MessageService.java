package com.paysys.indoMojaloopMarchant.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.utils.Log;

import org.json.JSONObject;
import java.util.Random;


public class MessageService extends FirebaseMessagingService {
    private static final String TAG_NAME ="" ;
    private String title;
    private String body;
    private JSONObject jsonNotifyObj;
    private String message;
    private Intent intent;
    private PendingIntent pendingIntent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
            } else {
                // Handle message within 10 seconds
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d( "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        if(!remoteMessage.getNotification().getBody().contains("OTP")) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("NotifyData", (new Gson()).toJson(remoteMessage.getNotification().getBody()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        Log.d("NOTIFICATION"+ remoteMessage.getNotification().getBody());
        /*if(!remoteMessage.getNotification().getBody().contains("OTP")) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }*/

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        /*if(!remoteMessage.getNotification().getBody().contains("OTP")) {
            notificationBuilder.setContentIntent(pendingIntent);
        }*/
        notificationBuilder.setContentTitle("Mojaloop");
//        notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(remoteMessage.getNotification().getBody()));
        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        notificationBuilder.setVibrate(new long[] { 1000, 1000});
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;


        notificationBuilder.setSmallIcon(R.drawable.cooppay_app_icon);
        int n_id =  new Random().nextInt();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "1234";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }
        notificationManager.notify(n_id, notificationBuilder.build());
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if(!remoteMessage.getNotification().getBody().contains("OTP")) {
            startActivity(intent);
        }
    }
}
