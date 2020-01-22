package com.ozarychta;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private TextView textView;
    private Button searchBtn;

//    private static RecyclerView.Adapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//    private static RecyclerView recyclerView;
//    private static ArrayList<Challenge> challenges = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.challenges);

        textView = findViewById(R.id.textView);
        searchBtn = findViewById(R.id.searchBtn);


//        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
////        recyclerView.setHasFixedSize(false);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
////        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        adapter = new CustomAdapter(challenges);
//        recyclerView.setAdapter(adapter);

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
            Toast.makeText(this, getString(R.string.not_connected_to_network), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void getChallengesFromServer() {
        Context context = this;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges",
                null,
                response -> {
                    try {
                        String s = "";

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                s = s+jsonObject +"\n\n";
                                Log.d("jsonObject", response.get(i).toString());
                                Integer id = jsonObject.getInt("id");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String city = jsonObject.getString("city");
                                String repeat = jsonObject.getString("repeatPeriod");
                                String category = jsonObject.getString("category");
//                                Integer goal = jsonObject.getInt("goal");

//                                city = (jsonObject.getString("city") != null) ? jsonObject.getString("city"): "";

                                Challenge c = new Challenge();
                                c.setTitle(title);
                                c.setCity(city);
                                c.setRepeatPeriod(RepeatPeriod.valueOf(repeat));
                                c.setCategory(Category.valueOf(category));
//                                c.setGoal(goal);


//                                challenges.add(c);
//                                adapter = new CustomAdapter(challenges);
//                                recyclerView.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        textView.setText(s);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("", "request response:failed message=" + e.getMessage());
                    }
                },
                error -> {
//                    if (!connected){
//                        Toast.makeText(context, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
//                                .show();
//                    }else
//                        Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
//                                .show();
//                    Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
//                    Log.w(TAG, "request response:failed msg=" + error.getMessage());
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
