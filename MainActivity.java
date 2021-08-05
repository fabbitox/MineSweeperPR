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

        board = findViewById(R.id.board);
        View valueInput = findViewById(R.id.value_input);
        EditText columnInput = findViewById(R.id.column_input);
        EditText rowInput = findViewById(R.id.row_input);
        Button startBtn = findViewById(R.id.start_btn);
        Button restartBtn = findViewById(R.id.restart_btn);

        int columnMin = 5;
        int rowMin = 8;
        int columnMax = 20;
        int rowMax = 36;

        startBtn.setOnClickListener(v -> {
            getBoardSize();
            //값 받기
            String columnStr = columnInput.getText().toString();
            String rowStr = rowInput.getText().toString();
            //값이 있을 때
            if (!columnStr.equals("") && !rowStr.equals("")) {
                int column = Integer.parseInt(columnStr);
                int row = Integer.parseInt(rowStr);
                //값이 범위 내인지
                boolean columnBigger = column > columnMax;
                boolean rowBigger = row > rowMax;
                boolean columnSmaller = column < columnMin;
                boolean rowSmaller = row < rowMin;
                //범위 밖이면 조절
                if (columnBigger) column = columnMax;
                else if (columnSmaller) column = columnMin;
                if (rowBigger) row = rowMax;
                else if (rowSmaller) row = rowMin;

                fillBoard(column, row);
                valueInput.setVisibility(View.INVISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
            }
        });

        restartBtn.setOnClickListener(v -> {
            valueInput.setVisibility(View.VISIBLE);
            restartBtn.setVisibility(View.INVISIBLE);
            board.removeAllViews();
            columnInput.setText("");
            rowInput.setText("");
        });
    }

    private void getBoardSize() {
        if (boardWidth == 0) {
            boardWidth = board.getWidth();
        }
        if (boardHeight == 0) {
            boardHeight = board.getHeight();
        }
    }

    private void fillBoard(int columnCount, int rowCount) {
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
}