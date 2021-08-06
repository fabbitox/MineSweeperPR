package com.first.minesweeperpr;

import android.widget.ImageButton;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class Game {
    // singleton pattern
    public static Game instance;
    private void Game() {}
    public Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    private Random random = new Random(currentTimeMillis());
    private int bombLeft;
    private int cellNumber;
    private boolean[] bombLocation;

    public void positionBomb(int column, int row, int bomb) {
        initialize(column, row, bomb);
    }

    private void initialize(int column, int row, int bomb) {
        cellNumber = column * row;
        bombLeft = bomb;
        bombLocation = new boolean[cellNumber];
        for (int i = 0; i < cellNumber; i++) {
            bombLocation[i] = false;
        }
    }

    public void setNumberImage(ImageButton ib, int whatImage) {
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
