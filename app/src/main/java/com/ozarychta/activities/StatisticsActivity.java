package com.ozarychta.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Day;
import com.ozarychta.model.HistoryDayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class StatisticsActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private static final String BASIC_DATE_FORMAT = "dd.MM.yyyy";

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat basicDateFormat;

    private ConnectivityManager connectivityManager;

    private Long challengeIdFromIntent;
    private ConfirmationType confirmationType;

    private ProgressBar progressBar;
    private TextView titleText;

    private RecyclerView.Adapter allDaysAdapter;
    private RecyclerView.LayoutManager allDaysLayoutManager;
    private RecyclerView allDaysRecyclerView;
    private ArrayList<Day> allDays;

    private TextView fromTextView;
    private TextView highestStreakTextView;
    private TextView toTextView;
    private TextView totalPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setTitle(R.string.statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        challengeIdFromIntent = getIntent().getLongExtra("CHALLENGE_ID", -1);
        String title = getIntent().getStringExtra("TITLE");
        Log.d(this.getClass().getSimpleName() + " challenge id ", challengeIdFromIntent.toString());

        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        basicDateFormat = new SimpleDateFormat(BASIC_DATE_FORMAT);
        basicDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        titleText = findViewById(R.id.titleTextView);
        titleText.setText(title);

        confirmationType = ConfirmationType.CHECK_TASK;

        allDaysRecyclerView = findViewById(R.id.history_recycler_view);
//        daysLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        allDaysLayoutManager = new GridLayoutManager(this, 7, GridLayoutManager.VERTICAL, false);
        allDaysRecyclerView.setLayoutManager(allDaysLayoutManager);
        allDaysRecyclerView.setItemAnimator(new DefaultItemAnimator());

        allDays = new ArrayList<>();
        allDaysAdapter = new HistoryDayAdapter(allDays);
        allDaysRecyclerView.setAdapter(allDaysAdapter);

        fromTextView = findViewById(R.id.fromTextView);
        highestStreakTextView = findViewById(R.id.highestStreakTextView);
        toTextView = findViewById(R.id.toTextView);
        totalPointsTextView = findViewById(R.id.totalPoints);

        silentSignInAndGetStatisticsData();
    }

    private void silentSignInAndGetStatisticsData() {
        progressBar.setVisibility(View.VISIBLE);
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getStatisticsData(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getStatisticsData(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getStatisticsData(String idToken) {
        allDays.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges/" + challengeIdFromIntent
                        + "/statistics",
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
                                    .show();
//                            progressBar.setVisibility(View.GONE);
                        }

                        Log.d("", response.toString(2));

                        JSONObject jsonObject = (JSONObject) response;

                        RepeatPeriod repeat = RepeatPeriod.valueOf(jsonObject.getString("repeatPeriod"));
                        Date start = dateFormat.parse(jsonObject.getString("startDate"));
                        Date end = dateFormat.parse(jsonObject.getString("endDate"));
                        ConfirmationType confirmation = ConfirmationType.valueOf(jsonObject.getString("confirmationType"));
                        Integer goal = jsonObject.getInt("goal");

                        confirmationType = confirmation;

                        JSONArray allDaysJsonArray = jsonObject.getJSONArray("allDays");

                        for (int i = 0; i < allDaysJsonArray.length(); i++) {
                            JSONObject dayJsonObject = (JSONObject) allDaysJsonArray.get(i);

                            Long id = dayJsonObject.getLong("id");
                            Date date = dateFormat.parse(dayJsonObject.getString("date"));
                            Boolean done = dayJsonObject.getBoolean("done");
                            Integer currentStatus = dayJsonObject.getInt("currentStatus");
                            Integer streak = dayJsonObject.getInt("streak");
                            Integer points = dayJsonObject.getInt("points");

                            Day day = new Day(id, date, done, currentStatus, goal, confirmation, streak, points);

                            allDays.add(day);
                            allDaysAdapter.notifyDataSetChanged();

                            showStatistics();
                        }

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
                headers.put("authorization", "Bearer " + idToken);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void showStatistics() {
        //Comparator gets the last max value
        Day maxStreakDay = allDays.stream().max((a, b) -> a.getStreak() > b.getStreak() ? 1 : -1).orElse(null);
        if(maxStreakDay == null || maxStreakDay.getStreak() == 0) {
            highestStreakTextView.setText("0");
            fromTextView.setText("");
            toTextView.setText("");
        } else {
            Integer highestStreak = maxStreakDay.getStreak();
            highestStreakTextView.setText(highestStreak.toString());

            Calendar c = Calendar.getInstance();
            c.setTime(maxStreakDay.getDate());
            c.add(Calendar.DAY_OF_YEAR, -(highestStreak-1));

            Date streakFirsDay = c.getTime();
            fromTextView.setText(basicDateFormat.format(streakFirsDay));
            toTextView.setText(basicDateFormat.format(maxStreakDay.getDate()));
        }

        Integer totalPoints = 0;
        for(Day d : allDays){
            totalPoints += d.getPoints();
        }
        totalPointsTextView.setText(totalPoints.toString());
    }
}
