package com.ozarychta.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;

import com.ozarychta.R;
import com.ozarychta.model.User;

public class ProfileActivity extends BaseActivity{

    private ConnectivityManager connectivityManager;

    private User user;

    private TextView usernameText;
    private TextView aboutMeText;
    private TextView mainGoalText;
    private TextView rankingPointsText;
    private TextView highestStrikeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.profile);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        usernameText = findViewById(R.id.usernameTextView);
        aboutMeText = findViewById(R.id.aboutMeTextView);
        mainGoalText = findViewById(R.id.mainGoalTextView);
        rankingPointsText = findViewById(R.id.pointsTextView);
        highestStrikeText = findViewById(R.id.strikeTextView);

        usernameText.setText("test");
        aboutMeText.setText("test");
        mainGoalText.setText("test");
        rankingPointsText.setText("test");
        highestStrikeText.setText("test");
    }
}
