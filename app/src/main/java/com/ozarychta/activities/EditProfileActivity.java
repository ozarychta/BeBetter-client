package com.ozarychta.activities;

import android.app.Activity;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.Task;
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity{

    private ConnectivityManager connectivityManager;

    private EditText usernameEdit;
    private EditText aboutMeEdit;
    private EditText mainGoalEdit;

    private Button editBtn;
    private ProgressBar progressBar;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userinfo);
        getSupportActionBar().setTitle(R.string.edit_user_info);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        usernameEdit = findViewById(R.id.usernameEdit);
        aboutMeEdit = findViewById(R.id.aboutMeEdit);
        mainGoalEdit = findViewById(R.id.mainGoalEdit);

        user = (User) getIntent().getSerializableExtra("USER");
        usernameEdit.setText(user.getUsername());
        aboutMeEdit.setText(user.getAboutMe());
        mainGoalEdit.setText(user.getMainGoal());

        editBtn = findViewById(R.id.editBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        editBtn.setOnClickListener(v -> silentSignInAndEditUserInfo());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void silentSignInAndEditUserInfo() {
        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            editUserInfo(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> editUserInfo(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void editUserInfo(String idToken) {
        String username = usernameEdit.getText().toString();
        String aboutMe = aboutMeEdit.getText().toString();
        String mainGoal = mainGoalEdit.getText().toString();

        if (Strings.isEmptyOrWhitespace(username)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_username), Toast.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("aboutMe", aboutMe);
        requestBody.put("mainGoal", mainGoal);

        JSONObject jsonRequestBody = new JSONObject(requestBody);
        Log.d("request body", "\n\n" + jsonRequestBody.toString() + "\n\n");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                "https://be-better-server.herokuapp.com/users/" + user.getId(),
                jsonRequestBody,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        Toast.makeText(getApplicationContext(), R.string.edited_user_info, Toast.LENGTH_LONG)
                                .show();

                        user.setUsername(username);
                        user.setAboutMe(aboutMe);
                        user.setMainGoal(mainGoal);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("USER",user);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("", "request response:failed message=" + e.getMessage());
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
                headers.put("authorization", "Bearer " + idToken);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }
}
