package com.ozarychta.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.ChallengeAdapter;
import com.ozarychta.model.Day;
import com.ozarychta.model.DayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ChallengeActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private static final String BASIC_DATE_FORMAT = "dd.MM.yyyy";
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat basicDateFormat;

    private ConnectivityManager connectivityManager;

    private Challenge challenge;
    private Day today;

    private TextView goalText;
    private TextView moreOrLessText;

    private TextView titleText;
    private TextView descText;
    private TextView cityText;

    private TextView categoryText;
    private TextView repeatText;
    private TextView confirmationText;
    private TextView accessText;

    private TextView startText;
    private TextView endText;

    private Button joinBtn;
    private Button statisticsBtn;

    private TextView daysLabel;
    private TextView notStartedYetLabel;
    private LinearLayout daysLayout;
    private MaterialCardView todayCard;
    private TextView todayDate;
    private ToggleButton todayToggle;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Day> pastDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        getSupportActionBar().setTitle(R.string.challenge);

        challenge = (Challenge) getIntent().getSerializableExtra("CHALLENGE");
        Log.d("challenge", challenge.getId().toString());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        basicDateFormat = new SimpleDateFormat(BASIC_DATE_FORMAT);
        basicDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        titleText = findViewById(R.id.titleTextView);
        descText = findViewById(R.id.descriptionTextView);
        cityText = findViewById(R.id.cityTextView);

        categoryText = findViewById(R.id.categoryTextView);
        repeatText = findViewById(R.id.repeatTextView);
        confirmationText = findViewById(R.id.confirmationTextView);
        accessText = findViewById(R.id.accessTextView);

        goalText = findViewById(R.id.goalTextView);
        moreOrLessText = findViewById(R.id.moreOrLessTextView);

        startText = findViewById(R.id.startTextView);
        endText = findViewById(R.id.endTextView);

        joinBtn = findViewById(R.id.joinBtn);
        statisticsBtn = findViewById(R.id.statisticsBtn);

        daysLabel = findViewById(R.id.daysLabel);
        daysLayout = findViewById(R.id.daysLinearLayout);
        todayCard = findViewById(R.id.today);

        recyclerView = findViewById(R.id.past_days_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        pastDays = new ArrayList<>();
        adapter = new DayAdapter(pastDays);
        recyclerView.setAdapter(adapter);

        titleText.setText(challenge.getTitle());
        descText.setText(challenge.getDescription());
        cityText.setText(challenge.getCity());

        categoryText.setText(challenge.getCategory().toString());
        repeatText.setText(challenge.getRepeatPeriod().toString());
        confirmationText.setText(challenge.getConfirmationType().toString());
        accessText.setText(challenge.getAccessType().toString());

        goalText.setText(challenge.getGoal().toString());
        moreOrLessText.setText(challenge.getMoreBetter().toString());

        startText.setText(basicDateFormat.format(challenge.getStartDate()));
        endText.setText(basicDateFormat.format(challenge.getEndDate()));

        notStartedYetLabel = findViewById(R.id.notStartedYetLabel);
        notStartedYetLabel.setVisibility(View.GONE);

        todayDate = todayCard.findViewById(R.id.dateTextView);
        todayToggle = todayCard.findViewById(R.id.toggleButton);
        todayToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                today.setDone(isChecked);
                silentSignInAndSaveChange();
            }
        });

        if(challenge.getAccessType() == AccessType.PUBLIC && challenge.getUserParticipant()==false){
            daysLabel.setVisibility(View.GONE);
            daysLayout.setVisibility(View.GONE);
            statisticsBtn.setVisibility(View.GONE);

            joinBtn.setVisibility(View.VISIBLE);
        } else {
            daysLabel.setVisibility(View.VISIBLE);
            statisticsBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);

            updateStateDependentUI();
        }

        joinBtn.setOnClickListener(v -> silentSignInAndJoinChallenge());
    }

    private void silentSignInAndSaveChange() {
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            saveChange(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> saveChange(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void saveChange(String tokenId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("done", today.getDone());
        requestBody.put("currentStatus", today.getCurrentStatus());

        JSONObject jsonRequestBody = new JSONObject(requestBody);
        Log.d("request body", "\n\n" + jsonRequestBody.toString() + "\n\n");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/days/" + today.getId(),
                jsonRequestBody,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

//                        Toast.makeText(getApplicationContext(), getString(R.string.updated_state), Toast.LENGTH_LONG)
//                                .show();
                        Log.d("", "Updated day");

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                headers.put("authorization", "Bearer " + tokenId);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void updateStateDependentUI() {
        ChallengeState state = challenge.getState();

        if(ChallengeState.NOT_STARTED == state){
            daysLayout.setVisibility(View.GONE);
            notStartedYetLabel.setVisibility(View.VISIBLE);
        } else if(ChallengeState.STARTED == state){
            daysLayout.setVisibility(View.VISIBLE);
            notStartedYetLabel.setVisibility(View.GONE);
            silentSignInAndGetLastFourDays();
            todayCard.setVisibility(View.VISIBLE);
        } else {
            daysLayout.setVisibility(View.VISIBLE);
            notStartedYetLabel.setVisibility(View.GONE);
            todayCard.setVisibility(View.GONE);
            silentSignInAndGetLastFourDays();
        }
    }

    private void silentSignInAndGetLastFourDays() {

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getLastFourDays(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getLastFourDays(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getLastFourDays(String idToken) {
        ChallengeState state = challenge.getState();
        pastDays.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/days?challengeState=" +state,
                null,
                response -> {
                    try {
                        if (response.length()==0){
                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
                                    .show();
//                            progressBar.setVisibility(View.GONE);
                        }

                        JSONObject jsonObject = (JSONObject) response.get(0);

                        Long id = jsonObject.getLong("id");
                        Date date = dateFormat.parse(jsonObject.getString("date"));
                        Boolean done = jsonObject.getBoolean("done");
                        Integer currentStatus = jsonObject.getInt("currentStatus");

                        if(state==ChallengeState.STARTED){
                            today = new Day(id, date, done, currentStatus);

                            todayDate.setText(simpleDateFormat.format(date));
                            todayToggle.setChecked(done);
                        } else {
                            Day day = new Day(id, date, done, currentStatus);
                            pastDays.add(day);
                            adapter.notifyDataSetChanged();
                        }


                        for (int i = 1; i < response.length(); i++) {
                            try {
                                jsonObject = (JSONObject) response.get(i);

                                id = jsonObject.getLong("id");
                                date = dateFormat.parse(jsonObject.getString("date"));
                                done = jsonObject.getBoolean("done");
                                currentStatus = jsonObject.getInt("currentStatus");

                                Day day = new Day(id, date, done, currentStatus);
                                pastDays.add(day);
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

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
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }


    private void silentSignInAndJoinChallenge() {
//        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            joinChallenge(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> joinChallenge(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void joinChallenge(String token) {
//        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/participants",
                null,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        daysLabel.setVisibility(View.VISIBLE);
                        statisticsBtn.setVisibility(View.VISIBLE);

                        joinBtn.setVisibility(View.GONE);

                        challenge.setUserParticipant(true);

                        updateStateDependentUI();

                        Toast.makeText(getApplicationContext(), getString(R.string.joined_challenge), Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                headers.put("authorization", "Bearer " + token);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

}
