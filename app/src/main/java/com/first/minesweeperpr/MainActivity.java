package com.first.minesweeperpr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private TableLayout board;
    private int boardWidth;
    private int boardHeight;
    private int[] adjCells;
    private Queue<Integer> toBeOpen;
    private int remainedCount;
    private int explodedCount;
    private TextView remainedTv;
    private TextView explodedTv;
    private boolean overFlag;
    private int foundIndex;
    private boolean finishFlag;
    private boolean flagChecked;
    private boolean firstFlag;
    private Timer timer;
    private int timerCount;
    private TextView timerView;
    private boolean quickMode;
    private boolean pauseFlag;
    private View valueInput;
    private View gameUi;
    private ImageButton flagBtn;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide navigation bar
        View decorView = getWindow().getDecorView();
        int systemUiVis = decorView.getSystemUiVisibility();
        systemUiVis |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        systemUiVis |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        systemUiVis |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(systemUiVis);

        // findViewById
        board = findViewById(R.id.board);
        valueInput = findViewById(R.id.value_input);
        TextView colText = findViewById(R.id.column_text);
        TextView rowText = findViewById(R.id.row_text);
        TextView mineText = findViewById(R.id.mine_text);
        SeekBar colBar = findViewById(R.id.column_bar);
        SeekBar rowBar = findViewById(R.id.row_bar);
        SeekBar mineBar = findViewById(R.id.mine_bar);
        Button startBtn = findViewById(R.id.start_btn);
        Button restartBtn = findViewById(R.id.restart_btn);
        View root = findViewById(R.id.root);
        remainedTv = findViewById(R.id.remained_count);
        explodedTv = findViewById(R.id.exploded_count);
        gameUi = findViewById(R.id.for_game);
        flagBtn = findViewById(R.id.flag_btn);
        timerView = findViewById(R.id.time);

        // initialize variables
        overFlag = false;
        foundIndex = 0;
        flagChecked = false;
        finishFlag = false;
        firstFlag = true;
        root.setBackgroundColor(0xcceeddff);// 배경 색
        game = Game.getInstance();
        toBeOpen = new LinkedList<>();
        final int[] counts = {5, 8, 4};
        timerCount = 0;
        quickMode = false;
        pauseFlag = false;

        // 난이도 선택
        findViewById(R.id.easy).setOnClickListener(v -> {
            colBar.setProgress(4);// 9
            rowBar.setProgress(1);// 9
            mineBar.setProgress(6);// 10
        });
        findViewById(R.id.normal).setOnClickListener(v -> {
            colBar.setProgress(11);// 16
            rowBar.setProgress(8);// 16
            mineBar.setProgress(28);// 40
        });
        findViewById(R.id.hard).setOnClickListener(v -> {
            colBar.setProgress(11);// 16
            rowBar.setProgress(22);// 30
            mineBar.setProgress(75);// 99
        });

        // SeekBar 값과 TextView, 변수 상호작용
        colBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colText.setText(String.format("가로: %s", (progress + 5)));
                counts[0] = progress + 5;
                mineBar.setMax(counts[0] * counts[1] / 5 * 2);
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
                rowText.setText(String.format("세로: %s", progress + 8));
                counts[1] = progress + 8;
                mineBar.setMax(counts[0] * counts[1] / 5 * 2);
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
                counts[2] = progress + counts[0] * counts[1] / 20;
                double mineRate = (double)counts[2] / counts[0] / counts[1] * 100;
                mineText.setText(String.format(getString(R.string.mine_text_format), counts[2], mineRate));
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
            explodedCount = 0;
            valueInput.setVisibility(View.INVISIBLE);
            gameUi.setVisibility(View.VISIBLE);
            remainedTv.setText(String.valueOf(remainedCount));
            explodedTv.setText(String.valueOf(explodedCount));
            timerView.setText(String.valueOf(timerCount));
        });
        // 초기화해서 다시 시작할 수 있도록
        restartBtn.setOnClickListener(v -> restartSetting(valueInput, gameUi, flagBtn));

        flagBtn.setOnClickListener(v -> {
            ImageButton ib = (ImageButton)v;
            if (flagChecked) {
                flagChecked = false;
                ib.setImageResource(R.drawable.mine);
            }
            else {
                flagChecked = true;
                ib.setImageResource(R.drawable.flag);
            }
        });
    }

    private void restartSetting(View valueInput, View gameUi, ImageButton flagBtn) {
        valueInput.setVisibility(View.VISIBLE);
        gameUi.setVisibility(View.INVISIBLE);
        overFlag = false;
        foundIndex = 0;
        flagChecked = false;
        flagBtn.setImageResource(R.drawable.mine);
        finishFlag = false;
        firstFlag = true;
        timerCount = 0;
        timerView.setText(String.valueOf(timerCount));
        if (timer != null) timer.cancel();
        board.removeAllViews();
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

        game.positionMine(columnCount, rowCount, mineCount);// 지뢰 위치 잡기
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
                    if (flagChecked) {// flag mode
                        openF(currIb, index);
                    }
                    else if (!getFlagState(currIb)) {// 깃발 표시 안 했을 때
                        open(currIb, index);
                    }
                });
                ib.setOnLongClickListener(v -> {
                    if (game.isOpened(index)) {// 열린 셀이면
                        openAdjWithFlag(index);// 깃발 수가 지뢰 수와 맞을 때 깃발 이외의 주위 셀을 열어 줌
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
        if (firstFlag) {// start timer
            timer = new Timer();
            firstFlag = false;
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!pauseFlag) {
                        timerCount++;
                        timerView.setText(String.valueOf(timerCount));
                    }
                }
            };
            game.safeStart(index);
            timer.schedule(timerTask, 0, 1000);
        }
        game.setImage(ib, index);
        game.setOpened(index);
        Log.d("open", String.valueOf(index));
        foundIndex = game.foundTo(foundIndex);
        if (foundIndex == -1 && !finishFlag) {// game is finished
            finishFlag = true;
            timer.cancel();
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setTitle("finish");
            String message;
            if (overFlag) {
                message = explodedCount + "회의 실수로";
            }
            else {
                message = "실수 없이";
            }
            alertBuilder.setMessage(message + " 지뢰가 없는 곳을 " + timerCount + "초 만에 모두 찾아냈습니다!");
            alertBuilder.show();
        }
        if (game.isMine(index)) {
            if (!overFlag) {
                overFlag = true;
                pauseFlag = true;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("game over");
                alertBuilder.setMessage("지뢰를 터뜨렸습니다! 계속 하시겠습니까?");
                alertBuilder.setPositiveButton("계속", (dialog, which) -> pauseFlag = false);
                alertBuilder.setNegativeButton("종료", (dialog, which) -> {
                    timer.cancel();
                    restartSetting(valueInput, gameUi, flagBtn);
                });
                alertBuilder.show();
            }
            remainedCount--;
            remainedTv.setText(String.valueOf(remainedCount));
            explodedCount++;
            explodedTv.setText(String.valueOf(explodedCount));
            ib.setEnabled(false);
        }
        else if (game.countAround(index) == 0) {
            openAdjCells(index);
        }
    }

    private void openF(ImageButton ib, int index) {
        if (game.isOpened(index)) {
            quickMode = true;
            openAdjWithFlag(index);
            quickMode = false;
        }
        else if (!quickMode) {
            toggleFlag(ib);
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
        remainedTv.setText(String.valueOf(remainedCount));// 지뢰 수 업데이트
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
            if (game.isValidIndex(around, index)) {// 깃발 표시
                if (getFlagState(getIbByIndex(around))) {
                    flagCount++;
                }
                else if (game.isMine(around) && game.isOpened(around)) {// 터뜨린 지뢰
                    flagCount++;
                }
            }
        }
        if (flagCount == mineCount) {// 깃발 수가 맞으면
            for (int i = 0; i < 8; i++) {
                int around = adjCells[i];
                if (game.isValidIndex(around, index)) {
                    if (!getFlagState(getIbByIndex(around)) && !game.isOpened(around)) {
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