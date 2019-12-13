
package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

public class Solver implements SolverInterface {
    
    private final int size;
    private final int root;
    private char[][] grid;
    private boolean[][] row;
    private boolean[][] col;
    private boolean[][] box;
    private int[] numRow;
    private boolean flag;
    private final char empty = '0';

    private Integer reccalls = 0;
    private Integer maxRecdepth = 0;

    private int lastRecdepth = 0;

    private ArrayList<ArrayList<Set<Integer>>> domain;


    public Solver() {
        size = 25;
        root = 5;
    }
    
    public Solver(int n) {
        size = n;
        root = (int) Math.sqrt(n);

        fillDomain();

    }
    
    private int getIndex(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }
    
    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }
    
    private boolean check(int x, int y, int num) {
        int b = root * (x / root) + (y / root);
        return !(row[x][num] || col[y][num] || box[b][num]);
    }
    
    private void add(int x, int y, int num, boolean f) {
        int b = root * (x / root) + (y / root);
        row[x][num] = f;
        col[y][num] = f;
        box[b][num] = f;
    }
    
    private int getRow() {
        int max = -1;
        int ans = -1;
        for (int i = 0; i < size; i++) {
            if (numRow[i] != size && numRow[i] > max) {
                ans = i;
                max = numRow[i];
            }
        }
        return ans;
    }

    private void fillDomain() {
        domain = new ArrayList<ArrayList<Set<Integer>>>();
        for (int i = 0; i < size; i++) {
            domain.add(new ArrayList<Set<Integer>>());
            for (int j = 0; j < size; j++) {
                domain.get(i).add(new HashSet<Integer>());
                for (int num = 0; num < size; num++) {
                    domain.get(i).get(j).add(num);
                }
            }
        }
    }


    private int getFreq(int num, int row, int col) {
        int rowFreq = 0;
        int colFreq = 0;
        int boxFreq = 0;
        for (int k = 0; k < size; k++) {
            if (domain.get(row).get(k).contains(num)) {
                rowFreq++;
            }
            if (domain.get(k).get(col).contains(num)) {
                colFreq++;
            }
        }

        // box
        int rb = root * (row/root);
        int cb = root * (col/root);
        for (int k = 0; k < root; k++) {
            for (int l = 0; l < root; l++) {
                if (domain.get(rb + k).get(cb + l).contains(num)) {
                    boxFreq++;
                }
            }
        }
        return Math.max(rowFreq, Math.max(colFreq, boxFreq));
    }


    private void init() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    numRow[i]++;
                    int num = getIndex(grid[i][j]);
                    if (row[i][num] || col[j][num]) {
                        flag = true;
                    }
                    row[i][num] = true;
                    col[j][num] = true;
                }
                int x = root * (i / root) + (j / root);
                int y = ((root * i) + (j % root)) % size;
                if (grid[x][y] != empty) {
                    int num = getIndex(grid[x][y]);
                    if (box[i][num]) {
                        flag = true;
                    }
                    box[i][num] = true;

                    // update cell domains
                    // row/col
                    for (int k = 0; k < size; k++) {
                        domain.get(i).get(k).remove(num);
                        domain.get(k).get(j).remove(num);
                    }

                    // box
                    int rb = root * (i/root);
                    int cb = root * (j/root);
                    for (int k = 0; k < root; k++) {
                        for (int l = 0; l < root; l++) {
                            domain.get(rb + k).get(cb + l).remove(num);
                        }
                    }

                }
            }
        }
    }
    
    private boolean dfs(int row, int col, int recdepth) {
        if (recdepth > maxRecdepth) {
            maxRecdepth = recdepth;
        }
        reccalls+=1;
        if (reccalls > 10000000) {
            return false;
        }
        if (col == size) {
            col = 0;
            row = getRow();
        }
        if (row == -1) {
            return true;
        }
        if (grid[row][col] != empty) {
            //System.out.println("GOING IN: " + Integer.valueOf(recdepth).toString());
            if (recdepth % 60 == 0 && recdepth != lastRecdepth) {
                lastRecdepth = recdepth;
                System.out.println("GOING IN: " + Integer.valueOf(recdepth).toString());
            }

            return dfs(row, col + 1, recdepth + 1);
        }
        //System.out.println("row = " + row);

        ArrayList<int[]> freqs = new ArrayList<int[]>();
        for (int num = 0; num < size; num++) {
            if (check(row, col, num)) {
                int[] pair = new int[2];
                pair[0] = num;
                pair[1] = getFreq(num, row, col);
                freqs.add(pair);
            }
        }

        Comparator<int[]> compareByFreq = new Comparator<int[]>() {
            public int compare(int[] ar1, int[] ar2) {
                if (ar1[1] < ar2[1]) {
                    return -1;
                }
                else if (ar1[1] == ar2[1]) {
                    return 0;
                }
                else { // ar1[1] > ar2[1]
                    return 1;
                }
            }
        };
        //int[] ar1, int[] ar2) -> ar1[1].compareTo(ar2[1]);
        Collections.sort(freqs, compareByFreq);

//        if (freqs.size() == 0 || freqs.get(0)[1] >= 13) {
//            return false;
//        }

//        System.out.println("****FREQS*****");
//        for (int i = 0; i < freqs.size(); i++) {
//            System.out.println("(" + String.valueOf(freqs.get(i)[0]) + ", " + String.valueOf(freqs.get(i)[1]) + ")");
//        }


        for (int i = 0; i < freqs.size(); i++) {
            int num = freqs.get(i)[0];
            add(row, col, num, true);
            grid[row][col] = getChar(num);
            numRow[row]++;
            if (recdepth % 60 == 0 && recdepth != lastRecdepth) {
                lastRecdepth = recdepth;
                System.out.println("GOING IN: " + Integer.valueOf(recdepth).toString());
            }
            if (dfs(row, col + 1, recdepth + 1)) {
                return true;
            }
            //System.out.println("backtrack on row = " + row);
            numRow[row]--;
            grid[row][col] = empty;
            add(row, col, num, false);
        }
        //System.out.println("GOING OUT: " + Integer.valueOf(recdepth).toString());

        return false;
    }
    
    public char[][] solveSudoku(char[][] board) {
        return board;
//        grid = board;
//        flag = false;
//        numRow = new int[size];
//        row = new boolean[size][size];
//        col = new boolean[size][size];
//        box = new boolean[size][size];
//        init();
//        if (flag) {
//            return false;
//        }
//        int r = getRow();
//        boolean ret = dfs(r, 0, 0);
//        System.out.println("Recursive calls: " + reccalls.toString());
//        System.out.println("Recursive depth: " + maxRecdepth.toString());
//        return ret;
    }
    
}
