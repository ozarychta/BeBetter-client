package com.ozarychta.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.model.Day;
import com.ozarychta.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity{

    private ConnectivityManager connectivityManager;

    private User user;

    private TextView usernameText;
    private TextView aboutMeText;
    private TextView mainGoalText;
    private TextView rankingPointsText;
    private TextView highestStrikeText;

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
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/users",
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

                        usernameText.setText(username);
                        aboutMeText.setText(aboutMe);
                        mainGoalText.setText(mainGoal);
                        rankingPointsText.setText(points);
                        highestStrikeText.setText(strike);

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
}
