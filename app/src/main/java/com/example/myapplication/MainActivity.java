package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.color.utilities.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView totalQuestionsTextView;
    TextView questionTextView;
    TextView tvHighScore;
    Button ansA,ansB,ansC,ansD;
    Button submitBtn;
    ImageView logoutBtn;
    ImageView profile_btn;

    int score=0;
    int totalQuestion = QuestionAnswer.question.length;
    int currentQuestionsIndex = 0;
    String selectedAnswer = "";

    // Create an array of unique numbers from 0 to numQuestions - 1
    ArrayList<Integer> numberArray = new ArrayList<>();
    private int randomIndex;
    DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int userScore = 100; // Replace with the actual score


        scoresRef.child(userId).setValue(userScore);

        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        profile_btn = findViewById(R.id.profile_btn);
        tvHighScore = findViewById(R.id.tvHighScore);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);

        profile_btn.setOnClickListener(v -> {
            // After signing out, you can navigate to a login or home screen
            Intent intent = new Intent(getApplication(), ProfileActivity.class);
            startActivity(intent);

        });
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            // Sign the user out
            mAuth.signOut();

            // After signing out, you can navigate to a login or home screen
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        totalQuestionsTextView.setText("Total Questions : " +totalQuestion);

        randomizeQuestion();
        loadNewQuestion();
        final String[] highScorerId = new String[1];
        final long[] highScore = {0};
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    highScorerId[0] = userSnapshot.getKey();
                    long userScore = userSnapshot.getValue(Long.class);
                    if (userScore > highScore[0]) {
                        highScore[0] = userScore;
                    }
                    if (!highScorerId[0].isEmpty()) {
                        usersRef.child(highScorerId[0].toString()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String highScorerEmail = dataSnapshot.getValue(String.class);
                                if (highScorerEmail != null) {
                                    tvHighScore.setVisibility(View.VISIBLE);
                                    tvHighScore.setText("High Scorer:"+ highScorerEmail+ " "+ highScore[0]);
                                } else {
                                    // Email not found or an error occurred
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle errors here
                            }
                        });
                    }
                    // Do something with userScore, userId, and highScore
                }
                // The variable highScore now contains the highest score, and highScorer contains the user's ID with the high score.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });


    }

    private void randomizeQuestion() {
        for (int i = 0; i < totalQuestion; i++) {
            numberArray.add(i);
        }

        // Shuffle the number array randomly
        shuffleArray(numberArray);
    }

    // Shuffle an ArrayList randomly
    private static void shuffleArray(ArrayList<Integer> array) {
        int index, temp;
        Random random = new Random();
        for (int i = array.size() - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array.get(index);
            array.set(index, array.get(i));
            array.set(i, temp);
        }
    }
    @Override
    public void onClick(View view) {

        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) view;
        if(clickedButton.getId()==R.id.submit_btn){

            if(selectedAnswer.equals(QuestionAnswer.correctAnswer[randomIndex])){
                score++;
            }
            loadNewQuestion();

        }else {
            //choices button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.RED);

        }



    }

    void loadNewQuestion(){

        if(numberArray.isEmpty()){
            finishQuiz();
            return;
        }

        randomIndex = numberArray.remove(0);
        questionTextView.setText(QuestionAnswer.question[randomIndex]);
        ansA.setText(QuestionAnswer.choice[randomIndex][0]);
        ansB.setText(QuestionAnswer.choice[randomIndex][1]);
        ansC.setText(QuestionAnswer.choice[randomIndex][2]);
        ansD.setText(QuestionAnswer.choice[randomIndex][3]);

    }

    void finishQuiz(){
        String passtatus = "";
        if(score > totalQuestion*0.60){
            passtatus = "Passed";
        }else {
            passtatus = "Failed";
        }

        new AlertDialog.Builder(this)
                .setTitle(passtatus)
                .setMessage("Score is"+ score+ "out of"+ totalQuestion)
                .setPositiveButton("Restart",(dialogInterface,i) -> restartQuiz())
                .setCancelable(false)
                .show();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        scoresRef.child(userId).setValue(score);



    }


    void restartQuiz(){
        score = 0;
        currentQuestionsIndex = 0;
        randomizeQuestion();
        loadNewQuestion();

    }


}