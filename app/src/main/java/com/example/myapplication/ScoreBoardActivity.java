package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.UserScore;
import com.example.UserScoresAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreBoardActivity extends AppCompatActivity {

    private TextView tvHighScore;
    private ListView scoresListView;
    private List<UserScore> userScoresList;
    private DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        tvHighScore = findViewById(R.id.tvHighScore);
        scoresListView = findViewById(R.id.scoresListView);
        userScoresList = new ArrayList<>();

        // Read user scores from Firebase
        scoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    long userScore = userSnapshot.getValue(Long.class);

                    // Retrieve the user's email from users node
                    usersRef.child(userId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userEmail = dataSnapshot.getValue(String.class);
                            UserScore userScoreObject = new UserScore(userId, userEmail, userScore);
                            userScoresList.add(userScoreObject);
                            // Sort the user scores by score in descending order (highest score first)
                            userScoresList.sort((userScore1, userScore2) -> Long.compare(userScore2.getUserScore(), userScore1.getUserScore()));

                            // Refresh the ListView (notify the adapter)
                            UserScoresAdapter adapter = new UserScoresAdapter(ScoreBoardActivity.this, userScoresList);
                            scoresListView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
