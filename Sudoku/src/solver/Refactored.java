package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.lang.Thread;

public class Refactored implements SolverInterface {

    private char[][] grid;
    private final Integer size;
    private final Integer root;
    private final char empty = '0';
    private ArrayList<ArrayList<Set<Integer>>> domain;

    private Integer reccalls = 0;
    private Integer maxRecdepth = 0;
    private int lastRecdepth = 0;


    private int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }

    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }

    private void initDomain() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    updateDomains(i, j, getNum(grid[i][j]), grid, domain);
                }
            }
        }
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

    public Refactored(int n) {
        size = n;
        root = (int) Math.sqrt(n);
//        arr = new HashSet<Integer>();
//        fillArray();
//        row = new HashMap<Integer, Set<Integer>>();
//        allocate(row);
//        col = new HashMap<Integer, Set<Integer>>();
//        allocate(col);
//        box = new HashMap<Integer, Set<Integer>>();
//        allocate(box);
//        emptyRow = new HashMap<Integer, Set<Integer>>();
//        create(emptyRow);
//        emptyCol = new HashMap<Integer, Set<Integer>>();
//        create(emptyCol);
//
          fillDomain();
    }
    public static void show(char[][] board) {
        for (char[] arr : board) {
            System.out.println(Arrays.toString(arr));
        }
    }

    private boolean checkRow(int r, int c, char cc, char[][] currGrid) {
        for (int j = 0; j < size; j++) {
            if (j == c) continue;
            if (currGrid[r][j] == cc) {
                System.out.println("BAD ROW");
                return false;
            }
        }
        return true;
    }

    private boolean checkCol(int r, int c, char cc, char[][] currGrid) {
        for (int i = 0; i < size; i++) {
            if (i == r) continue;
            if (currGrid[i][c] == cc) {
                System.out.println("BAD COL");
                return false;
            }
        }
        return true;
    }

    private boolean checkBox(int r, int c, char cc, char[][] currGrid) {
        int box = getBox(r, c);
        for (int[] pos : getBoxPositions(box)) {
            if (pos[0] == r && pos[1] == c) continue;
            if (currGrid[pos[0]][pos[1]] ==  cc) {
                System.out.println("BAD BOX");
                return false;
            }
        }
        return true;
    }

    private boolean checkGrid(char[][] currGrid) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char cc = currGrid[i][j];
                if (cc == empty) continue;
                if (!checkRow(i, j, cc, currGrid)) return false;
                if (!checkCol(i, j, cc, currGrid)) return false;
                if (!checkBox(i, j, cc, currGrid)) return false;
            }
        }
        return true;
    }

    private boolean init() {
        boolean ans = true;
        boolean res = checkGrid(grid);
        initDomain();
        if (!res) {
            System.out.println("BAD");
        }
        return res;
//        if (!ac3(grid, domain)) {
//            return false;
//        }
    }

    private char[][] getNewGrid(char[][] currGrid) {
        char[][] newGrid = new char[size][size];
        for (int k = 0; k < size; k++) {
            for (int l = 0; l < size; l++) {
                newGrid[k][l] = currGrid[k][l];
            }
        }
        return newGrid;
    }

    private ArrayList<ArrayList<Set<Integer>>> getNewDomain(ArrayList<ArrayList<Set<Integer>>> currDomain) {
        ArrayList<ArrayList<Set<Integer>>> newDomain = new ArrayList<ArrayList<Set<Integer>>>();
        for (int k = 0; k < size; k++) {
            ArrayList<Set<Integer>> kdom = new ArrayList<Set<Integer>>();
            for (int l = 0; l < size; l++) {
                kdom.add(new HashSet<Integer>(currDomain.get(k).get(l)));
            }
            newDomain.add(kdom);
        }
        return newDomain;
    }

