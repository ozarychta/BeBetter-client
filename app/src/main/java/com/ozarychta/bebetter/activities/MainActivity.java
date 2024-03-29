package com.ozarychta.bebetter.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.adapters.ChallengeAdapter;
import com.ozarychta.bebetter.adapters.EnumArrayAdapter;
import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.enums.Category;
import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.models.Challenge;
import com.ozarychta.bebetter.utils.ServerRequestUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private ConnectivityManager connectivityManager;
    private SimpleDateFormat dateFormat;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");

    private Spinner categorySpinner;
    private Spinner repeatSpinner;
    private Spinner stateSpinner;
    private EditText cityEdit;
    private EditText searchEdit;

    private Button searchBtn;
    private ProgressBar progressBar;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Challenge> challenges;
    private TextView noResultsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.challenges);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        searchBtn = findViewById(R.id.searchBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        categorySpinner = findViewById(R.id.categorySpinner);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        stateSpinner = findViewById(R.id.activeSpinner);
        cityEdit = findViewById(R.id.cityEdit);
        searchEdit = findViewById(R.id.searchEdit);

        categorySpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Category.values()));
        categorySpinner.setSelection(0);

        repeatSpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, RepeatPeriod.values()));
        repeatSpinner.setSelection(0);

        stateSpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ChallengeState[]{ChallengeState.ALL, ChallengeState.STARTED, ChallengeState.NOT_STARTED_YET }));
        stateSpinner.setSelection(0);

        recyclerView = findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        noResultsLabel = findViewById(R.id.noResultsLabel);
        noResultsLabel.setVisibility(View.GONE);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddChallengeActivity();
            }
        });

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        challenges = new ArrayList<>();
        adapter = new ChallengeAdapter(challenges);
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> silentSignInAnd(this::getChallenges));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        createNotificationChannel();

        if (ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null) {
                startLoginActivity();
                finish();
                return;
            }

            Log.d(this.getClass().getSimpleName() + " TOKEN ", account.getIdToken() == null ? "null" : account.getIdToken());
        } else {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
            startLoginActivity();
            finish();
            return;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = getString(R.string.channel_id_1);
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startAddChallengeActivity() {
        Intent i = new Intent(this, AddChallengeActivity.class);
        startActivity(i);
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void getChallenges(String token) {
        progressBar.setVisibility(View.VISIBLE);
        challenges.clear();
        noResultsLabel.setVisibility(View.GONE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges?" + getUrlParameters(),
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            progressBar.setVisibility(View.GONE);
                            noResultsLabel.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = (JSONObject) response.get(i);

                            Integer id = jsonObject.getInt("id");
                            String title = jsonObject.getString("title");
                            String description = jsonObject.getString("description");
                            String city = jsonObject.getString("city");
                            RepeatPeriod repeat = RepeatPeriod.valueOf(jsonObject.getString("repeatPeriod"));
                            Category category = Category.valueOf(jsonObject.getString("category"));
                            AccessType access = AccessType.valueOf(jsonObject.getString("accessType"));
                            OffsetDateTime offsetDateTimeStart = OffsetDateTime.parse(jsonObject.getString("startDate"), formatter);
                            LocalDateTime start = offsetDateTimeStart.toLocalDateTime();
                            OffsetDateTime offsetDateTimeEnd = OffsetDateTime.parse(jsonObject.getString("endDate"), formatter);
                            LocalDateTime end = offsetDateTimeEnd.toLocalDateTime();
                            ChallengeState state = ChallengeState.valueOf(jsonObject.getString("challengeState"));
                            ConfirmationType confirmation = ConfirmationType.valueOf(jsonObject.getString("confirmationType"));
                            Integer goal = jsonObject.getInt("goal");

                            Challenge c = new Challenge();
                            c.setId(Long.valueOf(id));
                            c.setTitle(title);
                            c.setDescription(description);
                            c.setCity(city);
                            c.setRepeatPeriod(repeat);
                            c.setCategory(category);
                            c.setConfirmationType(confirmation);
                            c.setAccessType(access);
                            c.setState(state);
                            c.setGoal(goal);
                            c.setStartDate(start);
                            c.setEndDate(end);

                            challenges.add(c);
                            adapter.notifyDataSetChanged();

                            Log.d(this.getClass().getSimpleName() + " jsonObject", challenges.get(i).toString());
                        }

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
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                                .show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + token);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    private String getUrlParameters() {
        String url = "access=" + AccessType.PUBLIC;

        if (categorySpinner.getSelectedItem() != Category.ALL) {
            url += "&category=" + ((Category)categorySpinner.getSelectedItem()).name();
        }
        if (repeatSpinner.getSelectedItem() != RepeatPeriod.ALL) {
            url += "&repeat=" + ((RepeatPeriod)repeatSpinner.getSelectedItem()).name();
        }
        if (stateSpinner.getSelectedItem() != ChallengeState.ALL) {
            url += "&state=" + ((ChallengeState)stateSpinner.getSelectedItem()).name();
        }
        String city = cityEdit.getText().toString();
        if (!city.isEmpty()) {
            url += "&city=" + city;
        }
        String search = searchEdit.getText().toString();
        if (!search.isEmpty()) {
            url += "&search=" + search;
        }
        return url;
    }
}
