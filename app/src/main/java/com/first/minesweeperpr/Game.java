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
    private int mineLeft;
    private int totalCellCount;
    private boolean[] mineMap;// 지뢰 있으면 true
    private boolean[] opened;// 열린 셀리면 true

    public void positionMine(int column, int row, int mine) {// 지뢰 위치 랜덤으로 생성
        initialize(column, row, mine);
        int mineIndex;
        while (mineLeft > 0) {
            mineIndex = random.nextInt(totalCellCount);
            if (!mineMap[mineIndex]) {
                mineMap[mineIndex] = true;
                mineLeft--;
            }
        }
    }

    private void initialize(int column, int row, int mine) {// 초기화
        columnCount = column;
        totalCellCount = column * row;
        mineLeft = mine;
        mineMap = new boolean[totalCellCount];
        opened = new boolean[totalCellCount];
        for (int i = 0; i < totalCellCount; i++) {
            mineMap[i] = false;
            opened[i] = false;
        }
        random.setSeed(currentTimeMillis());
    }

    public int countAround(int index) {// 주위 폭탄 수 계산
        int mineCount = 0;
        int[] adjCells = getAdjacentCells(index);// 주위 셀
        for (int i = 0; i < 8; i++) {
            if (isValidIndex(adjCells[i], index) && mineMap[adjCells[i]]) {
                mineCount++;
            }
        }
        return mineCount;
    }

    public int[] getAdjacentCells(int index) {
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
        if (mineMap[index]) {
            ib.setImageResource(R.drawable.exploded);
        }
        else {
            int aroundMine = countAround(index);
            switch (aroundMine) {
                case 0:
                    ib.setImageResource(R.drawable.blank);
                    break;
                case 1:
                    ib.setImageResource(R.drawable.one);
                    break;
                case 2:
                    ib.setImageResource(R.drawable.two);
                    break;
                case 3:
                    ib.setImageResource(R.drawable.three);
                    break;
                case 4:
                    ib.setImageResource(R.drawable.four);
                    break;
                case 5:
                    ib.setImageResource(R.drawable.five);
                    break;
                case 6:
                    ib.setImageResource(R.drawable.six);
                    break;
                case 7:
                    ib.setImageResource(R.drawable.seven);
                    break;
                case 8:
                    ib.setImageResource(R.drawable.eight);
                    break;
            }
        }
    }

    public boolean isOpened(int index) {
        return opened[index];
    }

    public void setOpened(int index) {
        opened[index] = true;
    }

    public boolean isMine(int index) {
        return mineMap[index];
    }
}