//    private int[] getNextPos(int r, int c, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain) {
//        int nextR = r;
//        int nextC = c;
//        do {
//            nextC++;
//
//            if (nextC == size) {
//                nextC = 0;
//                nextR++;
//            }
//            if (nextR == size) {
//                return null;
//            }
//        } while (currGrid[nextR][nextC] != empty);
//        int[] pos = new int[2];
//        pos[0] = nextR;
//        pos[1] = nextC;
//        return pos;
//    }

    private int[] getNextPos(int r, int c, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        int minDomainSize = size + 1;
        int[] minpos = new int[2];
        minpos[0] = -1;
        minpos[1] = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (currGrid[i][j] != empty) continue;
                int ijSize = currDomain.get(i).get(j).size();
                if (ijSize < minDomainSize) {
                    minDomainSize = ijSize;
                    minpos[0] = i;
                    minpos[1] = j;
                }
            }
        }
        return minpos;
    }

    private int getBox(int r, int c) {
        return root*(r/root) + (c/root);
    }

    private ArrayList<int[]> getBoxPositions(int box) {
        //System.out.println(String.valueOf(box));
        ArrayList<int[]> positions = new ArrayList<int[]>();
        for (int i = 0; i < root; i++) {
            //System.out.print('\n');
            for (int j = 0; j < root; j++) {
                int bi = box / root;
                int bj = box % root;
                int[] pos = new int[2];
                pos[0] = root*bi + i;
                pos[1] = root*bj + j;
                //System.out.print(String.valueOf(pos[0]) + ", " + String.valueOf(pos[1]) + " | ");
                positions.add(pos);
            }
        }
        //System.out.println();
        return positions;
    }

    private void updateDomains(int r, int c, int num, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        //show(currGrid);
//        System.out.println("POS: " + String.valueOf(r) + ", " + String.valueOf(c));

        int row = r;
        int col = c;
        int box = getBox(r, c);

//        System.out.println("BOX: " + String.valueOf(box));

        for (int j = 0; j < size; j++) {
//            System.out.println("ROWPOS: " + String.valueOf(row) + ", " + String.valueOf(j));
            if (j == c) continue;
            if (currGrid[r][j] != empty) continue;
            currDomain.get(row).get(j).remove(num);
        }
        for (int i = 0; i < size; i++) {
            if (i == r) continue;
            if (currGrid[i][c] != empty) continue;
            currDomain.get(i).get(col).remove(num);
        }
        for (int[] pos : getBoxPositions(box)) {
//            System.out.println("BOXPOS: " + String.valueOf(pos[0]) + ", " + String.valueOf(pos[1]));
            if (pos[0] == r && pos[1] == c) continue;
            if (currGrid[pos[0]][pos[1]] != empty) continue;
            currDomain.get(pos[0]).get(pos[1]).remove(num);
        }
    }

    private boolean isFull(char[][] currGrid) {
        boolean isFull = true;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (currGrid[i][j] == empty) {
                    isFull = false;
                }
            }
        }
        return isFull;
    }

    private char[][] dfs(int r, int c, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, int recdepth) {
//        if (recdepth > maxRecdepth) {
//            maxRecdepth = recdepth;
//        }
        reccalls+=1;
        if (reccalls % 100000 == 0) {
            System.out.println("RECCALLS : " + String.valueOf(reccalls));
            System.out.println("DEPTH " + String.valueOf(recdepth));
        }

        Set<Integer> dom = currDomain.get(r).get(c);
        for (int num : dom) {
            char[][] newGrid = getNewGrid(currGrid);
            char cc = getChar(num);
            newGrid[r][c] = cc;

            ArrayList<ArrayList<Set<Integer>>> newDomain = getNewDomain(currDomain);

            updateDomains(r, c, num, newGrid, newDomain);

//            if (!mac(r, c, newGrid, newDomain)) {
//                return null;
//            }

            if (isFull(newGrid)) {
                return newGrid;
            }
            int[] pos = getNextPos(r, c, newGrid, newDomain);

//            if (pos == null) {
//                return newGrid;
//            }

            char[][] resGrid = dfs(pos[0], pos[1], newGrid, newDomain, recdepth + 1);
            if (resGrid != null) {
                return resGrid;
            }
        }

        return null;
    }

    public char[][] solveSudoku(char[][] board) {
        grid = board;
        System.out.println("Initializing");
        double startTime = System.nanoTime();
        if (!init()) {
            return null;
        }
        double endTime = System.nanoTime();
        double time = (endTime - startTime) / 1000;
        System.out.println("init time: " + time + " us!");
        System.out.println();

        char[][] resGrid = dfs(0, 0, grid, domain, 0);


        return resGrid;
    }
}