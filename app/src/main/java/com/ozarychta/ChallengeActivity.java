package com.ozarychta;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ChallengeActivity extends BaseActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String SIMPLE_DATE_FORMAT = "dd-MM-yyyy";
    private SimpleDateFormat simpleDateFormat;

    private ConnectivityManager connectivityManager;

    private Challenge challenge;

    private TextView goalText;
    private TextView moreOrLessText;

    private TextView titleText;
    private TextView descText;
    private TextView cityText;

    private TextView categoryText;
    private TextView repeatText;
    private TextView confirmationText;
    private TextView accessText;

    private TextView startText;
    private TextView endText;

    private Button statisticsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        getSupportActionBar().setTitle(R.string.challenge);

        challenge = (Challenge) getIntent().getSerializableExtra("CHALLENGE");
        Log.d("challenge", challenge.getId().toString());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        titleText = findViewById(R.id.titleTextView);
        descText = findViewById(R.id.descriptionTextView);
        cityText = findViewById(R.id.cityTextView);

        categoryText = findViewById(R.id.categoryTextView);
        repeatText = findViewById(R.id.repeatTextView);
        confirmationText = findViewById(R.id.confirmationTextView);
        accessText = findViewById(R.id.accessTextView);

        goalText = findViewById(R.id.goalTextView);
        moreOrLessText = findViewById(R.id.moreOrLessTextView);

        startText = findViewById(R.id.startTextView);
        endText = findViewById(R.id.endTextView);

        statisticsBtn = findViewById(R.id.statisticsBtn);


        titleText.setText(challenge.getTitle());
        descText.setText(challenge.getDescription());
        cityText.setText(challenge.getCity());

        categoryText.setText(challenge.getCategory().toString());
        repeatText.setText(challenge.getRepeatPeriod().toString());
        confirmationText.setText(challenge.getConfirmationType().toString());
        accessText.setText(challenge.getAccessType().toString());

        goalText.setText(challenge.getGoal().toString());
        moreOrLessText.setText(challenge.getMoreBetter().toString());

        startText.setText(simpleDateFormat.format(challenge.getStartDate()));
        endText.setText(simpleDateFormat.format(challenge.getEndDate()));

    }

}
