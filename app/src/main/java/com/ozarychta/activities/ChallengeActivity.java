package com.ozarychta.activities;

import android.content.Context;
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
import com.ozarychta.R;
import com.ozarychta.ServerRequestUtil;
import com.ozarychta.SignInClient;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.Comment;
import com.ozarychta.model.CommentAdapter;
import com.ozarychta.model.Day;
import com.ozarychta.model.DayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ChallengeActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private static final String BASIC_DATE_FORMAT = "dd.MM.yyyy";
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat basicDateFormat;

    private ConnectivityManager connectivityManager;

    private Challenge challenge;
    private Day today;

    private TextView goalText;
    private TextView moreOrLessText;

    private TextView goalLabel;
    private TextView moreOrLessLabel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        getSupportActionBar().setTitle(R.string.challenge);

        challenge = (Challenge) getIntent().getSerializableExtra("CHALLENGE");
        Log.d(this.getClass().getSimpleName() + " challenge", challenge.getId().toString());

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
        moreOrLessText = findViewById(R.id.moreOrLessTextView);
        moreOrLessLabel = findViewById(R.id.moreOrLessLabel);

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

        titleText.setText(challenge.getTitle());
        descText.setText(challenge.getDescription());
        cityText.setText(challenge.getCity());

        categoryText.setText(challenge.getCategory().getLabel(getApplicationContext()));
        repeatText.setText(challenge.getRepeatPeriod().getLabel(getApplicationContext()));
        confirmationText.setText(challenge.getConfirmationType().getLabel(getApplicationContext()));
        accessText.setText(challenge.getAccessType().getLabel(getApplicationContext()));

        goalText.setText(challenge.getGoal().toString());
        moreOrLessText.setText(challenge.getMoreBetter().toString());

        startText.setText(basicDateFormat.format(challenge.getStartDate()));
        endText.setText(basicDateFormat.format(challenge.getEndDate()));

        notStartedYetLabel = findViewById(R.id.notStartedYetLabel);
        notStartedYetLabel.setVisibility(View.GONE);

        todayDate = todayCard.findViewById(R.id.dateTextView);
        todayToggle = todayCard.findViewById(R.id.toggleButton);
        todayToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                today.setDone(isChecked);
                silentSignInAndSaveChange();
            }
        });

        counterLinearLayout = todayCard.findViewById(R.id.counterLinearLayout);
        counterText = todayCard.findViewById(R.id.counterTextView);

        minusBtn = todayCard.findViewById(R.id.minusButton);
        minusBtn.setOnClickListener(v -> {
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

        if (challenge.getConfirmationType() == ConfirmationType.CHECK_TASK) {
            todayToggle.setVisibility(View.VISIBLE);
            counterLinearLayout.setVisibility(View.GONE);
        } else {
            todayToggle.setVisibility(View.GONE);
            counterLinearLayout.setVisibility(View.VISIBLE);
        }

        commentEdit = findViewById(R.id.commentEdit);
        hideCommentsBtn.setVisibility(View.GONE);
        commentsLinearLayout.setVisibility(View.GONE);
        noCommentsLabel.setVisibility(View.GONE);

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
            moreOrLessLabel.setVisibility(View.GONE);
            goalText.setVisibility(View.GONE);
            moreOrLessText.setVisibility(View.GONE);
        } else {
            goalLabel.setVisibility(View.VISIBLE);
            moreOrLessLabel.setVisibility(View.VISIBLE);
            goalText.setVisibility(View.VISIBLE);
            moreOrLessText.setVisibility(View.VISIBLE);
        }

        joinBtn.setOnClickListener(v -> silentSignInAndJoinChallenge());
        showCommentsBtn.setOnClickListener(v -> silentSignInAndShowComments());
        hideCommentsBtn.setOnClickListener(v -> hideComments());
        addCommentBtn.setOnClickListener(v -> silentSignInAndAddComment());
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

                        Integer id = jsonObject.getInt("id");

//                        Toast.makeText(getApplicationContext(), getString(R.string.updated_state), Toast.LENGTH_LONG)
//                                .show();
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
            silentSignInAndGetLastFourDays();
            todayCard.setVisibility(View.VISIBLE);
        } else {
            daysLayout.setVisibility(View.VISIBLE);
            notStartedYetLabel.setVisibility(View.GONE);
            todayCard.setVisibility(View.GONE);
            silentSignInAndGetLastFourDays();
        }
    }

    private void silentSignInAndGetLastFourDays() {

        Task<GoogleSignInAccount> task = SignInClient.getInstance(this).getGoogleSignInClient().silentSignIn();
        if (task.isSuccessful()) {
            // There's immediate result available.
            getLastFourDays(task.getResult().getIdToken());
        } else {
            task.addOnCompleteListener(
                    this,
                    task1 -> getLastFourDays(SignInClient.getTokenIdFromResult(task1)));
        }
    }

    private void getLastFourDays(String idToken) {
        ChallengeState state = challenge.getState();
        pastDays.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://be-better-server.herokuapp.com/challenges/" + challenge.getId() + "/days?challengeState=" + state,
                null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_results), Toast.LENGTH_LONG)
                                    .show();
//                            progressBar.setVisibility(View.GONE);
                        }

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
