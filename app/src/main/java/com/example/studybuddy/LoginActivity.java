package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }
}