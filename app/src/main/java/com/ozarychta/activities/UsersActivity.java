package com.ozarychta.activities;

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
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.model.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setTitle(R.string.find_users);

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

        searchBtn.setOnClickListener(v -> silentSignInAndGetUsers());

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if (ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
//            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//            if (account == null) {
//                startLoginActivity();
//                finish();
//                return;
//            }
//
//            Log.d("TOKEN ", account.getIdToken()==null ? "null" : account.getIdToken());
        } else {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
            startLoginActivity();
            finish();
            return;
        }

        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        signedUserId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        if (signedUserId == -1){
            Toast.makeText(getApplicationContext(), "Account error. Please sign in.", Toast.LENGTH_LONG)
                    .show();
            startLoginActivity();
            finish();
            return;
        }
    }

    private void silentSignInAndGetUsers() {
        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getUsersFromServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> {
                        getUsersFromServer(SignInClient.getTokenIdFromResult(task1));
                    });
        }
    }

    private void getUsersFromServer(String token) {
        progressBar.setVisibility(View.VISIBLE);
        users.clear();

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
                        if (response.length()==0){
                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.d("response", response.toString(2));

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = (JSONObject) response.get(i);

                                Long id = jsonObject.getLong("id");
                                String username = jsonObject.getString("username");
                                String aboutMe = jsonObject.getString("aboutMe");
                                String mainGoal = jsonObject.getString("mainGoal");
                                Integer points = jsonObject.getInt("rankingPoints");
                                Integer strike = jsonObject.getInt("highestStreak");

                                User u = new User(id, username, aboutMe, mainGoal, points, strike);

                                if(!signedUserId.equals(u.getId())){
                                    users.add(u);
                                }
                                adapter.notifyDataSetChanged();

                                Log.d("jsonObject user", users.get(i).getUsername());

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

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
