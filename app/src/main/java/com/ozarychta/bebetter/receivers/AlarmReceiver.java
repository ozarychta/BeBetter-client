package com.ozarychta.bebetter.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.activities.ChallengeActivity;
import com.ozarychta.bebetter.data.Reminder;
import com.ozarychta.bebetter.data.ReminderDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_1";
    private static final String DATE_FORMAT_WITHOUT_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Override
    public void onReceive(Context context, Intent intent) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_WITHOUT_TIMEZONE);

        Bundle bundle = intent.getExtras();
        Long alarmId = (Long) bundle.get("REMINDER_ID");

        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(context);
        Reminder reminder = reminderDatabase.reminderDao().findById(alarmId);
        String title = reminder.getTitle();
        Long challengeId = reminder.getChallengeId();

        Date endDate;
        try {
            endDate = dateFormat.parse(reminder.getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        Calendar now = Calendar.getInstance();
        if(now.after(end)){
            reminder.setEnabled(false);
            reminderDatabase.reminderDao().update(reminder);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(context, alarmId.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(reminderPendingIntent);
            Log.d(this.getClass().getSimpleName(), "Cancelled alarm : " + alarmId + " for challenge : " + title);

            return;
        }

        intent = new Intent(context, ChallengeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);

        intent.putExtra("CHALLENGE_ID", challengeId);
        intent.putExtra("TITLE", title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), alarmId.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
