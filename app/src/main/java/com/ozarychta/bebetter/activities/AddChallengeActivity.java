package com.ozarychta.bebetter.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.Task;
import com.ozarychta.bebetter.adapters.EnumArrayAdapter;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.utils.SignInClient;
import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.enums.CategoryDTO;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriodDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AddChallengeActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private SimpleDateFormat dateFormat;

    private ConnectivityManager connectivityManager;

    private TextView goalTextView;

    private EditText titleEdit;
    private EditText descEdit;
    private EditText cityEdit;
    private EditText goalEdit;

    private Spinner categorySpinner;
    private Spinner repeatSpinner;
    private Spinner confirmationSpinner;
    private Spinner accessSpinner;

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    private Button addBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);
        getSupportActionBar().setTitle(R.string.add_challenge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        goalTextView = findViewById(R.id.goalTextView);
        goalTextView.setVisibility(View.GONE);

        titleEdit = findViewById(R.id.titleEdit);
        descEdit = findViewById(R.id.descriptionEdit);
        cityEdit = findViewById(R.id.cityEdit);
        goalEdit = findViewById(R.id.goalEdit);
        goalEdit.setVisibility(View.GONE);

        categorySpinner = findViewById(R.id.categorySpinner);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        confirmationSpinner = findViewById(R.id.confirmationSpinner);
        accessSpinner = findViewById(R.id.accessSpinner);

        categorySpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CategoryDTO.values()));
        categorySpinner.setSelection(0);

        repeatSpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, RepeatPeriodDTO.values()));
        repeatSpinner.setSelection(0);

        confirmationSpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ConfirmationType.values()));
        confirmationSpinner.setSelection(0);
        confirmationSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ConfirmationType c = (ConfirmationType) confirmationSpinner.getSelectedItem();
                        if (c == ConfirmationType.CHECK_TASK) {
                            goalTextView.setVisibility(View.GONE);
                            goalEdit.setVisibility(View.GONE);
                        } else if (c == ConfirmationType.COUNTER_TASK) {
                            goalTextView.setVisibility(View.VISIBLE);
                            goalEdit.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        accessSpinner.setAdapter(new EnumArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, AccessType.values()));
        accessSpinner.setSelection(0);

        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);

        Calendar c = Calendar.getInstance();
        startDatePicker.setMinDate(c.getTimeInMillis());
        endDatePicker.setMinDate(c.getTimeInMillis());

        addBtn = findViewById(R.id.addBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        addBtn.setOnClickListener(v -> silentSignInAndAddChallenge());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private void silentSignInAndAddChallenge() {
        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            addChallengeToServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> addChallengeToServer(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void addChallengeToServer(String idToken) {
        String title = titleEdit.getText().toString();
        String desc = descEdit.getText().toString();
        String city = cityEdit.getText().toString();
        String goalText = goalEdit.getText().toString();

        if (Strings.isEmptyOrWhitespace(title) || Strings.isEmptyOrWhitespace(desc) || Strings.isEmptyOrWhitespace(city)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_fields_error), Toast.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("description", desc);
        requestBody.put("city", city);
        requestBody.put("accessType", ((AccessType) accessSpinner.getSelectedItem()).name());
        requestBody.put("category", ((CategoryDTO) categorySpinner.getSelectedItem()).name());
        requestBody.put("repeatPeriod", ((RepeatPeriodDTO) repeatSpinner.getSelectedItem()).name());

        ConfirmationType confirmationType = (ConfirmationType) confirmationSpinner.getSelectedItem();
        requestBody.put("confirmationType", confirmationType.name());

        Integer goal = 0;
        if (confirmationType == ConfirmationType.COUNTER_TASK) {
            if (!Strings.isEmptyOrWhitespace(goalText)) {
                goal = Integer.valueOf(goalText);
            }
        }
        requestBody.put("goal", goal);

        Log.d(this.getClass().getSimpleName() + " startDatePicker yyyy mm dd",
                startDatePicker.getYear() + " " + startDatePicker.getMonth() + " " + startDatePicker.getDayOfMonth());
        Log.d(this.getClass().getSimpleName() + " startDatePicker yyyy mm dd",
                endDatePicker.getYear() + " " + endDatePicker.getMonth() + " " + endDatePicker.getDayOfMonth());
        LocalDate startDate = LocalDate.of(startDatePicker.getYear(), startDatePicker.getMonth()+1, startDatePicker.getDayOfMonth());
        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);

        requestBody.put("startDate", start.toString());

        LocalDate endDate = LocalDate.of(endDatePicker.getYear(), endDatePicker.getMonth()+1, endDatePicker.getDayOfMonth());
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        if (end.isBefore(start)) {
            Toast.makeText(getApplicationContext(), getString(R.string.end_before_start), Toast.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        requestBody.put("endDate", end.toString());

        JSONObject jsonRequestBody = new JSONObject(requestBody);
        Log.d(this.getClass().getSimpleName() + " request body", "\n\n" + jsonRequestBody.toString() + "\n\n");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/challenges",
                jsonRequestBody,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        Toast.makeText(getApplicationContext(), getString(R.string.added_challenge), Toast.LENGTH_LONG)
                                .show();

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
                    showVolleyErrorMessage(error);
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

    private void showVolleyErrorMessage(VolleyError error) {
        if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
            Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Log.d(this.getClass().getName() + ": Error code :", String.valueOf(error.networkResponse.statusCode));
        try {
            String errorResponse = new String(error.networkResponse.data, "UTF-8");
            Log.d(this.getClass().getName() + ": Error response :", errorResponse);

            if (errorResponse.contains("ConstraintViolationException")) {
                Toast.makeText(getApplicationContext(), getString(R.string.exact_same_challenge_already_exists), Toast.LENGTH_LONG)
                        .show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG)
                .show();
    }
}
