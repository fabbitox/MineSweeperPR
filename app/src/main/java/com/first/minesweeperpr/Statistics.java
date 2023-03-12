package com.first.minesweeperpr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
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
            String best_msg = getString(R.string.best_str);
            if (best_recs[i] != -1) {
                best_msg = best_msg + best_recs[i] + getString(R.string.second);
            }
            else {
                best_msg = best_msg + "-";
            }
            best_tvs[i].setText(best_msg);

            String total_msg = getString(R.string.total_game_str) + total_recs[i];
            total_tvs[i].setText(total_msg);
            String win_msg = getString(R.string.win_game_str) + win_recs[i];
            win_tvs[i].setText(win_msg);

            String rate_msg = getString(R.string.win_rate_str);
            if (total_recs[i] != 0) {
                double win_rate = (double)win_recs[i] / total_recs[i] * 100;
                rate_msg = rate_msg + String.format(getString(R.string.fraction), win_rate);
            }
            else {
                rate_msg = rate_msg + "-";
            }
            rate_tvs[i].setText(rate_msg);
        }

        ImageButton delBtn = findViewById(R.id.del_rec_btn);
        delBtn.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Statistics.this);
            alertBuilder.setTitle("Delete check");
            alertBuilder.setMessage(R.string.delete_check_msg);
            alertBuilder.setPositiveButton(R.string.yes, (dialog, which) -> delete_recs(best_tvs, total_tvs, win_tvs, rate_tvs, shp, shp_keys));
            alertBuilder.setNegativeButton(R.string.no, (dialog, which) -> {});
            alertBuilder.show();
        });
    }

    private void delete_recs(TextView[] best_tvs, TextView[] total_tvs, TextView[] win_tvs, TextView[] rate_tvs, SharedPreferences shp, String[] shp_keys) {
        SharedPreferences.Editor editor = shp.edit();
        for (int i = 0; i < 9; i++) {
            editor.remove(shp_keys[i]);
        }
        editor.apply();

        for (int i = 0; i < 3; i++) {
            best_tvs[i].setText(R.string.best_0);
            total_tvs[i].setText(R.string.total_0);
            win_tvs[i].setText(R.string.win_0);
            rate_tvs[i].setText(R.string.win_rate_0);
        }
    }
}