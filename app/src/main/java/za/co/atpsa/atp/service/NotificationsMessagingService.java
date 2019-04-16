package za.co.atpsa.atp.service;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.fcm.MessagingService;

import java.util.Map;

import za.co.atpsa.atp.Content;
import za.co.atpsa.atp.R;

public class NotificationsMessagingService extends MessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // Log.i("NotificationsService", "Got a remote message 🎉");
        if(remoteMessage.getData().size() > 0){
            Map<String,String> data = remoteMessage.getData();
            if(data.containsKey("success")){
               String title = data.get("success").equals("1") ? "Payment Successful": "Payment Failed";
               sendNotification(title, data.get("message"));
            }

        }


    }

    private  void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, Content.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo_short)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setBadgeIconType(R.drawable.logo_short)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}