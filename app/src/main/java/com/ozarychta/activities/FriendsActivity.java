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

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ozarychta.FriendsViewModel;
import com.ozarychta.FriendsViewPagerAdapter;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.SortType;
import com.ozarychta.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;

    private Spinner sortBySpinner;
    private EditText searchEdit;

    private Button searchBtn;
    private ProgressBar progressBar;

    private ArrayList<User> following;
    private ArrayList<User> followers;

    private ViewPager viewPager;
    private FriendsViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    private FriendsViewModel friendsViewModel;

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

        viewPagerAdapter = new FriendsViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startAddFriendActivity();
            }
        });

        following = new ArrayList<>();
        followers = new ArrayList<>();

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
            getFollowingFromServer(task.getResult().getIdToken());
            getFollowersFromServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> {
                        getFollowingFromServer(SignInClient.getTokenIdFromResult(task1));
                        getFollowersFromServer(SignInClient.getTokenIdFromResult(task1));
                    });
        }
    }

    private void getFollowingFromServer(String token) {
        progressBar.setVisibility(View.VISIBLE);
        following.clear();
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

                                following.add(u);

                                Log.d("jsonObject following", following.get(i).getUsername());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        friendsViewModel.setFollowingLiveData(following);

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

    private void getFollowersFromServer(String token) {
        progressBar.setVisibility(View.VISIBLE);
        followers.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/followers?" + getUrlParameters(),
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

                                followers.add(u);

                                Log.d("jsonObject followers", followers.get(i).getUsername());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        friendsViewModel.setFollowersLiveData(followers);

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
        Long userId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        Log.d("USER_ID ", String.valueOf(userId));
    }
}
