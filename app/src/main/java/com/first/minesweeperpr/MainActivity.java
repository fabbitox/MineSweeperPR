package com.first.minesweeperpr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {
    private TableLayout board;
    private int boardWidth;
    private int boardHeight;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        board = findViewById(R.id.board);
        View valueInput = findViewById(R.id.value_input);
        EditText columnInput = findViewById(R.id.column_input);
        EditText rowInput = findViewById(R.id.row_input);
        EditText bombInput = findViewById(R.id.bomb_input);
        Button startBtn = findViewById(R.id.start_btn);
        Button restartBtn = findViewById(R.id.restart_btn);

        int columnMin = 5;
        int rowMin = 8;
        int bombMin = 4;
        int columnMax = 20;
        int rowMax = 32;

        game = Game.getInstance();

        startBtn.setOnClickListener(v -> {
            getBoardSize();
            //값 받기
            String columnStr = columnInput.getText().toString();
            String rowStr = rowInput.getText().toString();
            String bombStr = bombInput.getText().toString();
            //값이 있을 때
            if (!columnStr.equals("") && !rowStr.equals("") && !bombStr.equals("")) {
                int column = Integer.parseInt(columnStr);
                int row = Integer.parseInt(rowStr);
                int bomb = Integer.parseInt(bombStr);
                //값이 범위 내인지
                boolean columnBigger = column > columnMax;
                boolean rowBigger = row > rowMax;
                int bombMax = column * row - 10;
                boolean bombBigger = bomb > bombMax;
                boolean columnSmaller = column < columnMin;
                boolean rowSmaller = row < rowMin;
                boolean bombSmaller = bomb < bombMin;
                //범위 밖이면 조절
                if (columnBigger) column = columnMax;
                else if (columnSmaller) column = columnMin;
                if (rowBigger) row = rowMax;
                else if (rowSmaller) row = rowMin;
                if (bombBigger) bomb = bombMax;
                else if (bombSmaller) bomb = bombMin;

                fillBoard(column, row, bomb);
                valueInput.setVisibility(View.INVISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
            }
        });
        // 초기화해서 다시 시작할 수 있도록
        restartBtn.setOnClickListener(v -> {
            valueInput.setVisibility(View.VISIBLE);
            restartBtn.setVisibility(View.INVISIBLE);
            board.removeAllViews();
            columnInput.setText("");
            rowInput.setText("");
            bombInput.setText("");
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

    private void fillBoard(int columnCount, int rowCount, int bombCount) {
        int width = boardWidth / columnCount;
        int height = boardHeight / rowCount;
        int buttonSize = Math.min(width, height);
        int i, j;

        game.positionBomb(columnCount, rowCount, bombCount);
        for (i = 0; i < rowCount; i++) {
            TableRow row = new TableRow(this);
            board.addView(row);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(buttonSize, buttonSize);
            for (j = 0; j < columnCount; j++) {
                ImageButton ib = new ImageButton(this);
                ib.setLayoutParams(lp);
                ib.setBackgroundColor(0xffffffff);
                ib.setScaleType(ImageView.ScaleType.CENTER_CROP);
                game.setBombImage(ib, i * columnCount + j);
                row.addView(ib);
            }
        }
        // 보드가 중앙에 오도록
        int spaceWidth = boardWidth - buttonSize * columnCount;
        int spaceHeight = boardHeight - buttonSize * rowCount;
        board.setX(spaceWidth >> 1);
        board.setY(spaceHeight >> 1);
    }
}
