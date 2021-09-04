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
    public int columnCount;
    private int bombLeft;
    private int totalCellCount;
    private boolean[] bombMap;// 지뢰 있으면 true
    private int[] arounds;// 주위 셀
    private boolean[] opened;// 열린 셀리면 true

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

    private void initialize(int column, int row, int bomb) {// 초기화
        columnCount = column;
        totalCellCount = column * row;
        bombLeft = bomb;
        bombMap = new boolean[totalCellCount];
        opened = new boolean[totalCellCount];
        for (int i = 0; i < totalCellCount; i++) {
            bombMap[i] = false;
            opened[i] = false;
        }
        random.setSeed(currentTimeMillis());
    }

    public int countAround(int index) {// 주위 폭탄 수 계산
        int bombCount = 0;
        arounds = getArounds(index);
        for (int i = 0; i < 8; i++) {
            if (isValidIndex(arounds[i], index) && bombMap[arounds[i]]) {
                bombCount++;
            }
        }
        return bombCount;
    }

    public int[] getArounds(int index) {
        return new int[]{
                getLeftUp(index), getUp(index), getRightUp(index), getLeft(index), getRight(index),
                getLeftDown(index), getDown(index), getRightDown(index)
        };
    }

    public boolean isValidIndex(int index, int origin) {
        boolean valid = index >= 0 && index < totalCellCount;
        int indCol = index % columnCount;
        int oriCol = origin % columnCount;

        int rightEnd = columnCount - 1;
        boolean isOriLeft = oriCol == 0;
        boolean isIndLeft = indCol == 0;
        boolean isOriRight = oriCol == rightEnd;
        boolean isIndRight = indCol == rightEnd;
        if (!valid) {
            return false;
        }
        else if (isOriLeft && isIndRight) {
            return false;
        }
        else return !isOriRight || !isIndLeft;// simplified, else if (&&) {false} true
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

    public void setImage(ImageButton ib, int index) {// 칸에 따라 숫자나 폭탄 보여줌
        if (bombMap[index]) {
            ib.setImageResource(R.drawable.exploded);
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

    public boolean isOpened(int index) {
        return opened[index];
    }

    public void setOpened(int index) {
        opened[index] = true;
    }

    public boolean isBomb(int index) {
        return bombMap[index];
    }
}