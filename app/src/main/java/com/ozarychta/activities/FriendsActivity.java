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
import com.ozarychta.enums.SortType;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.FriendAdapter;
import com.ozarychta.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;

    private Spinner sortBySpinner;
    private EditText searchEdit;

    private Button searchBtn;
    private ProgressBar progressBar;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<User> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setTitle(R.string.friends);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        searchBtn = findViewById(R.id.searchBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        sortBySpinner = findViewById(R.id.sortBySpinner);
        searchEdit = findViewById(R.id.searchEdit);

        sortBySpinner.setAdapter(new ArrayAdapter<SortType>(this, android.R.layout.simple_spinner_dropdown_item, SortType.values()));
        sortBySpinner.setSelection(0);

        recyclerView = findViewById(R.id.friends_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startAddFriendActivity();
            }
        });

        friends = new ArrayList<>();
        adapter = new FriendAdapter(friends);
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> silentSignInAndGetFriends());

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

        silentSignInAndGetFriends();
    }

//    private void startAddFriendActivity() {
//        Intent i = new Intent(this, AddChallengeActivity.class);
//        startActivity(i);
//    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void silentSignInAndGetFriends() {
        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getFriendsFromServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getFriendsFromServer(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getFriendsFromServer(String token) {
        progressBar.setVisibility(View.VISIBLE);
        friends.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/following?" + getUrlParameters(),
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

                                Long id = jsonObject.getLong("id");
                                String username = jsonObject.getString("username");
                                String aboutMe = jsonObject.getString("aboutMe");
                                String mainGoal = jsonObject.getString("mainGoal");
                                Integer points = jsonObject.getInt("rankingPoints");
                                Integer strike = jsonObject.getInt("highestStreak");

                                User u = new User(id, username, aboutMe, mainGoal, points, strike);

                                friends.add(u);
                                adapter.notifyDataSetChanged();

                                Log.d("jsonObject", friends.get(i).toString());

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
//        url += "&sortBy=" + sortBySpinner.getSelectedItem();
//
//        String search = searchEdit.getText().toString();
//        if(!search.isEmpty()){
//            url += "&search=" + search;
//        }
        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        int userId = sharedPref.getInt(getString(R.string.user_id_field), -1);

        Log.d("USER_ID ", String.valueOf(userId));
    }
}
