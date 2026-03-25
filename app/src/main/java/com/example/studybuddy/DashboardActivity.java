package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button subjectsBtn = findViewById(R.id.btnSubjects);
        Button profileBtn = findViewById(R.id.btnProfile);

        subjectsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SubjectsActivity.class);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}