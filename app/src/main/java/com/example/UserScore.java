package com.example;

public class UserScore {
    private String userId;
    private String userEmail;
    private long userScore;

    public UserScore() {
        // Required empty public constructor
    }

    public UserScore(String userId, String userEmail, long userScore) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userScore = userScore;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getUserScore() {
        return userScore;
    }

    public void setUserScore(long userScore) {
        this.userScore = userScore;
    }
}

