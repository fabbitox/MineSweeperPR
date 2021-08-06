package com.first.minesweeperpr;

import android.widget.ImageButton;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class Game {
    // singleton pattern
    public static Game instance;
    private Game() {}
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    private final Random random = new Random();
    private int bombLeft;
    private int totalCellCount;
    private boolean[] bombMap;

    public void positionBomb(int column, int row, int bomb) {
        initialize(column, row, bomb);
        int bombIndex;
        while (bombLeft > 0) {
            bombIndex = random.nextInt(totalCellCount);
            if (!bombMap[bombIndex]) {
                bombMap[bombIndex] = true;
                bombLeft--;
            }
        }
    }

    public void setBombImage(ImageButton ib, int index) {// 테스트하기 위함
        if (bombMap[index]) {
            ib.setImageResource(R.drawable.bomb);
        }
        else {
            ib.setImageResource(R.drawable.blank);
        }
    }

    private void initialize(int column, int row, int bomb) {
        totalCellCount = column * row;
        bombLeft = bomb;
        bombMap = new boolean[totalCellCount];
        for (int i = 0; i < totalCellCount; i++) {
            bombMap[i] = false;
        }
        random.setSeed(currentTimeMillis());
    }

    public void setNumberImage(ImageButton ib, int whatImage) {// 일단 만들어 뒀는데 수정해서 쓰려나
        if (whatImage == 0) {
            ib.setImageResource(R.drawable.blank);
        }
        else if (whatImage == 1) {
            ib.setImageResource(R.drawable.one);
        }
        else if (whatImage == 2) {
            ib.setImageResource(R.drawable.two);
        }
        else if (whatImage == 3) {
            ib.setImageResource(R.drawable.three);
        }
        else if (whatImage == 4) {
            ib.setImageResource(R.drawable.four);
        }
        else if (whatImage == 5) {
            ib.setImageResource(R.drawable.five);
        }
        else if (whatImage == 6) {
            ib.setImageResource(R.drawable.six);
        }
        else if (whatImage == 7) {
            ib.setImageResource(R.drawable.seven);
        }
        else if (whatImage == 8) {
            ib.setImageResource(R.drawable.eight);
        }
        else {
            ib.setImageResource(R.drawable.bomb);
        }
    }
}
