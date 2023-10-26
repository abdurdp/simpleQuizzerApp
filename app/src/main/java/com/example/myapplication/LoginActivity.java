package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        setListeners();

    }

    private void setListeners() {
        Button loginButton = findViewById(R.id.loginButton);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if(!email.isEmpty()&&!password.isEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Login successful, navigate to the main activity
                                startActivity(new Intent(this, MainActivity.class));
                            } else {
                                // Login failed, display an error message
                                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        TextView signupTextView = findViewById(R.id.signupTextView);
        signupTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });


    }
}