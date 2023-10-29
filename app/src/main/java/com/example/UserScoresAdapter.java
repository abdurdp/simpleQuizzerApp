package com.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

public class UserScoresAdapter extends BaseAdapter {

    private Context context;
    private List<UserScore> userScoresList;

    public UserScoresAdapter(Context context, List<UserScore> userScoresList) {
        this.context = context;
        this.userScoresList = userScoresList;
    }

    @Override
    public int getCount() {
        return userScoresList.size();
    }

    @Override
    public Object getItem(int position) {
        return userScoresList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_score, parent, false);
        }

        UserScore currentUserScore = userScoresList.get(position);

        TextView tvUserEmail = convertView.findViewById(R.id.tvUserEmail);
        TextView tvUserScore = convertView.findViewById(R.id.tvUserScore);

        tvUserEmail.setText(currentUserScore.getUserEmail());
        tvUserScore.setText("Score: " + currentUserScore.getUserScore());

        return convertView;
    }
}
