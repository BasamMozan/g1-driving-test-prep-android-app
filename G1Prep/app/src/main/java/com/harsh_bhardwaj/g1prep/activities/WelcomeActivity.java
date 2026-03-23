package com.harsh_bhardwaj.g1prep.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.harsh_bhardwaj.g1prep.R;

public class WelcomeActivity extends AppCompatActivity {

    Button practiceQuestionsButton, findCentresButton, inviteFriendsButton;
    Context context;
    Vibrator vibrator;
    ImageView backArrowImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        practiceQuestionsButton = findViewById(R.id.practiceQuestionsButton);
        findCentresButton = findViewById(R.id.findCentresButton);
        inviteFriendsButton = findViewById(R.id.inviteFriendsButton);
        backArrowImageView = findViewById(R.id.backArrowIcon);


        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        practiceQuestionsButton.setOnClickListener(view -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(context, AllQuestionsActivity.class);
            startActivity(intent);
        });

        findCentresButton.setOnClickListener(view -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(context, MapsActivity.class);
            startActivity(intent);
        });

        inviteFriendsButton.setOnClickListener(view -> {

            vibrator.vibrate(50);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the G1 Prep application from the Play Store and let's practice for the G1 written test together!");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);


        });
        backArrowImageView.setOnClickListener(view -> {
            vibrator.vibrate(50);
            onBackPressed();
        });
    }
}