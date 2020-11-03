package com.ozarychta.bebetter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.adapters.UserAdapter;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.utils.SignInClient;
import com.ozarychta.bebetter.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;
    private Long signedUserId;

    private EditText searchEdit;
    private Button searchBtn;

    private ProgressBar progressBar;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    private ArrayList<User> users;
    private TextView noResultsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setTitle(R.string.find_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        searchBtn = findViewById(R.id.searchBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        searchEdit = findViewById(R.id.searchEdit);

        recyclerView = findViewById(R.id.users_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        users = new ArrayList<>();
        adapter = new UserAdapter(users);
        recyclerView.setAdapter(adapter);

        noResultsLabel = findViewById(R.id.noResultsLabel);
        noResultsLabel.setVisibility(View.GONE);

        searchBtn.setOnClickListener(v -> silentSignInAnd(this::getUsers));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        signedUserId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        if (signedUserId == -1) {
            Toast.makeText(getApplicationContext(), "Account error. Please sign in.", Toast.LENGTH_LONG)
                    .show();
            startLoginActivity();
            finish();
            return;
        }
    }

    private void getUsers(String token) {
        progressBar.setVisibility(View.VISIBLE);
        users.clear();
        noResultsLabel.setVisibility(View.GONE);
        String search = searchEdit.getText().toString();

        if (search.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_search), Toast.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/users?" + getUrlParameters(),
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            noResultsLabel.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.d(this.getClass().getSimpleName() + " response", response.toString(2));

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = (JSONObject) response.get(i);

                            Long id = jsonObject.getLong("id");
                            String username = jsonObject.getString("username");
                            String aboutMe = jsonObject.getString("aboutMe");
                            String mainGoal = jsonObject.getString("mainGoal");
                            Integer points = jsonObject.getInt("rankingPoints");
                            Integer strike = jsonObject.getInt("highestStreak");

                            User u = new User(id, username, aboutMe, mainGoal, points, strike);

                            if (!signedUserId.equals(u.getId())) {
                                users.add(u);
                            }
                            adapter.notifyDataSetChanged();

                            Log.d(this.getClass().getSimpleName() + " jsonObject user", jsonObject.toString(2));
                        }

                        if(users.isEmpty()){
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

        String search = searchEdit.getText().toString();
        if (!search.isEmpty()) {
            url += "&search=" + search;
        }

        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        Long userId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        Log.d(this.getClass().getSimpleName() + " USER_ID ", String.valueOf(userId));
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}

