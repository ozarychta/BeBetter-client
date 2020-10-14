package com.ozarychta.bebetter.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.adapters.CommentAdapter;
import com.ozarychta.bebetter.adapters.DayAdapter;
import com.ozarychta.bebetter.data.Reminder;
import com.ozarychta.bebetter.data.ReminderDatabase;
import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.enums.Category;
import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.models.Challenge;
import com.ozarychta.bebetter.models.Comment;
import com.ozarychta.bebetter.models.Day;
import com.ozarychta.bebetter.receivers.AlarmReceiver;
import com.ozarychta.bebetter.utils.ApplicationExecutor;
import com.ozarychta.bebetter.utils.ServerRequestUtil;
import com.ozarychta.bebetter.utils.SignInClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class ChallengeActivity extends BaseActivity {

    private static final Integer DEFAULT_DAYS_NUM = 7;

    private DateTimeFormatter simpleDateFormatter = DateTimeFormatter.ofPattern("dd.MM");
    private DateTimeFormatter basicDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");

    private ConnectivityManager connectivityManager;

    private Long challengeIdFromIntent;
    private String titleFromIntent;
    private Challenge challenge;
    private Day today;

    private TextView goalText;

    private TextView goalLabel;

    private TextView titleText;
    private TextView descText;
    private TextView cityText;

    private TextView categoryText;
    private TextView repeatText;
    private TextView confirmationText;
    private TextView accessText;

    private TextView startText;
    private TextView endText;

    private Button joinBtn;
    private Button statisticsBtn;
    private Button showCommentsBtn;
    private Button hideCommentsBtn;

    private TextView daysLabel;
    private TextView notStartedYetLabel;
    private LinearLayout daysLayout;

    private MaterialCardView todayCard;
    private TextView todayDate;
    private ToggleButton todayToggle;

    private LinearLayout counterLinearLayout;
    private Button minusBtn;
    private Button plusBtn;
    private TextView counterText;

    private RecyclerView.Adapter daysAdapter;
    private RecyclerView.LayoutManager daysLayoutManager;
    private RecyclerView daysRecyclerView;
    private ArrayList<Day> pastDays;

    private RecyclerView.Adapter commentsAdapter;
    private RecyclerView.LayoutManager commentsLayoutManager;
    private RecyclerView commentsRecyclerView;
    private ArrayList<Comment> comments;

    private TextView noCommentsLabel;

    private LinearLayout commentsLinearLayout;
    private ProgressBar progressBar;
    private ProgressBar addCommentProgressBar;

    private Button addCommentBtn;
    private EditText commentEdit;

    private ScrollView scrollView;

    private TextView reminderLabel;
    private MaterialCardView reminderCardView;
    private ToggleButton reminderToggle;
    private TextView reminderTimeTextView;


    private AlarmManager alarmManager;
    private PendingIntent reminderPendingIntent;

    private TextView noNetworkLabel;
    private Button refreshBtn;

    private Long signedUserId;

    private ReminderDatabase reminderDatabase;
    private Reminder reminder;
    private Long reminderId;

    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        getSupportActionBar().setTitle(R.string.challenge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        challengeIdFromIntent = getIntent().getLongExtra("CHALLENGE_ID", -1);
        titleFromIntent = getIntent().getStringExtra("TITLE");
        challenge = new Challenge();
        Log.d(this.getClass().getSimpleName() + " challenge id ", challengeIdFromIntent.toString());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        executor = ApplicationExecutor.getInstance().getExecutor();

        titleText = findViewById(R.id.titleTextView);
        if(titleFromIntent != null){
            titleText.setText(titleFromIntent);
        }

        descText = findViewById(R.id.descriptionTextView);
        cityText = findViewById(R.id.cityTextView);

        categoryText = findViewById(R.id.categoryTextView);
        repeatText = findViewById(R.id.repeatTextView);
        confirmationText = findViewById(R.id.confirmationTextView);
        accessText = findViewById(R.id.accessTextView);

        goalText = findViewById(R.id.goalTextView);
        goalLabel = findViewById(R.id.goalLabel);

        startText = findViewById(R.id.startTextView);
        endText = findViewById(R.id.endTextView);

        joinBtn = findViewById(R.id.joinBtn);
        statisticsBtn = findViewById(R.id.statisticsBtn);
        showCommentsBtn = findViewById(R.id.showCommentsBtn);
        hideCommentsBtn = findViewById(R.id.hideCommentsBtn);
        addCommentBtn = findViewById(R.id.addCommentBtn);

        commentsLinearLayout = findViewById(R.id.commentsLayout);
        scrollView = findViewById(R.id.scrollView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        addCommentProgressBar = findViewById(R.id.addCommentProgressBar);
        addCommentProgressBar.setVisibility(View.GONE);

        daysLabel = findViewById(R.id.daysLabel);
        daysLayout = findViewById(R.id.daysLinearLayout);
        todayCard = findViewById(R.id.today);
        todayCard.setVisibility(View.GONE);

        daysRecyclerView = findViewById(R.id.past_days_recycler_view);
        daysLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        daysRecyclerView.setLayoutManager(daysLayoutManager);
        daysRecyclerView.setItemAnimator(new DefaultItemAnimator());

        pastDays = new ArrayList<>();
        daysAdapter = new DayAdapter(pastDays);
        daysRecyclerView.setAdapter(daysAdapter);

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentsRecyclerView.setLayoutManager(commentsLayoutManager);
        commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        comments = new ArrayList<>();
        commentsAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setAdapter(commentsAdapter);

        noCommentsLabel = findViewById(R.id.noCommentsLabel);

        notStartedYetLabel = findViewById(R.id.notStartedYetLabel);
        notStartedYetLabel.setVisibility(View.GONE);

        noNetworkLabel = findViewById(R.id.noNetworkLabel);
        noNetworkLabel.setVisibility(View.GONE);

        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(v -> {
            silentSignInAndGetChallenge();
        });
        refreshBtn.setVisibility(View.GONE);

        todayDate = todayCard.findViewById(R.id.dateTextView);
        todayToggle = todayCard.findViewById(R.id.toggleButton);
        todayToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) return;

                if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG)
                            .show();
                }
                today.setDone(isChecked);
                silentSignInAndSaveChange();
            }
        });

        counterLinearLayout = todayCard.findViewById(R.id.counterLinearLayout);
        counterText = todayCard.findViewById(R.id.counterTextView);

        minusBtn = todayCard.findViewById(R.id.minusButton);
        minusBtn.setOnClickListener(v -> {
            if (!ServerRequestUtil.isConnectedToNetwork(connectivityManager)) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG)
                        .show();
            }

            Integer counter = Integer.valueOf(counterText.getText().toString());
            if (counter > 0) {
                counter -= 1;
                today.setCurrentStatus(counter);
                counterText.setText(String.valueOf(counter));
                silentSignInAndSaveChange();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_less_that_zero), Toast.LENGTH_LONG)
                        .show();
            }
        });

        plusBtn = todayCard.findViewById(R.id.plusButton);
        plusBtn.setOnClickListener(v -> {
            Integer counter = Integer.valueOf(counterText.getText().toString()) + 1;
            today.setCurrentStatus(counter);
            counterText.setText(String.valueOf(counter));
            silentSignInAndSaveChange();
        });

        commentEdit = findViewById(R.id.commentEdit);
        hideCommentsBtn.setVisibility(View.GONE);
        commentsLinearLayout.setVisibility(View.GONE);
        noCommentsLabel.setVisibility(View.GONE);

        reminderDatabase = ReminderDatabase.getInstance(this);

        reminderLabel = findViewById(R.id.reminderLabel);
        reminderLabel.setVisibility(View.GONE);
        reminderCardView = findViewById(R.id.reminderCardView);
        reminderCardView.setVisibility(View.GONE);

        reminderToggle = findViewById(R.id.reminderToggleButton);
        reminderTimeTextView = findViewById(R.id.reminderTimeTextView);

        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref_filename),Context.MODE_PRIVATE);
        signedUserId = sharedPref.getLong(getString(R.string.user_id_field), -1);

        if (signedUserId == -1){
            Toast.makeText(getApplicationContext(), getString(R.string.sign_in_result_error), Toast.LENGTH_LONG)
                    .show();
        }

        reminder = reminderDatabase.reminderDao().findByChallengeIdAndUserId(challengeIdFromIntent, signedUserId);

        if(reminder == null){
            reminder = new Reminder(challengeIdFromIntent, signedUserId, false, 12, 0, "", "");
            reminderId = reminderDatabase.reminderDao().insert(reminder);
        } else {
            reminderId = reminder.getId();
        }

        reminderTimeTextView.setText(String.format("%02d:%02d", reminder.getHour(), reminder.getMin()));
        if(reminder.getEnabled()){
            reminderCardView.setBackgroundColor(getColor(R.color.white));
        } else {
            reminderCardView.setBackgroundColor(getColor(R.color.lightGrey));
        }

        reminderTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ChallengeActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        reminderTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        reminder.setHour(hourOfDay);
                        reminder.setMin(minutes);

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                reminderDatabase.reminderDao().update(reminder);
                            }
                        });

                        if(reminder.getEnabled()){
                            setReminder();
                        }
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        reminderToggle.setChecked(reminder.getEnabled());

        reminderToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) return;
                if(isChecked){
                    reminderCardView.setBackgroundColor(getColor(R.color.white));
                    setReminder();
                    reminder.setEnabled(true);
                } else {
                    reminderCardView.setBackgroundColor(getColor(R.color.lightGrey));
                    cancelReminder();
                    reminder.setEnabled(false);
                }
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        reminderDatabase.reminderDao().update(reminder);
                    }
                });
            }
        });

        silentSignInAndGetChallenge();

        joinBtn.setOnClickListener(v -> silentSignInAndJoinChallenge());
        showCommentsBtn.setOnClickListener(v -> silentSignInAndShowComments());
        hideCommentsBtn.setOnClickListener(v -> hideComments());
        addCommentBtn.setOnClickListener(v -> silentSignInAndAddComment());
        statisticsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("CHALLENGE_ID", challengeIdFromIntent);
            intent.putExtra("TITLE", challenge.getTitle());
            startActivity(intent);
        });
    }

    private void setReminder() {
        Intent reminderIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        reminderIntent.putExtra("REMINDER_ID", reminderId);

        reminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        reminderPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminderId.intValue(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar reminderTime = Calendar.getInstance();
        reminderTime.setTimeInMillis(System.currentTimeMillis());
        reminderTime.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        reminderTime.set(Calendar.MINUTE, reminder.getMin());

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reminderTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, reminderPendingIntent);
        Toast.makeText(getApplicationContext(), getString(R.string.reminder_set), Toast.LENGTH_LONG)
                .show();
    }

    private void cancelReminder() {
        Intent reminderIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        reminderIntent.putExtra("REMINDER_ID", reminderId);

        reminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        reminderPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminderId.intValue(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(reminderPendingIntent);
        Toast.makeText(getApplicationContext(), getString(R.string.reminder_canceled), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void silentSignInAndGetChallenge() {
        progressBar.setVisibility(View.VISIBLE);
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getChallengeFromServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getChallengeFromServer(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getChallengeFromServer(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges/" + challengeIdFromIntent,
                null,
                response -> {
                    try {
                        joinBtn.setVisibility(View.VISIBLE);
                        statisticsBtn.setVisibility(View.VISIBLE);
                        showCommentsBtn.setVisibility(View.VISIBLE);
                        noNetworkLabel.setVisibility(View.GONE);
                        refreshBtn.setVisibility(View.GONE);

                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String city = jsonObject.getString("city");
                        RepeatPeriod repeat = RepeatPeriod.valueOf(jsonObject.getString("repeatPeriod"));
                        Category category = Category.valueOf(jsonObject.getString("category"));
                        AccessType access = AccessType.valueOf(jsonObject.getString("accessType"));
                        OffsetDateTime offsetDateTimeStart = OffsetDateTime.parse(jsonObject.getString("startDate"), formatter);
                        LocalDateTime start = offsetDateTimeStart.toLocalDateTime();
                        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.parse(jsonObject.getString("endDate"), formatter);
                        LocalDateTime end = offsetDateTimeEnd.toLocalDateTime();
                        ChallengeState state = ChallengeState.valueOf(jsonObject.getString("challengeState"));
                        ConfirmationType confirmation = ConfirmationType.valueOf(jsonObject.getString("confirmationType"));
                        Boolean isUserParticipant = jsonObject.getBoolean("userParticipant");
                        Integer goal = jsonObject.getInt("goal");

                        challenge.setId(Long.valueOf(id));
                        challenge.setTitle(title);
                        challenge.setDescription(description);
                        challenge.setCity(city);
                        challenge.setRepeatPeriod(repeat);
                        challenge.setCategory(category);
                        challenge.setConfirmationType(confirmation);
                        challenge.setAccessType(access);
                        challenge.setState(state);
                        challenge.setGoal(goal);
                        challenge.setStartDate(start);
                        challenge.setEndDate(end);
                        challenge.setUserParticipant(isUserParticipant);

                        Log.d(this.getClass().getSimpleName() + " jsonObject", challenge.toString());

                        if(reminder.getTitle().isEmpty()){
                            reminder.setTitle(challenge.getTitle());
                            reminder.setEndDate(challenge.getEndDate().format(dateFormatter));
                        }
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                reminderDatabase.reminderDao().update(reminder);
                            }
                        });

                        updateUIWithChallenge();

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
                        todayCard.setVisibility(View.GONE);
                        reminderCardView.setVisibility(View.GONE);
                        joinBtn.setVisibility(View.GONE);
                        statisticsBtn.setVisibility(View.GONE);
                        showCommentsBtn.setVisibility(View.GONE);
                        noNetworkLabel.setVisibility(View.VISIBLE);
                        refreshBtn.setVisibility(View.VISIBLE);
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

    private void silentSignInAndShowComments() {
        progressBar.setVisibility(View.VISIBLE);
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            showComments(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> showComments(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void silentSignInAndAddComment() {
        addCommentProgressBar.setVisibility(View.VISIBLE);
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            addCommentToServer(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> addCommentToServer(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void addCommentToServer(String token) {
        String comment = commentEdit.getText().toString();

        if (Strings.isEmptyOrWhitespace(comment)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_fields_error), Toast.LENGTH_LONG)
                    .show();
            addCommentProgressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", comment);

        JSONObject jsonRequestBody = new JSONObject(requestBody);
        Log.d(this.getClass().getSimpleName() + " request body", "\n\n" + jsonRequestBody.toString() + "\n\n");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/comments",
                jsonRequestBody,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        Toast.makeText(getApplicationContext(), R.string.added_comment, Toast.LENGTH_LONG)
                                .show();

                        showComments(token);
                        commentEdit.setText("");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
                        addCommentProgressBar.setVisibility(View.GONE);
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
                    addCommentProgressBar.setVisibility(View.GONE);
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
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void hideComments() {
        showCommentsBtn.setVisibility(View.VISIBLE);
        hideCommentsBtn.setVisibility(View.GONE);
        commentsLinearLayout.setVisibility(View.GONE);
    }

    private void showComments(String token) {
        progressBar.setVisibility(View.VISIBLE);
        noCommentsLabel.setVisibility(View.GONE);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(scrollView.FOCUS_DOWN);
            }
        });
        comments.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/comments",
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            noCommentsLabel.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < response.length(); i++) {
//                            try {
                            JSONObject jsonObject = (JSONObject) response.get(i);

                            Long id = jsonObject.getLong("id");
                            Long creatorId = jsonObject.getLong("creatorId");
                            String creatorUsername = jsonObject.getString("creatorUsername");
                            String text = jsonObject.getString("text");
                            OffsetDateTime createdAt = OffsetDateTime.parse(jsonObject.getString("createdAt"), formatter);

                            Comment c = new Comment();
                            c.setId(id);
                            c.setCreatorId(creatorId);
                            c.setCreatorUsername(creatorUsername);
                            c.setText(text);
                            c.setCreatedAt(createdAt);

                            comments.add(c);
                            commentsAdapter.notifyDataSetChanged();
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
                        commentsLinearLayout.setVisibility(View.VISIBLE);
                        showCommentsBtn.setVisibility(View.GONE);
                        hideCommentsBtn.setVisibility(View.VISIBLE);
                        addCommentProgressBar.setVisibility(View.GONE);

                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.fullScroll(scrollView.FOCUS_DOWN);
                            }
                        });
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

    private void silentSignInAndSaveChange() {
        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            saveChange(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> saveChange(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void saveChange(String tokenId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("done", today.getDone());
        requestBody.put("currentStatus", today.getCurrentStatus());

        JSONObject jsonRequestBody = new JSONObject(requestBody);
        Log.d(this.getClass().getSimpleName() + " request body", "\n\n" + jsonRequestBody.toString() + "\n\n");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/days/" + today.getId(),
                jsonRequestBody,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer points = jsonObject.getInt("points");
                        String message = "";

                        if (points == -1) {
                            Toast.makeText(getApplicationContext(), points + " " + getString(R.string.point), Toast.LENGTH_LONG)
                                    .show();
                        } else if (points < -1) {
                            Toast.makeText(getApplicationContext(), points + " " + getString(R.string.points), Toast.LENGTH_LONG)
                                    .show();
                        } else if (points == 1) {
                            Toast.makeText(getApplicationContext(), "+" + points + " " + getString(R.string.point), Toast.LENGTH_LONG)
                                    .show();
                        } else if (points > 1) {
                            Toast.makeText(getApplicationContext(), "+" + points + " " + getString(R.string.points), Toast.LENGTH_LONG)
                                    .show();
                        }

                        Log.d(this.getClass().getSimpleName(), "Updated day");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
//                        progressBar.setVisibility(View.GONE);
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
//                    progressBar.setVisibility(View.GONE);
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + tokenId);
                return headers;
            }
        };
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void updateStateDependentUI() {
        ChallengeState state = challenge.getState();

        if (ChallengeState.NOT_STARTED_YET == state) {
            daysLayout.setVisibility(View.GONE);
            notStartedYetLabel.setVisibility(View.VISIBLE);
            statisticsBtn.setVisibility(View.GONE);
        } else if (ChallengeState.STARTED == state) {
            daysLayout.setVisibility(View.VISIBLE);
            notStartedYetLabel.setVisibility(View.GONE);
            silentSignInAndGetLastDays();
            todayCard.setVisibility(View.VISIBLE);
            reminderLabel.setVisibility(View.VISIBLE);
            reminderCardView.setVisibility(View.VISIBLE);
        } else {
            daysLayout.setVisibility(View.VISIBLE);
            notStartedYetLabel.setVisibility(View.GONE);
            todayCard.setVisibility(View.GONE);
            silentSignInAndGetLastDays();
        }
    }

    private void updateUIWithChallenge() {
        titleText.setText(challenge.getTitle());
        descText.setText(challenge.getDescription());
        cityText.setText(challenge.getCity());

        categoryText.setText(challenge.getCategory().getLabel(getApplicationContext()));
        repeatText.setText(challenge.getRepeatPeriod().getLabel(getApplicationContext()));
        confirmationText.setText(challenge.getConfirmationType().getLabel(getApplicationContext()));
        accessText.setText(challenge.getAccessType().getLabel(getApplicationContext()));

        goalText.setText(challenge.getGoal().toString());

        startText.setText(challenge.getStartDate().format(basicDateFormatter));
        endText.setText(challenge.getEndDate().format(basicDateFormatter));

        if (challenge.getConfirmationType() == ConfirmationType.CHECK_TASK) {
            todayToggle.setVisibility(View.VISIBLE);
            counterLinearLayout.setVisibility(View.GONE);
        } else {
            todayToggle.setVisibility(View.GONE);
            counterLinearLayout.setVisibility(View.VISIBLE);
        }

        if (challenge.getAccessType() == AccessType.PUBLIC && challenge.isUserParticipant() == true) {
            showCommentsBtn.setVisibility(View.VISIBLE);
        } else {
            showCommentsBtn.setVisibility(View.GONE);
        }

        if (!challenge.isUserParticipant()) {
            daysLabel.setVisibility(View.GONE);
            daysLayout.setVisibility(View.GONE);
            statisticsBtn.setVisibility(View.GONE);

            joinBtn.setVisibility(View.VISIBLE);
        } else {
            daysLabel.setVisibility(View.VISIBLE);
            statisticsBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);

            updateStateDependentUI();
        }

        if (challenge.getConfirmationType() == ConfirmationType.CHECK_TASK) {
            goalLabel.setVisibility(View.GONE);
            goalText.setVisibility(View.GONE);
        } else {
            goalLabel.setVisibility(View.VISIBLE);
            goalText.setVisibility(View.VISIBLE);
        }
    }

    private void silentSignInAndGetLastDays() {

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getLastDays(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getLastDays(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getLastDays(String idToken) {
        ChallengeState state = challenge.getState();
        pastDays.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId()
                        + "/days?challengeState=" + state + "&daysNum=" + DEFAULT_DAYS_NUM,
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
                                    .show();
//                            progressBar.setVisibility(View.GONE);
                        }

                        Log.d("", response.toString(2));

                        JSONObject jsonObject = (JSONObject) response.get(0);

                        Long id = jsonObject.getLong("id");

                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(jsonObject.getString("date"), formatter);
                        LocalDateTime date = offsetDateTime.toLocalDateTime();

                        Boolean done = jsonObject.getBoolean("done");
                        Integer currentStatus = jsonObject.getInt("currentStatus");

                        if (state == ChallengeState.STARTED) {
                            today = new Day(id, date, done, currentStatus);
                            today.setConfirmationType(challenge.getConfirmationType());

                            todayDate.setText(date.format(simpleDateFormatter));
                            todayToggle.setChecked(done);
                            counterText.setText(String.valueOf(currentStatus));
                            todayCard.setVisibility(View.VISIBLE);
                        } else {
                            Day day = new Day(id, date, done, currentStatus);
                            day.setConfirmationType(challenge.getConfirmationType());

                            pastDays.add(day);
                            daysAdapter.notifyDataSetChanged();
                        }


                        for (int i = 1; i < response.length(); i++) {
                            jsonObject = (JSONObject) response.get(i);

                            id = jsonObject.getLong("id");
                            offsetDateTime = OffsetDateTime.parse(jsonObject.getString("date"), formatter);
                            date = offsetDateTime.toLocalDateTime();
                            done = jsonObject.getBoolean("done");
                            currentStatus = jsonObject.getInt("currentStatus");

                            Day day = new Day(id, date, done, currentStatus);
                            day.setConfirmationType(challenge.getConfirmationType());

                            pastDays.add(day);
                            daysAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
//                        progressBar.setVisibility(View.GONE);
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
//                    progressBar.setVisibility(View.GONE);
                    todayCard.setVisibility(View.GONE);
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
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    private void silentSignInAndJoinChallenge() {
//        progressBar.setVisibility(View.VISIBLE);

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            joinChallenge(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> joinChallenge(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void joinChallenge(String token) {
//        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/participants",
                null,
                response -> {
                    try {
                        JSONObject jsonObject = (JSONObject) response;

                        Integer id = jsonObject.getInt("id");

                        daysLabel.setVisibility(View.VISIBLE);
                        statisticsBtn.setVisibility(View.VISIBLE);
                        showCommentsBtn.setVisibility(View.VISIBLE);

                        joinBtn.setVisibility(View.GONE);

                        challenge.setUserParticipant(true);

                        updateStateDependentUI();

                        Toast.makeText(getApplicationContext(), getString(R.string.joined_challenge), Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.unknown_error_occurred), Toast.LENGTH_LONG)
                                .show();
                        Log.d(this.getClass().getName(), e.getMessage());
                    } finally {
//                        progressBar.setVisibility(View.GONE);
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
//                    progressBar.setVisibility(View.GONE);
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
        ServerRequestUtil.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }
}
