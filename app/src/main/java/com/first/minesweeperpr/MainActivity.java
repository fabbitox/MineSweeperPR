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
        View root = findViewById(R.id.root);
        root.setBackgroundColor(0xcceeddff);// 배경 색

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
                ib.setBackgroundColor(0x99faf5ff);// 안 연 셀 색
                ib.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ib.setImageResource(R.drawable.blank);
                ib.setOnClickListener(v -> {
                    ImageButton currIb = (ImageButton)v;
                    if (!getFlagState(currIb)) {// 깃발 표시 안 했을 때
                        open((ImageButton) v, index);
                    }
                });
                ib.setOnLongClickListener(v -> {
                    if (game.isOpened(index)) {// 열린 셀이면
                        openAroundsWithFlag(index);// 깃발 수가 폭탄 수와 맞을 때 깃발 이외의 주위 셀을 열어 줌
                    }
                    else {// 안 열린 셀이면 깃발을 표시하거나 없앰
                        toggleFlag((ImageButton)v);
                    }
                    return true;
                });
                row.addView(ib);
            }
        }
        // 보드가 중앙에 오도록
        int spaceWidth = boardWidth - buttonSize * columnCount;
        int spaceHeight = boardHeight - buttonSize * rowCount;
        board.setX(board.getX() + (spaceWidth >> 1));
        board.setY(spaceHeight >> 1);
    }

    private void open(ImageButton ib, int index) {// 셀 열기
        ib.setBackgroundColor(0xddeeddff);// 연 셀 색
        game.setImage(ib, index);
        game.setOpened(index);
        openArounds(index);
    }

    private void openArounds(int index) {
        int aroundBomb = game.countAround(index);
        if (aroundBomb == 0 && !game.isBomb(index)) {// 주위에 폭탄 없을 때, 폭탄일 때는 게임 종료니까 따로
            arounds = game.getArounds(index);// 주위 셀 자동으로 열어주는 기능
            for (int i = 0; i < 8; i++) {
                int around = arounds[i];
                if (game.isValidIndex(around, index)) {
                    if (!game.isOpened(around)) {
                        toBeOpen.add(around);// 주위 열어야 할 셀들 등록
                        game.setOpened(around);
                    }
                }
            }
            openQueue();
        }
    }

    private void openQueue() {
        while (!toBeOpen.isEmpty()) {// 빈 상태에서는 실행 안 되기 때문에 NullPointer 날 리가 없음
            @SuppressWarnings("ConstantConditions") int index = toBeOpen.poll();
            open(getIbByIndex(index), index);// 열어줌
        }
    }

    private void toggleFlag(ImageButton ib) {
        boolean flagState = getFlagState(ib);
        if (flagState) {// 깃발 꽂은 상태 -> 지우기
            ib.setImageResource(R.drawable.blank);
            ib.setTag(R.string.flag, false);
        }
        else {// 깃발 없는 상태 -> 깃발
            ib.setImageResource(R.drawable.flag);
            ib.setTag(R.string.flag, true);
        }
    }

    private boolean getFlagState(ImageButton ib) {
        boolean flagState;
        Object tag = ib.getTag(R.string.flag);
        if (tag != null) {// 상태 불러오기
            flagState = (boolean)tag;
        }
        else {
            flagState = false;
        }
        return flagState;
    }

    private void openAroundsWithFlag(int index) {
        arounds = game.getArounds(index);
        int flagCount = 0;
        int bombCount = game.countAround(index);
        for (int i = 0; i < 8; i++) {// 주위 깃발 수 세기
            int around = arounds[i];
            if (game.isValidIndex(around, index)) {
                if (getFlagState(getIbByIndex(around))) {
                    flagCount++;
                }
            }
        }
        if (flagCount == bombCount) {// 깃발 수가 맞으면
            for (int i = 0; i < 8; i++) {
                int around = arounds[i];
                if (game.isValidIndex(around, index)) {
                    if (!getFlagState(getIbByIndex(around))) {
                        toBeOpen.add(around);
                    }
                }
            }
            openQueue();
        }
    }

    private ImageButton getIbByIndex(int index) {// index에 해당하는 이미지 버튼 return
        int columnCount = game.columnCount;
        int row = index / columnCount;
        int column = index % columnCount;
        TableRow tr = (TableRow)board.getChildAt(row);
        return (ImageButton)tr.getChildAt(column);
    }
}