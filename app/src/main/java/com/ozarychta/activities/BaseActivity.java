package com.ozarychta.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ozarychta.R;
import com.ozarychta.SignInClient;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
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
            case R.id.action_challenges:
                startChallengesActivity();
                finish();
                break;
            case R.id.action_my_challenges:
                startMyChallengesActivity();
                finish();
                break;
            case R.id.action_profile:
                startProfileActivity();
                finish();
                break;
            case R.id.action_friends:
                //
                break;
            case R.id.action_achievements:
                //
                break;
            case R.id.action_statistics:
                //
                break;
            case R.id.action_settings:
                //
                break;
            case R.id.action_sign_out:
                signOut();
                break;
            default:
                break;
        }

        return true;
    }

    private void startProfileActivity() {
        Intent i = new Intent(this, ProfileActivity.class);
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

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
