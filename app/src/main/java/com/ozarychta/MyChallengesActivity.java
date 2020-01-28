package com.ozarychta;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ozarychta.enums.Active;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.RepeatPeriod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyChallengesActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;

    private Spinner categorySpinner;
    private Spinner repeatSpinner;
    private Spinner activeSpinner;
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
        getSupportActionBar().setTitle(R.string.my_challenges);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        searchBtn = findViewById(R.id.searchBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        categorySpinner = findViewById(R.id.categorySpinner);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        activeSpinner = findViewById(R.id.activeSpinner);
        cityEdit = findViewById(R.id.cityEdit);
        searchEdit = findViewById(R.id.searchEdit);

        categorySpinner.setAdapter(new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, Category.values()));
        categorySpinner.setSelection(0);

        repeatSpinner.setAdapter(new ArrayAdapter<RepeatPeriod>(this, android.R.layout.simple_spinner_dropdown_item, RepeatPeriod.values()));
        repeatSpinner.setSelection(0);

        activeSpinner.setAdapter(new ArrayAdapter<Active>(this, android.R.layout.simple_spinner_dropdown_item, Active.values()));
        activeSpinner.setSelection(0);

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


        challenges = new ArrayList<>();
        adapter = new CustomAdapter(challenges);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        int userId = sharedPref.getInt(getString(R.string.user_id_field), -1);

        searchBtn.setOnClickListener(v -> getMyChallengesFromServer(userId));

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if (ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {

//            getMyChallengesFromServer(userId);

        }
        else {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void getMyChallengesFromServer(Integer userId) {
        progressBar.setVisibility(View.VISIBLE);
        challenges.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges" + "?creatorId=" + userId + getUrlParameters(),
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
                                String repeat = jsonObject.getString("repeatPeriod");
                                String category = jsonObject.getString("category");
                                Integer goal = 0;
                                if(ConfirmationType.valueOf(jsonObject.getString("confirmationType")) == ConfirmationType.TIMER_TASK){
                                    goal = jsonObject.getInt("goal");
                                }

                                Challenge c = new Challenge();
                                c.setTitle(title);
                                c.setCity(city);
                                c.setRepeatPeriod(RepeatPeriod.valueOf(repeat));
                                c.setCategory(Category.valueOf(category));
                                c.setGoal(goal);


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
                headers.put("authorization", "Bearer ");
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
        if(activeSpinner.getSelectedItem() != Active.ALL){
            url += "&active=" + ((Active)activeSpinner.getSelectedItem()).getBooleanValue();
        }
        String city = cityEdit.getText().toString();
        if(!city.isEmpty()){
            url += "&city=" + city;
        }
        String search = searchEdit.getText().toString();
        if(!search.isEmpty()){
            url += "&search=" + search;
        }
        Log.d("request", url);
        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        SharedPreferences sharedPref = getApplicationContext()
//                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
//        int userId = sharedPref.getInt(getString(R.string.user_id_field), -1);

//        Log.d("USER_ID ", String.valueOf(userId));
    }

    private void startAddChallengeActivity() {
        Intent i = new Intent(this, AddChallengeActivity.class);
        startActivity(i);
    }
}