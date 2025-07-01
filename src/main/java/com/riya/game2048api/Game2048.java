package com.riya.game2048api;

import java.util.Random;

public class Game2048 {
    private int[][] board;
    private int score;
    private static final int SIZE = 4;
    private Random random;

    public Game2048() {
        board = new int[SIZE][SIZE];
        score = 0;
        random = new Random();
        addRandomTile();
        addRandomTile();
    }

    public int[][] getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    public void move(String direction) {
        boolean changed = false;

        switch (direction.toLowerCase()) {
            case "w":
            case "up":
                changed = moveUp();
                break;
            case "s":
            case "down":
                changed = moveDown();
                break;
            case "a":
            case "left":
                changed = moveLeft();
                break;
            case "d":
            case "right":
                changed = moveRight();
                break;
        }

        if (changed) {
            addRandomTile();
        }
    }

    private void addRandomTile() {
        int emptyCount = 0;
        for (int[] row : board)
            for (int val : row)
                if (val == 0)
                    emptyCount++;

        if (emptyCount == 0) return;

        int pos = random.nextInt(emptyCount);
        int value = random.nextDouble() < 0.9 ? 2 : 4;

        int count = 0;
        outer:
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] == 0)
                    if (count++ == pos) {
                        board[i][j] = value;
                        break outer;
                    }
    }

    private boolean moveLeft() {
        boolean changed = false;

        for (int i = 0; i < SIZE; i++) {
            int[] row = board[i];
            int[] newRow = new int[SIZE];
            int index = 0;

            for (int j = 0; j < SIZE; j++)
                if (row[j] != 0)
                    newRow[index++] = row[j];

            for (int j = 0; j < SIZE - 1; j++) {
                if (newRow[j] != 0 && newRow[j] == newRow[j + 1]) {
                    newRow[j] *= 2;
                    score += newRow[j];
                    newRow[j + 1] = 0;
                    j++;
                }
            }

            int[] compressed = new int[SIZE];
            index = 0;
            for (int j = 0; j < SIZE; j++)
                if (newRow[j] != 0)
                    compressed[index++] = newRow[j];

            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != compressed[j]) {
                    changed = true;
                }
                board[i][j] = compressed[j];
            }
        }

        return changed;
    }

    private boolean moveRight() {
        rotate180();
        boolean changed = moveLeft();
        rotate180();
        return changed;
    }

    private boolean moveUp() {
        transpose();
        boolean changed = moveLeft();
        transpose();
        return changed;
    }

    private boolean moveDown() {
        transpose();
        rotate180();
        boolean changed = moveLeft();
        rotate180();
        transpose();
        return changed;
    }

    private void transpose() {
        for (int i = 0; i < SIZE; i++)
            for (int j = i + 1; j < SIZE; j++) {
                int tmp = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = tmp;
            }
    }

    private void rotate180() {
        for (int i = 0; i < SIZE / 2; i++) {
            int[] tmp = board[i];
            board[i] = board[SIZE - i - 1];
            board[SIZE - i - 1] = tmp;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE / 2; j++) {
                int tmp = board[i][j];
                board[i][j] = board[i][SIZE - j - 1];
                board[i][SIZE - j - 1] = tmp;
            }
        }
    }
}
