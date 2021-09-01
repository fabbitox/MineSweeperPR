package com.first.minesweeperpr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private TableLayout board;
    private int boardWidth;
    private int boardHeight;
    private Game game;
    private int[] arounds;
    private Queue<Integer> toBeOpen;

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
        toBeOpen = new LinkedList<>();

        startBtn.setOnClickListener(v -> {
            getBoardSize();
            // 값 받기
            String columnStr = columnInput.getText().toString();
            String rowStr = rowInput.getText().toString();
            String bombStr = bombInput.getText().toString();
            // 값이 있을 때
            if (!columnStr.equals("") && !rowStr.equals("") && !bombStr.equals("")) {
                int column = Integer.parseInt(columnStr);
                int row = Integer.parseInt(rowStr);
                int bomb = Integer.parseInt(bombStr);
                // 값이 범위 내인지
                boolean columnBigger = column > columnMax;
                boolean rowBigger = row > rowMax;
                boolean columnSmaller = column < columnMin;
                boolean rowSmaller = row < rowMin;
                // 범위 밖이면 조절
                if (columnBigger) column = columnMax;
                else if (columnSmaller) column = columnMin;
                if (rowBigger) row = rowMax;
                else if (rowSmaller) row = rowMin;
                // 폭탄 수
                int bombMax = column * row - 10;
                boolean bombBigger = bomb > bombMax;
                boolean bombSmaller = bomb < bombMin;
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

        game.positionBomb(columnCount, rowCount, bombCount);// 폭탄 위치 잡기
        for (i = 0; i < rowCount; i++) {
            TableRow row = new TableRow(this);
            board.addView(row);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(buttonSize, buttonSize);
            for (j = 0; j < columnCount; j++) {
                int index = i * columnCount + j;
                ImageButton ib = new ImageButton(this);
                ib.setLayoutParams(lp);
                ib.setBackgroundColor(0xffffffff);// 안 연 셀 색
                ib.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ib.setImageResource(R.drawable.blank);
                ib.setOnClickListener(v -> reveal((ImageButton) v, index));
                row.addView(ib);
            }
        }
        // 보드가 중앙에 오도록
        int spaceWidth = boardWidth - buttonSize * columnCount;
        int spaceHeight = boardHeight - buttonSize * rowCount;
        board.setX(spaceWidth >> 1);
        board.setY(spaceHeight >> 1);
    }

    private void reveal(ImageButton ib, int index) {// 주위에 폭탄 없을 때 자동으로 열어주기
        ib.setBackgroundColor(0xffddeeff);// 연 셀 색
        game.setImage(ib, index);
        int aroundBomb = game.countAround(index);
        if (aroundBomb == 0) {// 주위 열어야 할 셀들 등록
            arounds = game.getArounds(index);
            for (int i = 0; i < 8; i++) {
                int around = arounds[i];
                if (game.isValidIndex(around, index)) {
                    if (!game.isOpened(around)) {
                        toBeOpen.add(around);
                    }
                }
            }
        }
        while (!toBeOpen.isEmpty()) {// 열어줌
            int i = toBeOpen.poll();
            reveal(getIbByIndex(i), i);
        }
    }

    private ImageButton getIbByIndex(int index) {
        int columnCount = game.columnCount;
        int row = index / columnCount;
        int column = index % columnCount;
        TableRow tr = (TableRow)board.getChildAt(row);
        return (ImageButton)tr.getChildAt(column);
    }
}