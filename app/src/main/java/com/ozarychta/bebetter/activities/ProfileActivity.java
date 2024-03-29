package com.ozarychta.bebetter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.Task;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.adapters.AchievementAdapter;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.utils.SignInClient;
import com.ozarychta.bebetter.models.Achievement;
import com.ozarychta.bebetter.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

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
    private Button followBtn;
    private Button unfollowBtn;

    private ProgressBar progressBar;
    private ProgressBar achievementsProgressBar;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    private ArrayList<Achievement> achievements;
    private TextView noResultsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        usernameText = findViewById(R.id.usernameTextView);
        aboutMeText = findViewById(R.id.aboutMeTextView);
        mainGoalText = findViewById(R.id.mainGoalTextView);
        rankingPointsText = findViewById(R.id.pointsTextView);
        highestStrikeText = findViewById(R.id.strikeTextView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        achievementsProgressBar = findViewById(R.id.achievementsProgressBar);
        achievementsProgressBar.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.achievementsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        achievements = new ArrayList<>();
        adapter = new AchievementAdapter(achievements);
        recyclerView.setAdapter(adapter);

        noResultsLabel = findViewById(R.id.noResultsLabel);
        noResultsLabel.setVisibility(View.GONE);

        editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("USER", user);
            startActivityForResult(intent, REQUEST_CODE);
        });

        followBtn = findViewById(R.id.followBtn);
        followBtn.setOnClickListener(v -> silentSignInAnd(this::followUser)
        );

        unfollowBtn = findViewById(R.id.unfollowBtn);
        unfollowBtn.setOnClickListener(v -> silentSignInAnd(this::unfollowUser)
        );

        userIdFromIntent = getIntent().getLongExtra("USER_ID", -1);

        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        signedUserId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        if (userIdFromIntent.equals(signedUserId)) {
            editBtn.setVisibility(View.VISIBLE);
            followBtn.setVisibility(View.GONE);
            unfollowBtn.setVisibility(View.GONE);
        } else {
            editBtn.setVisibility(View.GONE);
            followBtn.setVisibility(View.GONE);
            unfollowBtn.setVisibility(View.GONE);
        }

        if (signedUserId == -1) {
            Toast.makeText(getApplicationContext(), "signed user id -1", Toast.LENGTH_LONG)
                    .show();
            editBtn.setVisibility(View.GONE);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        silentSignInAnd(this::getUserInfo);
        silentSignInAnd(this::getAchievements);
    }

    private void getAchievements(String idToken) {
        achievementsProgressBar.setVisibility(View.VISIBLE);
        achievements.clear();
        noResultsLabel.setVisibility(View.GONE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/users/" + userIdFromIntent + "/achievements",
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            noResultsLabel.setVisibility(View.VISIBLE);
                            achievementsProgressBar.setVisibility(View.GONE);
                        }
                        Log.d(this.getClass().getSimpleName() + " response", response.toString(2));

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = (JSONObject) response.get(i);

                            Long id = jsonObject.getLong("id");
                            String title = jsonObject.getString("title");
                            String desc = jsonObject.getString("description");
                            Boolean achieved = jsonObject.getBoolean("achieved");

                            Achievement a = new Achievement(id, title, desc, achieved);

                            if (achieved) {
                                achievements.add(a);
                                adapter.notifyDataSetChanged();
                            }

                            Log.d(this.getClass().getSimpleName() + " jsonObject user", jsonObject.toString(2));
                        }

                        if (achievements.isEmpty()) {
                            noResultsLabel.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
                        achievementsProgressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                                .show();
                    }
                    achievementsProgressBar.setVisibility(View.GONE);
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
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    private void followUser(String tokenIdFromResult) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/follow?userId=" + userIdFromIntent,
                null,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        Log.d(this.getClass().getSimpleName(), id + " followed user " + userIdFromIntent);

                        Toast.makeText(getApplicationContext(), "followed user" + userIdFromIntent, Toast.LENGTH_LONG)
                                .show();

                        followBtn.setVisibility(View.GONE);
                        unfollowBtn.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                                .show();
                    } else
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                                .show();
                    progressBar.setVisibility(View.GONE);
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + tokenIdFromResult);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void unfollowUser(String tokenIdFromResult) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/unfollow?userId=" + userIdFromIntent,
                null,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        Log.d(this.getClass().getSimpleName(), id + " followed user " + userIdFromIntent);

                        Toast.makeText(getApplicationContext(), "unfollowed user" + userIdFromIntent, Toast.LENGTH_LONG)
                                .show();

                        followBtn.setVisibility(View.VISIBLE);
                        unfollowBtn.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                                .show();
                    } else
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                                .show();
                    progressBar.setVisibility(View.GONE);
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + tokenIdFromResult);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void getUserInfo(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
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
                        Boolean followed = jsonObject.getBoolean("followed");

                        user = new User(id, username, aboutMe, mainGoal, points, strike, followed);
                        updateUI();

                    } catch (Exception e) {
                        showExceptionMessage(e);
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    showVolleyErrorMessage(error);
                    progressBar.setVisibility(View.GONE);
                }
        ) {
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + idToken);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void showVolleyErrorMessage(VolleyError error) {
        if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
            Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                    .show();
        }

        Log.d(this.getClass().getName() + ": Error code :", String.valueOf(error.networkResponse.statusCode));
        try {
            Log.d(this.getClass().getName() + ": Error response :", new String(error.networkResponse.data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showExceptionMessage(Exception exception) {
        exception.printStackTrace();
        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                .show();
        Log.d(this.getClass().getName(), exception.getMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                user = (User) data.getSerializableExtra("USER");
                updateUI();
            }
        }
    }

    private void updateUI() {
        if (user.isFollowed()) {
            followBtn.setVisibility(View.GONE);
            unfollowBtn.setVisibility(View.VISIBLE);
        } else if (!signedUserId.equals(userIdFromIntent)) {
            followBtn.setVisibility(View.VISIBLE);
            unfollowBtn.setVisibility(View.GONE);
        }

        usernameText.setText(user.getUsername());
        aboutMeText.setText(Strings.isEmptyOrWhitespace(user.getAboutMe()) ? "_" : user.getAboutMe());
        mainGoalText.setText(Strings.isEmptyOrWhitespace(user.getMainGoal()) ? "_" : user.getMainGoal());
        rankingPointsText.setText(String.valueOf(user.getRankingPoints()));
        highestStrikeText.setText(String.valueOf(user.getHighestStrike()));
    }
}
