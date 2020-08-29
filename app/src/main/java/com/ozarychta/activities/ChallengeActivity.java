package com.ozarychta.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import com.ozarychta.AlarmReceiver;
import com.ozarychta.R;
import com.ozarychta.Reminder;
import com.ozarychta.ReminderDatabase;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.Comment;
import com.ozarychta.model.CommentAdapter;
import com.ozarychta.model.Day;
import com.ozarychta.model.DayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ChallengeActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private static final String BASIC_DATE_FORMAT = "dd.MM.yyyy";
    private static final Integer DEFAULT_DAYS_NUM = 7;

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat basicDateFormat;

    private ConnectivityManager connectivityManager;

    private Long challengeIdFromIntent;
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

    private MaterialCardView reminderCardView;
    private ToggleButton reminderToggle;
    private TextView reminderTimeTextView;


    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private TextView noNetworkLabel;
    private Button refreshBtn;

    private ReminderDatabase reminderDatabase;
    private Reminder reminder;
    private Long reminderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        getSupportActionBar().setTitle(R.string.challenge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        challengeIdFromIntent = getIntent().getLongExtra("CHALLENGE_ID", -1);
        challenge = new Challenge();
//        challenge = (Challenge) getIntent().getSerializableExtra("CHALLENGE");
        Log.d(this.getClass().getSimpleName() + " challenge id ", challengeIdFromIntent.toString());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        basicDateFormat = new SimpleDateFormat(BASIC_DATE_FORMAT);
        basicDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        titleText = findViewById(R.id.titleTextView);
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

        reminderCardView = findViewById(R.id.reminderCardView);
        reminderToggle = findViewById(R.id.reminderToggleButton);
        reminderTimeTextView = findViewById(R.id.reminderTimeTextView);

        reminder = reminderDatabase.reminderDao().findByChallengeId(challengeIdFromIntent);

        if(reminder == null){
            reminder = new Reminder(challengeIdFromIntent, false, 12, 0);
            reminderId = reminderDatabase.reminderDao().insert(reminder);
        } else {
            reminderId = reminder.getId();
        }

        reminderTimeTextView.setText(String.format("%02d:%02d", reminder.getHour(), reminder.getMin()));

        reminderTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ChallengeActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        reminderTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        reminder.setHour(hourOfDay);
                        reminder.setMin(minutes);
                        reminderDatabase.reminderDao().update(reminder);

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
                    reminder.setEnabled(true);
                    reminderDatabase.reminderDao().update(reminder);
                    setReminder();
                } else {
                    reminderCardView.setBackgroundColor(getColor(R.color.lightGrey));
                    reminder.setEnabled(false);
                    reminderDatabase.reminderDao().update(reminder);
                    cancelReminder();
                }
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
            startActivity(intent);
        });

//        List<Reminder> list = reminderDatabase.reminderDao().getAll();
//        for(Reminder r : list){
//            Log.d(this.getClass().getSimpleName() + " reminders from database: ", r.toString());
//        }
    }

    private void setReminder() {
        Intent intent = new Intent(ChallengeActivity.this, AlarmReceiver.class);
        intent.putExtra("CHALLENGE_ID", challengeIdFromIntent);
        intent.putExtra("TITLE", challenge.getTitle());
        intent.putExtra("ALARM_ID", reminderId);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        alarmIntent = PendingIntent.getBroadcast(ChallengeActivity.this, reminderId.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        calendar.set(Calendar.MINUTE, reminder.getMin());

        alarmManager = (AlarmManager) ChallengeActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        Toast.makeText(getApplicationContext(), getString(R.string.reminder_set), Toast.LENGTH_LONG)
                .show();
    }

    private void cancelReminder() {
        Intent intent = new Intent(ChallengeActivity.this, AlarmReceiver.class);
        intent.putExtra("CHALLENGE_ID", challengeIdFromIntent);
        intent.putExtra("TITLE", challenge.getTitle());
        intent.putExtra("ALARM_ID", reminderId);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        alarmIntent = PendingIntent.getBroadcast(ChallengeActivity.this, reminderId.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) ChallengeActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
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
                        todayCard.setVisibility(View.VISIBLE);
                        reminderCardView.setVisibility(View.VISIBLE);
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
                        Date start = dateFormat.parse(jsonObject.getString("startDate"));
                        Date end = dateFormat.parse(jsonObject.getString("endDate"));
                        ChallengeState state = ChallengeState.valueOf(jsonObject.getString("challengeState"));
                        ConfirmationType confirmation = ConfirmationType.valueOf(jsonObject.getString("confirmationType"));
                        Boolean isUserParticipant = jsonObject.getBoolean("userParticipant");

                        Integer goal = 0;
                        Boolean isMoreBetter = true;
                        if (confirmation == ConfirmationType.COUNTER_TASK) {
                            isMoreBetter = jsonObject.getBoolean("moreBetter");
                            goal = jsonObject.getInt("goal");
                        }

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
                        challenge.setMoreBetter(isMoreBetter);
                        challenge.setStartDate(start);
                        challenge.setEndDate(end);
                        challenge.setUserParticipant(isUserParticipant);

                        Log.d(this.getClass().getSimpleName() + " jsonObject", challenge.toString());

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
                            Date createdAt = dateFormat.parse(jsonObject.getString("createdAt"));

                            Comment c = new Comment();
                            c.setId(id);
                            c.setCreatorId(creatorId);
                            c.setCreatorUsername(creatorUsername);
                            c.setText(text);
                            c.setCreatedAt(createdAt);

                            comments.add(c);
                            commentsAdapter.notifyDataSetChanged();

//                                Log.d(this.getClass().getSimpleName() + " comment", comments.get(i).toString());

//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
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
        } else if (ChallengeState.STARTED == state) {
            daysLayout.setVisibility(View.VISIBLE);
            notStartedYetLabel.setVisibility(View.GONE);
            silentSignInAndGetLastDays();
            todayCard.setVisibility(View.VISIBLE);
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

        startText.setText(basicDateFormat.format(challenge.getStartDate()));
        endText.setText(basicDateFormat.format(challenge.getEndDate()));

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

        if (challenge.isUserParticipant() == false) {
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
                        Date date = dateFormat.parse(jsonObject.getString("date"));
                        Boolean done = jsonObject.getBoolean("done");
                        Integer currentStatus = jsonObject.getInt("currentStatus");

                        if (state == ChallengeState.STARTED) {
                            today = new Day(id, date, done, currentStatus);
                            today.setConfirmationType(challenge.getConfirmationType());

                            todayDate.setText(simpleDateFormat.format(date));
                            todayToggle.setChecked(done);
                            counterText.setText(String.valueOf(currentStatus));
                        } else {
                            Day day = new Day(id, date, done, currentStatus);
                            day.setConfirmationType(challenge.getConfirmationType());

                            pastDays.add(day);
                            daysAdapter.notifyDataSetChanged();
                        }


                        for (int i = 1; i < response.length(); i++) {
//                            try {
                            jsonObject = (JSONObject) response.get(i);

                            id = jsonObject.getLong("id");
                            date = dateFormat.parse(jsonObject.getString("date"));
                            done = jsonObject.getBoolean("done");
                            currentStatus = jsonObject.getInt("currentStatus");

                            Day day = new Day(id, date, done, currentStatus);
                            day.setConfirmationType(challenge.getConfirmationType());

                            pastDays.add(day);
                            daysAdapter.notifyDataSetChanged();

//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
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
