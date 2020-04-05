package com.ozarychta.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.model.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity{

    private static final Integer REQUEST_CODE = 1;

    private ConnectivityManager connectivityManager;

    private User user;
    private Long userIdFromIntent;
    private Long signedUserId;

    private TextView usernameText;
    private TextView aboutMeText;
    private TextView mainGoalText;
    private TextView rankingPointsText;
    private TextView highestStrikeText;

    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.profile);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        usernameText = findViewById(R.id.usernameTextView);
        aboutMeText = findViewById(R.id.aboutMeTextView);
        mainGoalText = findViewById(R.id.mainGoalTextView);
        rankingPointsText = findViewById(R.id.pointsTextView);
        highestStrikeText = findViewById(R.id.strikeTextView);

        editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("USER", user);
            startActivityForResult(intent, REQUEST_CODE);
        });

        userIdFromIntent = Long.valueOf(getIntent().getIntExtra("USER_ID", -1));

        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        signedUserId = Long.valueOf(sharedPref.getInt(getString(R.string.user_id_field), -1));

        if (userIdFromIntent.equals(signedUserId)){
            editBtn.setVisibility(View.VISIBLE);
        } else {
            editBtn.setVisibility(View.GONE);
        }

        if (signedUserId == -1){
            Toast.makeText(getApplicationContext(), "signed user id -1", Toast.LENGTH_LONG)
                    .show();
            editBtn.setVisibility(View.GONE);
        }

        silentSignInAndGetUserInfo();
    }

    private void silentSignInAndGetUserInfo() {
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getUserInfo(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getUserInfo(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getUserInfo(String idToken) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/users/" + userIdFromIntent,
                null,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Long id = jsonObject.getLong("id");
                        String username = jsonObject.getString("username");
                        String aboutMe = jsonObject.getString("aboutMe");
                        String mainGoal = jsonObject.getString("mainGoal");
                        Integer points = jsonObject.getInt("rankingPoints");
                        Integer strike = jsonObject.getInt("highestStreak");

                        user = new User(id, username, aboutMe, mainGoal, points, strike);
                        updateUI();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("", "request response:failed message=" + e.getMessage());
                    } finally {
//                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)){
                        Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                                .show();
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                                .show();
                    }
//                    progressBar.setVisibility(View.GONE);
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + idToken);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                user = (User) data.getSerializableExtra("USER");
                updateUI();
            }
        }
    }

    private void updateUI() {
        usernameText.setText(user.getUsername());
        aboutMeText.setText(user.getAboutMe());
        mainGoalText.setText(user.getMainGoal());
        rankingPointsText.setText(String.valueOf(user.getRankingPoints()));
        highestStrikeText.setText(String.valueOf(user.getHighestStrike()));
    }
}
