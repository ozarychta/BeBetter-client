package com.ozarychta;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.CategoryDTO;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.MoreOrLess;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.enums.RepeatPeriodDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AddChallengeActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private SimpleDateFormat dateFormat;

    private ConnectivityManager connectivityManager;

    private TextView goalTextView;
    private TextView moreOrLessTextView;

    private EditText titleEdit;
    private EditText descEdit;
    private EditText cityEdit;
    private EditText goalEdit;

    private Spinner categorySpinner;
    private Spinner repeatSpinner;
    private Spinner confirmationSpinner;
    private Spinner accessSpinner;
    private Spinner moreOrLessSpinner;

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    private Button addBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);
        getSupportActionBar().setTitle(R.string.add_challenge);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        goalTextView = findViewById(R.id.goalTextView);
        goalTextView.setVisibility(View.GONE);
        moreOrLessTextView = findViewById(R.id.moreOrLessTextView);
        moreOrLessTextView.setVisibility(View.GONE);

        titleEdit = findViewById(R.id.titleEdit);
        descEdit = findViewById(R.id.descriptionEdit);
        cityEdit = findViewById(R.id.cityEdit);
        goalEdit = findViewById(R.id.goalEdit);
        goalEdit.setVisibility(View.GONE);

        categorySpinner = findViewById(R.id.categorySpinner);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        confirmationSpinner = findViewById(R.id.confirmationSpinner);
        accessSpinner = findViewById(R.id.accessSpinner);
        moreOrLessSpinner = findViewById(R.id.moreOrLessSpinner);
        moreOrLessSpinner.setVisibility(View.GONE);

        categorySpinner.setAdapter(new ArrayAdapter<CategoryDTO>(this, android.R.layout.simple_spinner_dropdown_item, CategoryDTO.values()));
        categorySpinner.setSelection(0);

        repeatSpinner.setAdapter(new ArrayAdapter<RepeatPeriodDTO>(this, android.R.layout.simple_spinner_dropdown_item, RepeatPeriodDTO.values()));
        repeatSpinner.setSelection(0);

        confirmationSpinner.setAdapter(new ArrayAdapter<ConfirmationType>(this, android.R.layout.simple_spinner_dropdown_item, ConfirmationType.values()));
        confirmationSpinner.setSelection(0);
        confirmationSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ConfirmationType c = (ConfirmationType) confirmationSpinner.getSelectedItem();
                        if(c == ConfirmationType.CHECK_TASK){
                            goalTextView.setVisibility(View.GONE);
                            moreOrLessTextView.setVisibility(View.GONE);
                            goalEdit.setVisibility(View.GONE);
                            moreOrLessSpinner.setVisibility(View.GONE);
                        } else if(c == ConfirmationType.COUNTER_TASK){
                            goalTextView.setVisibility(View.GONE);
                            moreOrLessTextView.setVisibility(View.VISIBLE);
                            goalEdit.setVisibility(View.GONE);
                            moreOrLessSpinner.setVisibility(View.VISIBLE);
                        } else if(c == ConfirmationType.TIMER_TASK){
                            goalTextView.setVisibility(View.VISIBLE);
                            moreOrLessTextView.setVisibility(View.GONE);
                            goalEdit.setVisibility(View.VISIBLE);
                            moreOrLessSpinner.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        moreOrLessSpinner.setAdapter(new ArrayAdapter<MoreOrLess>(this, android.R.layout.simple_spinner_dropdown_item, MoreOrLess.values()));
        moreOrLessSpinner.setSelection(0);

        accessSpinner.setAdapter(new ArrayAdapter<AccessType>(this, android.R.layout.simple_spinner_dropdown_item, AccessType.values()));
        accessSpinner.setSelection(0);

        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);

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

        if (title.isEmpty() || desc.isEmpty() || city.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_fields_error), Toast.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("description", desc);
        requestBody.put("city", city);
        requestBody.put("accessType", accessSpinner.getSelectedItem().toString());
        requestBody.put("category", categorySpinner.getSelectedItem().toString());
        requestBody.put("repeatPeriod", repeatSpinner.getSelectedItem().toString());

        ConfirmationType confirmationType = (ConfirmationType) confirmationSpinner.getSelectedItem();
        requestBody.put("confirmationType", confirmationType.toString());

        Integer goal = 0;
        Boolean isMoreBetter = true;

        if(confirmationType == ConfirmationType.COUNTER_TASK){
            isMoreBetter = ((MoreOrLess)moreOrLessSpinner.getSelectedItem()).getBooleanValue();
        } else if(confirmationType == ConfirmationType.TIMER_TASK){
            String goalString = goalEdit.getText().toString();
            if(!goalString.isEmpty()){
                goal = Integer.valueOf(goalString);
            }
        }

        requestBody.put("goal", goal);
        requestBody.put("moreBetter", isMoreBetter);

        Log.d("startDatePicker.getYear()", String.valueOf(startDatePicker.getYear()));
        Log.d("startDatePicker.getMonth()", String.valueOf(startDatePicker.getMonth()));
        Log.d("startDatePicker.getYear()", String.valueOf(startDatePicker.getDayOfMonth()));

        Log.d("startDatePicker.getYear()", String.valueOf(startDatePicker.getYear()));
        Log.d("startDatePicker.getMonth()", String.valueOf(startDatePicker.getMonth()));
        Log.d("startDatePicker.getYear()", String.valueOf(startDatePicker.getDayOfMonth()));

        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, startDatePicker.getYear());
        start.set(Calendar.MONTH, startDatePicker.getMonth());
        start.set(Calendar.DAY_OF_MONTH, startDatePicker.getDayOfMonth());

        requestBody.put("startDate", dateFormat.format(start.getTime()));

        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, endDatePicker.getYear());
        end.set(Calendar.MONTH, endDatePicker.getMonth());
        end.set(Calendar.DAY_OF_MONTH, endDatePicker.getDayOfMonth());

        requestBody.put("endDate", dateFormat.format(end.getTime()));

        JSONObject jsonRequestBody = new JSONObject(requestBody);
        Log.d("request body", "\n\n" + jsonRequestBody.toString() + "\n\n");

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
