package test.foodsharing;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

// Handles FireBase interactions for push notifications

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private String ADMIN_CHANNEL_ID = "admin_channel";

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage p0) {
        super.onMessageReceived(p0);

        Intent intent = new Intent(this, LoginActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int notificationID = random.nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(p0.getData().get("title"))
                .setContentText(p0.getData().get("message"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    private void setupChannels(NotificationManager notificationManager) {
        String adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        notificationManager.createNotificationChannel(adminChannel);
    }


}
