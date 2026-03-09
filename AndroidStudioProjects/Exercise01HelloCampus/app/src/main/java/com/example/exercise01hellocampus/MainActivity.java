package com.example.exercise01hellocampus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView departmentText;
    Button chairButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set student ID in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student ID: 20101570");
        }

        departmentText = findViewById(R.id.tvDepartment);
        chairButton = findViewById(R.id.btnChair);

        chairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                departmentText.setText("Angie M. Ceniza-Canillo, PhD");
            }
        });
    }
}