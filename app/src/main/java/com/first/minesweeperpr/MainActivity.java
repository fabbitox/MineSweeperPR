package com.first.minesweeperpr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {
    private TableLayout board;
    private int boardWidth;
    private int boardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View valueInput = findViewById(R.id.value_input);
        EditText columnInput = findViewById(R.id.column_input);
        EditText rowInput = findViewById(R.id.row_input);
        Button startBtn = findViewById(R.id.start_btn);
        Button restartBtn = findViewById(R.id.restart_btn);
        board = findViewById(R.id.board);

        int columnMax = 20;
        int rowMax = 40;
        boardWidth = board.getWidth();
        boardHeight = board.getHeight();

        startBtn.setOnClickListener(v -> {
            String columnStr = columnInput.getText().toString();
            String rowStr = rowInput.getText().toString();

            if (!columnStr.equals("") && !rowStr.equals("")) {// 값이 있을 때
                int column = Integer.parseInt(columnStr);
                int row = Integer.parseInt(rowStr);

                if (column > columnMax) column = columnMax;
                if (row > rowMax) row = rowMax;
                makeBoard(column, row);


                valueInput.setVisibility(View.INVISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
            }
        });

        restartBtn.setOnClickListener(v -> {
            valueInput.setVisibility(View.VISIBLE);
            board.setVisibility(View.INVISIBLE);
        });
    }

    private void makeBoard(int columnCount, int rowCount) {
        int width = boardWidth / columnCount;
        int height = boardHeight / rowCount;
        int buttonSize = Math.min(width, height);

        for (int i = 0; i < rowCount; i++) {
            TableRow row = new TableRow(this);
            board.addView(row);
            for (int j = 0; j < columnCount; j++) {
                Button button = new Button(this);
                button.setMinimumWidth(buttonSize);
                button.setMinimumHeight(buttonSize);
                button.setWidth(buttonSize);
                button.setHeight(buttonSize);
                button.setBackgroundColor(0xff000000 + 0x33 * (i * columnCount + j));
                row.addView(button);
            }
        }
        // 중앙에 오도록
        int spaceWidth = boardWidth - buttonSize * columnCount;
        int spaceHeight = boardHeight - buttonSize * rowCount;
        board.setX(spaceWidth >> 1);
        board.setY(spaceHeight >> 1);
    }

    private void clearBoard() {//보드도 레이아웃 파일에서 빼자 코드 수정 필요

    }
}