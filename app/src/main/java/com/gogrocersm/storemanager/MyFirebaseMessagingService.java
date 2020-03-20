package com.gogrocersm.storemanager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.session.MediaSession;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gogrocersm.storemanager.Config.BaseURL;
import com.gogrocersm.storemanager.MainActivity;
import com.gogrocersm.storemanager.R;
import com.gogrocersm.storemanager.util.CustomVolleyJsonRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public  class MyFirebaseMessagingService extends FirebaseMessagingService {
    Activity _context;
    public SharedPreferences settings;





    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        int type=getSharedPreferences("login_info",MODE_PRIVATE).getInt("usertype",2);

        RemoteMessage.Notification data = remoteMessage.getNotification();
//        String body = data.get("message");
//        String title = data.get("title");
//        Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

        Intent intent;
        if(type==2){
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        else
            intent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 101, intent, 0);

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                null
                ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new NotificationCompat.Builder(
                    getApplicationContext(), "222")
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setAutoCancel(true)
                    .setLargeIcon(((BitmapDrawable)getDrawable(R.drawable.icons)).getBitmap())
                    .setSmallIcon(R.drawable.icons)
                    //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.icons)
                    .setContentIntent(pi);
        }

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nm.notify(101, builder.build());
    }

}


