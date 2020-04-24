package com.ozarychta.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.ChallengeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private SimpleDateFormat dateFormat;

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

        categorySpinner.setAdapter(new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, Category.values()));
        categorySpinner.setSelection(0);

        repeatSpinner.setAdapter(new ArrayAdapter<RepeatPeriod>(this, android.R.layout.simple_spinner_dropdown_item, RepeatPeriod.values()));
        repeatSpinner.setSelection(0);

//        List<ChallengeState> states = Arrays.asList(ChallengeState.values());
//        states.remove(3);
        stateSpinner.setAdapter(new ArrayAdapter<ChallengeState>(this, android.R.layout.simple_spinner_dropdown_item, ChallengeState.values()));
        stateSpinner.setSelection(0);

        recyclerView = findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddChallengeActivity();
//                Toast.makeText(getApplicationContext(), "fab clicked", Toast.LENGTH_LONG)
//                        .show();
            }
        });

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        challenges = new ArrayList<>();
        adapter = new ChallengeAdapter(challenges);
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> silentSignInAndGetChallenges());

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if (ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null) {
                startLoginActivity();
                finish();
                return;
            }

            Log.d("TOKEN ", account.getIdToken()==null ? "null" : account.getIdToken());

//            Toast.makeText(this, "successful sign in, account not null? main", Toast.LENGTH_LONG)
//                    .show();

//            requestQueue =  Volley.newRequestQueue(this);

//            Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
//            if (task.isSuccessful()) {
//                // There's immediate result available.
////                addUser(task.getResult().getIdToken());
//            } else {
//                task.addOnCompleteListener(
////                        this,
////                        task1 -> addUser(SignInClient.getTokenIdFromResult(task1)));
//            }


        }
        else {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
            startLoginActivity();
            finish();
            return;
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

    private void silentSignInAndGetChallenges() {
        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getChallengesFromServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getChallengesFromServer(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getChallengesFromServer(String token) {
        progressBar.setVisibility(View.VISIBLE);
        challenges.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges?" + getUrlParameters(),
                null,
                response -> {
                    try {
                        if (response.length()==0){
                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = (JSONObject) response.get(i);

                                Integer id = jsonObject.getInt("id");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String city = jsonObject.getString("city");
                                RepeatPeriod repeat = RepeatPeriod.valueOf(jsonObject.getString("repeatPeriod"));
                                Category category = Category.valueOf(jsonObject.getString("category"));
                                AccessType access = AccessType.valueOf(jsonObject.getString("accessType"));
                                Date start = dateFormat.parse(jsonObject.getString("startDate"));
                                Date end = dateFormat.parse(jsonObject.getString("endDate"));
                                ChallengeState state = ChallengeState.valueOf(jsonObject.getString("challengeState"));
                                ConfirmationType confirmation = ConfirmationType.valueOf(jsonObject.getString("confirmationType"));
                                Boolean isUserParticipant = jsonObject.getBoolean("userParticipant");

                                Integer goal = 0;
                                if(confirmation == ConfirmationType.TIMER_TASK){
                                    goal = jsonObject.getInt("goal");
                                }
                                Boolean isMoreBetter = true;
                                if(confirmation == ConfirmationType.COUNTER_TASK){
                                    isMoreBetter = jsonObject.getBoolean("moreBetter");
                                }

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
                                c.setMoreBetter(isMoreBetter);
                                c.setStartDate(start);
                                c.setEndDate(end);
                                c.setUserParticipant(isUserParticipant);

                                challenges.add(c);
                                adapter.notifyDataSetChanged();

                                Log.d("jsonObject", challenges.get(i).toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("", "request response:failed message=" + e.getMessage());
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
        String url = "";
        if(categorySpinner.getSelectedItem() != Category.ALL){
            url += "&category=" + categorySpinner.getSelectedItem();
        }
        if(repeatSpinner.getSelectedItem() != RepeatPeriod.ALL){
            url += "&repeat=" + repeatSpinner.getSelectedItem();
        }
        if(stateSpinner.getSelectedItem() != ChallengeState.ALL){
            url += "&state=" + stateSpinner.getSelectedItem();
        }
        String city = cityEdit.getText().toString();
        if(!city.isEmpty()){
            url += "&city=" + city;
        }
        String search = searchEdit.getText().toString();
        if(!search.isEmpty()){
            url += "&search=" + search;
        }
        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        Long userId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        Log.d("USER_ID ", String.valueOf(userId));
    }
}
