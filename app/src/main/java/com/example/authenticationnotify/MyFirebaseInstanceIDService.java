package com.example.authenticationnotify;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        getFirebaseMessage(message.getNotification().getTitle()  , message.getNotification().getBody());
    }

    private void getFirebaseMessage(String title   , String msg) {
        NotificationCompat.Builder builder  = new NotificationCompat.Builder(this , "myChannelFirebaseNoifiction")
                .setSmallIcon(R.drawable.add)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat  = NotificationManagerCompat.from(this) ;
        managerCompat.notify(101 , builder.build());






    }

}
