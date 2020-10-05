package com.ozarychta.bebetter.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.data.Reminder;
import com.ozarychta.bebetter.data.ReminderDatabase;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
            Long signedUserId = sharedPref.getLong(context.getString(R.string.user_id_field), -1);

            ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(context);
            List<Reminder> reminders = reminderDatabase.reminderDao().findByUserIdAndEnabled(signedUserId, true);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent reminderIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            reminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            for (Reminder r : reminders) {
                Long reminderId = r.getId();
                reminderIntent.putExtra("REMINDER_ID", reminderId);
                PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                        reminderId.intValue(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar reminderTime = Calendar.getInstance();
                reminderTime.setTimeInMillis(System.currentTimeMillis());
                reminderTime.set(Calendar.HOUR_OF_DAY, r.getHour());
                reminderTime.set(Calendar.MINUTE, r.getMin());

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reminderTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, reminderPendingIntent);
            }
    }
}
