package com.ozarychta.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ozarychta.R;

public class StatisticsActivity extends BaseActivity {

    private Long challengeIdFromIntent;

    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setTitle(R.string.statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        challengeIdFromIntent = getIntent().getLongExtra("CHALLENGE_ID", -1);
        Log.d(this.getClass().getSimpleName() + " challenge id ", challengeIdFromIntent.toString());

        titleText = findViewById(R.id.titleTextView);
        titleText.setText(String.valueOf(challengeIdFromIntent));
    }
}
