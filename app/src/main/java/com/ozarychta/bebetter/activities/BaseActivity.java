package com.ozarychta.bebetter.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ozarychta.bebetter.receivers.AlarmReceiver;
import com.ozarychta.bebetter.utils.ApplicationExecutor;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.data.Reminder;
import com.ozarychta.bebetter.data.ReminderDatabase;
import com.ozarychta.bebetter.utils.SignInClient;

import java.util.List;
import java.util.concurrent.Executor;

public class BaseActivity extends AppCompatActivity {

    private ReminderDatabase reminderDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        reminderDatabase = ReminderDatabase.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;
            case R.id.action_challenges:
                startChallengesActivity();
//                finish();
                break;
            case R.id.action_my_challenges:
                startMyChallengesActivity();
//                finish();
                break;
            case R.id.action_profile:
                startProfileActivity();
//                finish();
                break;
            case R.id.action_friends:
                startFriendsActivity();
//                finish();
                break;
            case R.id.action_sign_out:
                signOut();
                break;
            default:
                break;
        }

        return true;
    }

    private void startFriendsActivity() {
        Intent i = new Intent(this, FriendsActivity.class);
        startActivity(i);
    }

    private void startProfileActivity() {
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        Long signedUserId = sharedPref.getLong(getString(R.string.user_id_field), -1);
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("USER_ID", signedUserId);
        startActivity(i);
    }

    private void startMyChallengesActivity() {
        Intent i = new Intent(this, MyChallengesActivity.class);
        startActivity(i);
    }

    private void startChallengesActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void signOut() {
        cancelAllReminders();

        SignInClient.getInstance(this).getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(getString(R.string.user_id_field));
                        editor.commit();

                        startLoginActivity();
                        finish();
                    }
                });

    }

    private void cancelAllReminders(){
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        Long signedUserId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        List<Reminder> reminders = reminderDatabase.reminderDao().findByUserIdAndEnabled(signedUserId, true);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent reminderIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        reminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Executor executor = ApplicationExecutor.getInstance().getExecutor();

        for(Reminder r : reminders){
            Long reminderId = r.getId();
            reminderIntent.putExtra("REMINDER_ID", reminderId);
            PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(this, reminderId.intValue(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(reminderPendingIntent);
            Log.d(this.getClass().getSimpleName() + "Cancelled reminder with id ", reminderId.toString());

            r.setEnabled(false);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    reminderDatabase.reminderDao().update(r);
                }
            });

        }
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
