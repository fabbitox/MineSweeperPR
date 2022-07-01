package com.first.minesweeperpr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private TableLayout board;
    private int boardWidth;
    private int boardHeight;
    private int[] adjCells;
    private Queue<Integer> toBeOpen;
    private int remainedCount;
    private TextView remainedTv;
    private boolean overFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        board = findViewById(R.id.board);
        View valueInput = findViewById(R.id.value_input);
        TextView colText = findViewById(R.id.column_text);
        TextView rowText = findViewById(R.id.row_text);
        TextView mineText = findViewById(R.id.mine_text);
        SeekBar colBar = findViewById(R.id.column_bar);
        SeekBar rowBar = findViewById(R.id.row_bar);
        SeekBar mineBar = findViewById(R.id.mine_bar);
        Button startBtn = findViewById(R.id.start_btn);
        Button restartBtn = findViewById(R.id.restart_btn);
        View root = findViewById(R.id.root);
        root.setBackgroundColor(0xcceeddff);// 배경 색
        remainedTv = findViewById(R.id.remained_count);
        overFlag = false;

        game = Game.getInstance();
        toBeOpen = new LinkedList<>();

        final int[] counts = {16, 16, 40};

        colBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colText.setText(String.valueOf(progress + 5));
                counts[0] = progress + 5;
                mineBar.setMax(counts[0] * counts[1] - 14);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rowBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rowText.setText(String.valueOf(progress + 8));
                counts[1] = progress + 8;
                mineBar.setMax(counts[0] * counts[1] - 14);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mineBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mineText.setText(String.valueOf(progress + 4));
                counts[2] = progress + 4;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startBtn.setOnClickListener(v -> {
            getBoardSize();
            // 값 받기
            fillBoard(counts[0], counts[1], counts[2]);
            remainedCount = counts[2];
            valueInput.setVisibility(View.INVISIBLE);
            restartBtn.setVisibility(View.VISIBLE);
            remainedTv.setText(String.valueOf(remainedCount));
            remainedTv.setVisibility(View.VISIBLE);
        });
        // 초기화해서 다시 시작할 수 있도록
        restartBtn.setOnClickListener(v -> {
            valueInput.setVisibility(View.VISIBLE);
            restartBtn.setVisibility(View.INVISIBLE);
            remainedTv.setVisibility(View.INVISIBLE);
            overFlag = false;
            board.removeAllViews();
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

    private void fillBoard(int columnCount, int rowCount, int mineCount) {
        int width = boardWidth / columnCount;
        int height = boardHeight / rowCount;
        int buttonSize = Math.min(width, height);
        int i, j;

        game.positionMine(columnCount, rowCount, mineCount);// 폭탄 위치 잡기
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
                        open(currIb, index);
                    }
                });
                ib.setOnLongClickListener(v -> {
                    if (game.isOpened(index)) {// 열린 셀이면
                        openAdjWithFlag(index);// 깃발 수가 폭탄 수와 맞을 때 깃발 이외의 주위 셀을 열어 줌
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
        board.setX(board.getLeft() + (spaceWidth >> 1));
        board.setY(board.getTop() + (spaceHeight >> 1));
    }

    private void open(ImageButton ib, int index) {// 셀 열기
        ib.setBackgroundColor(0xddeeddff);// 연 셀 색
        game.setImage(ib, index);
        game.setOpened(index);
        int aroundMine = game.countAround(index);
        if (game.isMine(index)) {
            overFlag = true;
        }
        if (aroundMine == 0) {
            openAdjCells(index);
        }
    }

    private void openAdjCells(int index) {// 주위 셀 자동으로 열어주는 기능
        adjCells = game.getAdjacentCells(index);
        for (int i = 0; i < 8; i++) {
            int around = adjCells[i];
            if (game.isValidIndex(around, index)) {
                if (!game.isOpened(around)) {
                    toBeOpen.add(around);// 주위 열어야 할 셀들 등록
                    game.setOpened(around);
                }
            }
        }
        openQueue();
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
            remainedCount++;
        }
        else {// 깃발 없는 상태 -> 깃발
            ib.setImageResource(R.drawable.flag);
            ib.setTag(R.string.flag, true);
            remainedCount--;
        }
        remainedTv.setText(String.valueOf(remainedCount));// 폭탄 수 업데이트
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

    private void openAdjWithFlag(int index) {
        adjCells = game.getAdjacentCells(index);
        int flagCount = 0;
        int mineCount = game.countAround(index);
        for (int i = 0; i < 8; i++) {// 주위 깃발 수 세기
            int around = adjCells[i];
            if (game.isValidIndex(around, index)) {
                if (getFlagState(getIbByIndex(around))) {
                    flagCount++;
                }
            }
        }
        if (flagCount == mineCount) {// 깃발 수가 맞으면
            for (int i = 0; i < 8; i++) {
                int around = adjCells[i];
                if (game.isValidIndex(around, index)) {
                    if (!getFlagState(getIbByIndex(around))) {
                        toBeOpen.add(around);
                    }
                }
            }
            openQueue();
        }
    }

    private ImageButton getIbByIndex(int index) {// 해당하는 index 이미지 버튼 return
        int columnCount = game.columnCount;
        int row = index / columnCount;
        int column = index % columnCount;
        TableRow tr = (TableRow)board.getChildAt(row);
        return (ImageButton)tr.getChildAt(column);
    }
}