package com.first.minesweeperpr;

import android.widget.ImageButton;
import android.widget.TableLayout;

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
    private int columnCount;
    private int bombLeft;
    private int totalCellCount;
    private boolean[] bombMap;// 지뢰 있으면 true

    public void positionBomb(int column, int row, int bomb) {// 지뢰 위치 랜덤으로 생성
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

    private void initialize(int column, int row, int bomb) {
        columnCount = column;
        totalCellCount = column * row;
        bombLeft = bomb;
        bombMap = new boolean[totalCellCount];
        for (int i = 0; i < totalCellCount; i++) {
            bombMap[i] = false;
        }
        random.setSeed(currentTimeMillis());
    }

    public int countAround(int index) {
        int bombCount = 0;
        int[] arounds = new int[] {
                getLeftUp(index), getUp(index), getRightUp(index), getLeft(index), getRight(index),
                getLeftDown(index), getDown(index), getRightDown(index)
        };
        for (int i = 0; i < 8; i++) {
            if (isValidIndex(arounds[i]) && bombMap[arounds[i]]) {
                bombCount++;
            }
        }
        return bombCount;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < totalCellCount;
    }

    private int getLeftUp(int index) {
        return index - columnCount - 1;
    }

    private int getUp(int index) {
        return index - columnCount;
    }

    private int getRightUp(int index) {
        return index - columnCount + 1;
    }

    private int getLeft(int index) {
        return index - 1;
    }

    private int getRight(int index) {
        return index + 1;
    }

    private int getLeftDown(int index) {
        return index + columnCount - 1;
    }

    private int getDown(int index) {
        return index + columnCount;
    }

    private int getRightDown(int index) {
        return index + columnCount + 1;
    }

    public void setImage(ImageButton ib, int index) {
        if (bombMap[index]) {
            ib.setImageResource(R.drawable.bomb);
        }
        else {
            int aroundBomb = countAround(index);
            if (aroundBomb == 0) {
                ib.setImageResource(R.drawable.blank);
            }
            else if (aroundBomb == 1) {
                ib.setImageResource(R.drawable.one);
            }
            else if (aroundBomb == 2) {
                ib.setImageResource(R.drawable.two);
            }
            else if (aroundBomb == 3) {
                ib.setImageResource(R.drawable.three);
            }
            else if (aroundBomb == 4) {
                ib.setImageResource(R.drawable.four);
            }
            else if (aroundBomb == 5) {
                ib.setImageResource(R.drawable.five);
            }
            else if (aroundBomb == 6) {
                ib.setImageResource(R.drawable.six);
            }
            else if (aroundBomb == 7) {
                ib.setImageResource(R.drawable.seven);
            }
            else {
                ib.setImageResource(R.drawable.eight);
            }
        }
    }
}