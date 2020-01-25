package com.ozarychta;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private boolean connectedToNetwork;
    private RequestQueue requestQueue;

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
        getSupportActionBar().setTitle(R.string.challenges);

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


        challenges = new ArrayList<>();
        adapter = new CustomAdapter(challenges);
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> getChallengesFromServer());

        connectedToNetwork = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        connectedToNetwork = connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected();

        if (connectedToNetwork) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null) {
                startLoginActivity();
                finish();
                return;
            }

//            Log.d("TOKEN ", account.getIdToken());
//            LoginActivity.silentSignIn(this, this::sendAddUser, "MainActivity");

//            Toast.makeText(this, "successful sign in, account not null? main", Toast.LENGTH_LONG)
//                    .show();

            requestQueue =  Volley.newRequestQueue(this);





        }
        else {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void getChallengesFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        challenges.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges",
                null,
                response -> {
                    try {

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
                    if (!connectedToNetwork){
                        Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                                .show();
                    }else
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                                .show();
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
        requestQueue.add(jsonArrayRequest);
    }
}
