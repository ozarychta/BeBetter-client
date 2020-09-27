package com.ozarychta;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ozarychta.activities.ChallengeActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_1";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent intent = new Intent(this, ChallengeActivity.class);

        Bundle bundle = intent.getExtras();
        Long id = (Long) bundle.get("CHALLENGE_ID");
        String title = (String) bundle.get("TITLE");
        Long alarmId = (Long) bundle.get("ALARM_ID");
//        int test = Integer.parseInt(testString);
//        Challenge challenge = (Challenge) bundle.getSerializable("CHALLENGE");
        Log.d(this.getClass().getSimpleName() + " challenge", id.toString());
//        intent.setClass(context, ChallengeActivity.class);

        intent = new Intent(context, ChallengeActivity.class);
//        intent.putExtra("CHALLENGE", challenge);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);

        intent.putExtra("CHALLENGE_ID", id);
        intent.putExtra("TITLE", title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmId.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.reminder_text_default)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
