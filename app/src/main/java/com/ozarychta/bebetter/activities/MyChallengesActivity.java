package com.ozarychta.bebetter.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.adapters.ChallengesViewPagerAdapter;
import com.ozarychta.bebetter.adapters.EnumArrayAdapter;
import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.enums.Category;
import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.models.Challenge;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.viewmodels.ChallengesViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyChallengesActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");

    private Spinner categorySpinner;
    private Spinner repeatSpinner;
    private Spinner stateSpinner;
    private EditText cityEdit;
    private EditText searchEdit;

    private Button searchBtn;
    private ProgressBar progressBar;

    private ArrayList<Challenge> created;
    private ArrayList<Challenge> joined;
    private ArrayList<Challenge> all;

    private ViewPager viewPager;
    private ChallengesViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    private ChallengesViewModel challengesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_challenges);
        getSupportActionBar().setTitle(R.string.my_challenges);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

        stateSpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ChallengeState.values()));
        stateSpinner.setSelection(0);

        viewPagerAdapter = new ChallengesViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        challengesViewModel = new ViewModelProvider(this).get(ChallengesViewModel.class);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddChallengeActivity();
            }
        });

        created = new ArrayList<>();
        joined = new ArrayList<>();
        all = new ArrayList<>();

        searchBtn.setOnClickListener(v -> silentSignInAnd(this::getChallenges));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        silentSignInAnd(this::getChallenges);
    }

    private void startAddChallengeActivity() {
        Intent i = new Intent(this, AddChallengeActivity.class);
        startActivity(i);
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void getChallenges(String token){
        getCreatedChallenges(token);
        getJoinedChallenges(token);
    }

    private void getJoinedChallenges(String token) {
        progressBar.setVisibility(View.VISIBLE);
        joined.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/users/challenges?" + getUrlParameters(),
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            progressBar.setVisibility(View.GONE);
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
                            Integer creatorId = jsonObject.getInt("creatorId");

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
                            c.setCreatorId(creatorId);

                            joined.add(c);
                            Log.d(this.getClass().getSimpleName() + " jsonObject challenge", joined.get(i).getTitle());
                        }

                        challengesViewModel.setJoinedLiveData(joined);
                        challengesViewModel.setAllLiveData(Stream.concat(created.stream(), joined.stream())
                                .distinct()
                                .collect(Collectors.toCollection(ArrayList::new)));

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
                headers.put("authorization", "Bearer " + token);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    private void getCreatedChallenges(String token) {
        progressBar.setVisibility(View.VISIBLE);
        created.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/users/created?" + getUrlParameters(),
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            progressBar.setVisibility(View.GONE);
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
                            Integer creatorId = jsonObject.getInt("creatorId");

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
                            c.setCreatorId(creatorId);

                            created.add(c);
                            Log.d(this.getClass().getSimpleName() + " jsonObject challenge", created.get(i).getTitle());
                        }

                        challengesViewModel.setCreatedLiveData(created);
                        challengesViewModel.setAllLiveData(Stream.concat(created.stream(), joined.stream())
                                .distinct()
                                .collect(Collectors.toCollection(ArrayList::new)));

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
                headers.put("authorization", "Bearer " + token);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    private String getUrlParameters() {
        String url = "";
//        url += "&type=" + AccessType.PRIVATE;

        if (categorySpinner.getSelectedItem() != Category.ALL) {
            url += "&category=" + ((Category)categorySpinner.getSelectedItem()).name();
        }
        if (repeatSpinner.getSelectedItem() != RepeatPeriod.ALL) {
            url += "&repeat=" + ((RepeatPeriod)repeatSpinner.getSelectedItem()).name();
        }
//        if (stateSpinner.getSelectedItem() != ChallengeState.ALL) {
            url += "&state=" + ((ChallengeState)stateSpinner.getSelectedItem()).name();
//        }
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
