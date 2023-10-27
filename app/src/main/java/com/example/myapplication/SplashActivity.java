package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Delay the transition to the main activity
        new Handler().postDelayed(() -> {
            // Start the main activity
            if(currentUser != null){
                startActivity(new Intent(this, MainActivity.class));
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
        }, 3000);
        final ImageView logoImageView = findViewById(R.id.logoImageView);

        Animation zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoomInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Perform actions when the zoom-in animation ends
                logoImageView.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.zoom_out));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });

        logoImageView.startAnimation(zoomInAnimation);

    }
}