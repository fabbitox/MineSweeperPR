package com.first.minesweeperpr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Statistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView[] best_tvs = {findViewById(R.id.easy_best), findViewById(R.id.normal_best), findViewById(R.id.hard_best)};
        TextView[] total_tvs = {findViewById(R.id.easy_total), findViewById(R.id.normal_total), findViewById(R.id.hard_total)};
        TextView[] win_tvs = {findViewById(R.id.easy_win), findViewById(R.id.normal_win), findViewById(R.id.hard_win)};
        TextView[] rate_tvs = {findViewById(R.id.easy_rate), findViewById(R.id.normal_rate), findViewById(R.id.hard_rate)};

        SharedPreferences shp = getSharedPreferences("statistics", MODE_PRIVATE);
        String[] shp_keys = {
                "easy_best", "normal_best", "hard_best",
                "easy_game", "normal_game", "hard_game", "easy_win", "normal_win", "hard_win"
        };
        int[] best_recs = new int[3];
        int[] total_recs = new int[3];
        int[] win_recs = new int[3];
        for (int i = 0; i < 3; i++) {
            best_recs[i] = shp.getInt(shp_keys[i], -1);
            total_recs[i] = shp.getInt(shp_keys[3 + i], 0);
            win_recs[i] = shp.getInt(shp_keys[6 + i], 0);
        }

        for (int i = 0; i < 3; i++) {
            String best_msg = "최고기록: ";
            if (best_recs[i] != -1) {
                best_msg = best_msg + best_recs[i];
            }
            else {
                best_msg = best_msg + "-";
            }
            best_tvs[i].setText(best_msg);

            String total_msg = "전체 게임: " + total_recs[i];
            total_tvs[i].setText(total_msg);
            String win_msg = "승리한 게임: " + win_recs[i];
            win_tvs[i].setText(win_msg);

            String rate_msg = "승률: ";
            if (total_recs[i] != 0) {
                double win_rate = (double)win_recs[i] / total_recs[i];
                rate_msg = rate_msg + String.format(getString(R.string.fraction), win_rate);
            }
            else {
                rate_msg = rate_msg + "-";
            }
            rate_tvs[i].setText(rate_msg);
        }
    }
}