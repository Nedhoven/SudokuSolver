package solver;

import java.util.Arrays;
import java.util.LinkedList;

public class LocalSearch implements SolverInterface {
    private final Integer size;
    private final char empty = '0';
    private int root;

    private char[][] grid;
    private boolean[][] reserved;

    private int calls = 0;

    private LinkedList<Integer> tabu;

    private int[] lastChanged;

    private double thresh = 0;

    public LocalSearch(int n) {
        size = n;
        root = (int)(Math.sqrt(size));
        reserved = new boolean[size][size];
        tabu = new LinkedList<Integer>();
        lastChanged = new int[2];
    }

    public void init() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    reserved[i][j] = true;
                }
                else {
                    reserved[i][j] = false;
                    grid[i][j] = getChar((int)(Math.random() * size));
                }
            }
        }
    }

    private int getNumConflictsForPos(int row, int col, int num) {
        int numConflicts = 0;
        int box = root * (row / root) + (col/root);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (getNum(grid[i][j]) == num) {
                    numConflicts += 1;
                }
            }
        }

        int rb = root * (row/root);
        int cb = root * (col/root);
        for (int k = 0; k < root; k++) {
            for (int l = 0; l < root; l++) {
                if (getNum(grid[k][l] )== num) {
                    numConflicts += 1;
                }
            }
        }
        return numConflicts;
    }

    private int getNumConflicts() {
        int totalNumConflicts = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                totalNumConflicts += getNumConflictsForPos(i, j, getNum(grid[i][j]));
            }
        }
        return totalNumConflicts;
    }

    private int[] getMostConflictedPos() {
        int[] pos = new int[2];
        pos[0] = -1;
        pos[1] = -1;
        int maxConflicts = -1;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tabu.indexOf(size*i+j) != -1) {
                    continue;
                }
                int numConflicts = getNumConflictsForPos(i, j, getNum(grid[i][j]));
                if (numConflicts > maxConflicts) {
                    maxConflicts = numConflicts;
                    pos[0] = i;
                    pos[1] = j;
                }
            }
        }

        return pos;
    }

    private int getLeastConflictedValForPos(int row, int col) {
        int minNumConflicts = 3*(size) + 1;
        int leastConflictedVal = -1;
        for (int num = 0; num < size; num++) {
            if (num == getNum(grid[row][col])) {
                continue;
            }
            int numConflicts = getNumConflictsForPos(row, col, num);
            if (numConflicts < minNumConflicts) {
                minNumConflicts = numConflicts;
                leastConflictedVal = num;
            }
        }
        return leastConflictedVal;
    }

    private boolean localSearch() {
        int[] pos = new int[2];
        do {
            pos[0] = (int) (Math.random() * size);
            pos[1] = (int) (Math.random() * size);
        } while (reserved[pos[0]][pos[1]]);

        int val = (int)(Math.random() * size);
        if (getNumConflictsForPos(pos[0], pos[1], val) < getNumConflictsForPos(pos[0], pos[1], getNum(grid[0][1]))) {
            grid[pos[0]][pos[1]] = getChar(val);
        }
        else {
            double prob = Math.random();
            if (prob < thresh) {
                grid[pos[0]][pos[1]] = getChar(val);
            }
        }
        int numConflicts = getNumConflicts();
        if (numConflicts > 0) {
            if (calls % 100000 == 0) {
                System.out.println(String.valueOf(numConflicts));
            }
            return false;
        }
        else {
            return true;
        }
        //if (getNumConflictsForPos())
//        double prob = Math.random();
//        int[] pos = new int[2];
//        if (prob < thresh) {
//            do {
//                pos[0] = (int) (Math.random() * size);
//                pos[1] = (int) (Math.random() * size);
//            } while (reserved[pos[0]][pos[1]]);
//        }
//        else {
//            pos = getMostConflictedPos();
//        }
//        prob = Math.random();
//        int val;
//        if (prob < thresh) {
//            val = (int)(Math.random() * size);
//        }
//        else {
//            val = getLeastConflictedValForPos(pos[0], pos[1]);
//        }
//        tabu.addLast(pos[0] * size + pos[1]);
//        if (tabu.size() > 5) {
//            tabu.removeFirst();
//        }
//        grid[pos[0]][pos[1]] = getChar(val);
//        lastChanged[0] = pos[0];
//        lastChanged[1] = pos[1];
//        int numConflicts = getNumConflicts();
//        if (numConflicts > 0) {
//            if (calls % 100000 == 0) {
//                System.out.println(String.valueOf(numConflicts));
//            }
//            return false;
//        }
//        else {
//            return true;
//        }
    }

    public void show(char[][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == lastChanged[0] && j == lastChanged[1]) {
                    System.out.print("*" + grid[i][j] + "*");
                }
                else {
                    System.out.print(" " + grid[i][j] + " ");
                }
            }
            System.out.print('\n');
        }
//        for (char[] arr : board) {
//            System.out.println(Arrays.toString(arr));
//        }
    }

    private int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }

    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }

    public char[][] solveSudoku(char[][] board) {
        return board;
//        grid = board;
//        System.out.println("Initializing");
//        double startTime = System.nanoTime();
////        if (!init()) {
////            return false;
////        }
//        init();
//        double endTime = System.nanoTime();
//        double time = (endTime - startTime) / 1000;
//        System.out.println("init time: " + time + " us!");
//        System.out.println();
//
//        while(!localSearch()) {
//            if (calls % 100000 == 0) {
//                show(grid);
//                thresh = thresh * 999/1000;
//                        //1.0/100000000.0;
//                System.out.println(String.valueOf(thresh));
//            }
//            calls++;
//            if (calls > 100000000) {
//                return false;
//            }
//        }
//        return true;
//
//        //System.out.println("row: " + row);
//        //System.out.println("col: " + col);
//        //System.out.println("box: " + box);
//        //System.out.println("empty cells at each row: " + emptyRow);
//        //System.out.println("empty cells at each col: " + emptyCol);
//        //int[] pos = getIndex();
//        //boolean ret = dfs(pos[0], pos[1], 0);
//        //System.out.println("recursive calls: " + reccalls.toString());
//        //System.out.println("recursive depth: " + maxRecdepth.toString());
//
    }

}