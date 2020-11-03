package com.ozarychta.bebetter.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.adapters.HistoryDayAdapter;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.models.Day;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.utils.SignInClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends BaseActivity {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");
    DateTimeFormatter basicDayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

    private HorizontalBarChart weekdaysChart;

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

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        titleText = findViewById(R.id.titleTextView);
        titleText.setText(title);

        confirmationType = ConfirmationType.CHECK_TASK;

        allDaysRecyclerView = findViewById(R.id.history_recycler_view);
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

        weekdaysChart = (HorizontalBarChart) findViewById(R.id.horizontalBarChart);

//        LineChart chart = (LineChart) findViewById(R.id.chart);
//        chart.setVisibility(View.GONE);
//
//        List<Entry> entries = new ArrayList<Entry>();
//        entries.add(new Entry(1, 2));
//        entries.add(new Entry(2, 3));
//        entries.add(new Entry(3, 4));
//        entries.add(new Entry(4, 20));
//
//        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
//        dataSet.setColor(Color.rgb(0, 155, 0));
//        dataSet.setValueTextColor(Color.rgb(0, 0, 155));
//
//        LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate(); // refresh

        silentSignInAnd(this::getStatisticsData);
    }

    private void getStatisticsData(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
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
                        OffsetDateTime offsetDateTimeStart = OffsetDateTime.parse(jsonObject.getString("startDate"), formatter);
                        LocalDateTime start = offsetDateTimeStart.toLocalDateTime();
                        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.parse(jsonObject.getString("endDate"), formatter);
                        LocalDateTime end = offsetDateTimeEnd.toLocalDateTime();
                        ConfirmationType confirmation = ConfirmationType.valueOf(jsonObject.getString("confirmationType"));
                        Integer goal = jsonObject.getInt("goal");

                        confirmationType = confirmation;

                        JSONArray allDaysJsonArray = jsonObject.getJSONArray("allDays");

                        for (int i = 0; i < allDaysJsonArray.length(); i++) {
                            JSONObject dayJsonObject = (JSONObject) allDaysJsonArray.get(i);

                            Long id = dayJsonObject.getLong("id");
                            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dayJsonObject.getString("date"), formatter);
                            LocalDateTime date = offsetDateTime.toLocalDateTime();
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

            LocalDateTime streakFirstDayDate = maxStreakDay.getDate().minusDays(highestStreak-1);

            fromTextView.setText(streakFirstDayDate.format(basicDayFormatter));
            toTextView.setText(maxStreakDay.getDate().format(basicDayFormatter));
        }

        Integer totalPoints = 0;
        for(Day d : allDays){
            totalPoints += d.getPoints();
        }
        totalPointsTextView.setText(totalPoints.toString());

        showWeekDaysChart();
    }

    private void showWeekDaysChart() {
        weekdaysChart.getDescription().setEnabled(false);
        weekdaysChart.setDrawGridBackground(false);
        weekdaysChart.setDrawValueAboveBar(false);
        weekdaysChart.getLegend().setEnabled(false);

        XAxis xAxis = weekdaysChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setTextSize(12);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.black));

        String[] DAYS = {getString(R.string.sunday), getString(R.string.saturday),
                getString(R.string.friday), getString(R.string.thursday),
                getString(R.string.wednesday), getString(R.string.tuesday),
                getString(R.string.monday)};

        xAxis.setValueFormatter(new IndexAxisValueFormatter(DAYS){
        });

        YAxis axisLeft = weekdaysChart.getAxisLeft();
        axisLeft.setGranularity(1);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = weekdaysChart.getAxisRight();
        axisRight.setGranularity(1);
        axisRight.setAxisMinimum(0);

        int monCount = 0;
        int tueCount = 0;
        int wedCount = 0;
        int thuCount = 0;
        int friCount = 0;
        int satCount = 0;
        int sunCount = 0;

        for(Day day : allDays){
            LocalDateTime dateTime = day.getDate();

            if(day.getPoints() > 0){
                switch(dateTime.getDayOfWeek()){
                    case MONDAY:
                        monCount += 1;
                        break;
                    case TUESDAY:
                        tueCount +=1;
                        break;
                    case WEDNESDAY:
                        wedCount += 1;
                        break;
                    case THURSDAY:
                        thuCount +=1;
                        break;
                    case FRIDAY:
                        friCount += 1;
                        break;
                    case SATURDAY:
                        satCount +=1;
                        break;
                    case SUNDAY:
                        sunCount += 1;
                        break;
                }
            }
        }

        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(6, monCount));
        entries.add(new BarEntry(5, tueCount));
        entries.add(new BarEntry(4, wedCount));
        entries.add(new BarEntry(3, thuCount));
        entries.add(new BarEntry(2, friCount));
        entries.add(new BarEntry(1, satCount));
        entries.add(new BarEntry(0, sunCount));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColor(R.color.primaryColor);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        weekdaysChart.setData(barData);
        weekdaysChart.invalidate();
    }
}
