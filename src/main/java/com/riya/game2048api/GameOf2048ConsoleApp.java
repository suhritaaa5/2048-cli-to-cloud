package com.riya.game2048api;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class GameOf2048ConsoleApp {

    public static final int SIZE = 4;
    public static final char MOVE_LEFT = 'A';
    public static final char MOVE_RIGHT = 'D';
    public static final char MOVE_UP = 'W';
    public static final char MOVE_DOWN = 'S';

    private static final String HIGHSCORE_FILE = "highscore.txt"; // File to store the high score
    private static int highScore = 0;  // Static variable to hold high score
    private int[][] board;
    private int score;
    private Random random;
    private Scanner scanner;

    public GameOf2048ConsoleApp() {
        board = new int[SIZE][SIZE];
        random = new Random();
        scanner = new Scanner(System.in);
        score = 0;
        loadHighScore(); 
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not load high score. Starting from zero.");
            highScore = 0; // Default to 0 if there's an error
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.out.println("Could not save high score.");
        }
    }

    public void showBoard() {
        System.out.println("Score: " + score + " | High Score: " + highScore);
        for (int i = 0; i < SIZE; i++) {
            System.out.print("-------".repeat(SIZE));
            System.out.println();
            for (int j = 0; j < SIZE; j++) {
                System.out.printf("| %4s ", board[i][j] == 0 ? "" : board[i][j]);
            }
            System.out.println("|");
        }
        System.out.print("-------".repeat(SIZE));
        System.out.println();
    }

    public void addRandomTile() {
        int i, j;
        do {
            i = random.nextInt(SIZE);
            j = random.nextInt(SIZE);        
        } 
        while (board[i][j] != 0);

        board[i][j] = (random.nextInt(10) < 9) ? 2 : 4; // 90% chance for 2, 10% for 4
    }

    public boolean gameWon() {
        return searchOnBoard(2048);
    }

    public boolean searchOnBoard(int x) {
        for (int[] row : board) {
            for (int tile : row) {
                if (tile == x) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean userCanMakeAMove() {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (board[i][j] == board[i][j + 1] || board[i][j] == board[i + 1][j]) {
                    return true;
                }
            }
        }
        for (int i = 0; i < SIZE - 1; i++) {
            if (board[i][SIZE - 1] == board[i + 1][SIZE - 1]) return true;
            if (board[SIZE - 1][i] == board[SIZE - 1][i + 1]) return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return gameWon() || (!searchOnBoard(0) && !userCanMakeAMove());
    }

    public boolean canMoveInDirection(char direction) {
        int[][] original = copyBoard();
        processMove(direction);
        boolean canMove = !areBoardsEqual(original, board);
        board = original; // Restore original board state after testing
        return canMove;
    }

    public char getUserMove() {
        System.out.println("Choose a move (W: Up, S: Down, A: Left, D: Right, H: Hint): ");
    
        // Check if there's input available, if not, return a default value.
        if (!scanner.hasNext()) {
            System.out.println("No input available. Using default move 'W'.");
            return 'W';  // Always use 'W' as default if no input available.
        }
    
        String input = scanner.nextLine().toUpperCase();
    
        if (input.isEmpty() || input.length() > 1 || "WASDH".indexOf(input.charAt(0)) == -1) {
            System.out.println("Invalid Input! Try again.");
            return getUserMove();
        }
    
        char move = input.charAt(0);
    
        if (move != 'H' && !canMoveInDirection(move)) {
            System.out.println("No more possible moves in this direction. Choose another direction.");
            return getUserMove();
        }
    
        return move;
    }
    
    public int[] processLeftMove(int row[]) {
        int[] newRow = new int[SIZE];
        int j = 0;
        for (int value : row) if (value != 0) newRow[j++] = value;
        for (int i = 0; i < SIZE - 1; i++) {
            if (newRow[i] != 0 && newRow[i] == newRow[i + 1]) {
                newRow[i] *= 2;
                score += newRow[i];
                newRow[i + 1] = 0;
                for (int k = i + 1; k < SIZE - 1; k++) newRow[k] = newRow[k + 1];
                newRow[SIZE - 1] = 0;
            }
        }
        return newRow;
    }

    public int[] processRightMove(int row[]) {
        int[] reversed = reverseArray(row);
        reversed = processLeftMove(reversed);
        return reverseArray(reversed);
    }

    public void processMove(char move) {
        if (move == 'H') {
            System.out.println("Hint: " + getBestMove());
            return;
        }
        int[][] original = copyBoard();
        switch (move) {
            case MOVE_LEFT -> {
                for (int i = 0; i < SIZE; i++) board[i] = processLeftMove(board[i]);
            }
            case MOVE_RIGHT -> {
                for (int i = 0; i < SIZE; i++) board[i] = processRightMove(board[i]);
            }
            case MOVE_UP -> {
                for (int j = 0; j < SIZE; j++) {
                    int[] column = new int[SIZE];
                    for (int i = 0; i < SIZE; i++) column[i] = board[i][j];
                    column = processLeftMove(column);
                    for (int i = 0; i < SIZE; i++) board[i][j] = column[i];
                }
            }
            case MOVE_DOWN -> {
                for (int j = 0; j < SIZE; j++) {
                    int[] column = new int[SIZE];
                    for (int i = 0; i < SIZE; i++) column[i] = board[i][j];
                    column = processRightMove(column);
                    for (int i = 0; i < SIZE; i++) board[i][j] = column[i];
                }
            }
        }
        if (!areBoardsEqual(original, board)) addRandomTile();
    }

    public int[] reverseArray(int arr[]) {
        int[] reverseArr = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            reverseArr[i] = arr[arr.length - i - 1];
        }
        return reverseArr;
    }

    private String getBestMove() {
        int mergesLeft = countPotentialMerges('A');
        int mergesRight = countPotentialMerges('D');
        int mergesUp = countPotentialMerges('W');
        int mergesDown = countPotentialMerges('S');

        int maxMerges = Math.max(Math.max(mergesLeft, mergesRight), Math.max(mergesUp, mergesDown));
        if (maxMerges == mergesLeft) return "Left";
        if (maxMerges == mergesRight) return "Right";
        if (maxMerges == mergesUp) return "Up";
        return "Down";
    }

    private int countPotentialMerges(char direction) {
        int[][] original = copyBoard();
        processMove(direction);
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (board[i][j] == board[i][j + 1]) count++;
                if (board[j][i] == board[j + 1][i]) count++;
            }
        }
        board = original;
        return count;
    }

    private boolean areBoardsEqual(int[][] board1, int[][] board2) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board1[i][j] != board2[i][j]) return false;
            }
        }
        return true;
    }

    private int[][] copyBoard() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    public void play() {
        addRandomTile();
        addRandomTile();
        showBoard();

        while (!isGameOver()) {
            char move = getUserMove();
            processMove(move);
            showBoard();
            
        }

        System.out.println("Game Over! Final Score: " + score);
        if (score > highScore) {
            highScore = score;
            saveHighScore();
            System.out.println("Congratulations! You've set a new high score.");
        }
    }

    public static void main(String[] args) {
        GameOf2048ConsoleApp game = new GameOf2048ConsoleApp();
        game.play();
    }
}
