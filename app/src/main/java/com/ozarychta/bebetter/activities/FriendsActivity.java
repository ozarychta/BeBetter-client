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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ozarychta.bebetter.adapters.EnumArrayAdapter;
import com.ozarychta.bebetter.viewmodels.FriendsViewModel;
import com.ozarychta.bebetter.adapters.FriendsViewPagerAdapter;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.utils.SignInClient;
import com.ozarychta.bebetter.enums.SortType;
import com.ozarychta.bebetter.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends BaseActivity {

    private ConnectivityManager connectivityManager;

    private Spinner sortTypeSpinner;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        searchBtn = findViewById(R.id.searchBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        sortTypeSpinner = findViewById(R.id.sortBySpinner);
        searchEdit = findViewById(R.id.searchEdit);

        sortTypeSpinner.setAdapter(new EnumArrayAdapter<SortType>(this, android.R.layout.simple_spinner_dropdown_item, SortType.values()));
        sortTypeSpinner.setSelection(0);

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
                startUsersActivity();
            }
        });

        following = new ArrayList<>();
        followers = new ArrayList<>();

        searchBtn.setOnClickListener(v -> silentSignInAnd(this::getFriends));

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        silentSignInAnd(this::getFriends);
    }

    private void startUsersActivity() {
        Intent i = new Intent(this, UsersActivity.class);
        startActivity(i);
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void getFriends(String token) {
            getFollowing(token);
            getFollowers(token);
    }

    private void getFollowing(String token) {
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
                                JSONObject jsonObject = (JSONObject) response.get(i);

                                Long id = jsonObject.getLong("id");
                                String username = jsonObject.getString("username");
                                String aboutMe = jsonObject.getString("aboutMe");
                                String mainGoal = jsonObject.getString("mainGoal");
                                Integer points = jsonObject.getInt("rankingPoints");
                                Integer strike = jsonObject.getInt("highestStreak");

                                User u = new User(id, username, aboutMe, mainGoal, points, strike);

                                following.add(u);

                                Log.d(this.getClass().getSimpleName() + " jsonObject following", following.get(i).getUsername());
                        }

                        friendsViewModel.setFollowingLiveData(following);
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

    private void getFollowers(String token) {
        progressBar.setVisibility(View.VISIBLE);
        followers.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/followers?" + getUrlParameters(),
                null,
                response -> {
                    try {
                        if (response.length()==0){
//                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
//                                    .show();
                            progressBar.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);

                                Long id = jsonObject.getLong("id");
                                String username = jsonObject.getString("username");
                                String aboutMe = jsonObject.getString("aboutMe");
                                String mainGoal = jsonObject.getString("mainGoal");
                                Integer points = jsonObject.getInt("rankingPoints");
                                Integer strike = jsonObject.getInt("highestStreak");

                                User u = new User(id, username, aboutMe, mainGoal, points, strike);

                                followers.add(u);

                                Log.d(this.getClass().getSimpleName() + " jsonObject followers", followers.get(i).getUsername());

                        }

                        friendsViewModel.setFollowersLiveData(followers);

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
        url += "&sortType=" + sortTypeSpinner.getSelectedItem();

        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        Long userId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        Log.d(this.getClass().getSimpleName() + " USER_ID ", String.valueOf(userId));
    }
}
