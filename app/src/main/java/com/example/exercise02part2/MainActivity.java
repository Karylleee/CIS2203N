package com.example.exercise02part2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.exercise02part2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSubmit.setOnClickListener(v -> validateLogin());
    }

    private void validateLogin() {

        String studentId = binding.etStudentId.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Check if ID is valid
        if (studentId.length() < 2) {
            binding.tvResult.setText("Invalid Student ID");
            return;
        }

        // Get last 2 digits
        String lastTwoDigits = studentId.substring(studentId.length() - 2);

        // Expected password format
        String expectedPassword = "blue" + lastTwoDigits;

        // Compare passwords
        if (password.equals(expectedPassword)) {
            binding.tvResult.setText("Access Granted");
        } else {
            binding.tvResult.setText("Access Denied");
        }
    }
}