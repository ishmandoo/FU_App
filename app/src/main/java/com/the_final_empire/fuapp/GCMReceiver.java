package com.the_final_empire.fuapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMReceiver extends BroadcastReceiver {
    public static final String TAG = GCMReceiver.class.getSimpleName();
    public GCMReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // assumes WordService is a registered service

        String friendName = intent.getStringExtra("friendName");

        Log.i(TAG, "Fuck you received from " + intent.getStringExtra("friendName"));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setVibrate(new long[] {0, 300, 200, 300})
                .setContentTitle(friendName + " says fuck you!")
                .setContentText(friendName + " says fuck you!");


        int mNotificationId = 001;

        Intent resultIntent = new Intent(context, LogIn.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(FUActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());





        NetworkService.startActionUpdateFriends(context);

    }
}
