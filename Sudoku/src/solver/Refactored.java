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

    private int[] getPosPairFromInt(Integer posint) {
        int[] pos = new int[2];
        pos[0] = posint / size;
        pos[1] = posint % size;
        return pos;
    }

    private Integer getIntFromPosPair(int i, int j) {
        return i * size + j;
    }


    private boolean propagate(LinkedList<Integer> lst, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        while (lst.size() > 0) {
            Integer posint = lst.removeFirst();
            int[] pos = getPosPairFromInt(posint);
            int row = pos[0];
            int col = pos[1];

            //System.out.println("ROWCOL: " + String.valueOf(row) + " " + String.valueOf(col) + " " + posint.toString());

            if (currDomain.get(row).get(col).size() > 1) {
                System.out.println("UH OH");
            }

            if (currDomain.get(row).get(col).size() == 0) {
                show(currGrid);
                System.out.println(" BAD " + String.valueOf(row) + " " + String.valueOf(col));
                return false;
            }
            if (currGrid[pos[0]][pos[1]] == empty) {
                currGrid[pos[0]][pos[1]] = getChar(currDomain.get(row).get(col).iterator().next());
            }
            int num = getNum(currGrid[pos[0]][pos[1]]);

            System.out.println("CURRENT POS: " + String.valueOf(row) + " " + String.valueOf(col));

            for (int k = 0; k < size; k++) {
                Set<Integer> rowmateDom = currDomain.get(row).get(k);
                Set<Integer> colmateDom = currDomain.get(k).get(col);

                if (k != col && currGrid[row][k] == empty) {
                    int oldRowmateSize = rowmateDom.size();
                    rowmateDom.remove(num);
                    int newRowmateSize = rowmateDom.size();
                    if (newRowmateSize == 1 && oldRowmateSize > 1) {
                        //int newnum = rowmateDom.iterator().next();
                        //currGrid[row][k] = getChar(newnum);
                        System.out.println("ADDING ROWPOS: " + String.valueOf(row) + " " + String.valueOf(k));
                        lst.add(getIntFromPosPair(row, k));
                        //updateDomains(row, k, newnum, currGrid, currDomain);
                    }
                }

                if (k != row && currGrid[k][col] == empty) {
                    int oldColmateSize = colmateDom.size();
                    colmateDom.remove(num);
                    int newColmateSize = colmateDom.size();
                    if (newColmateSize == 1 && oldColmateSize > 1) {
                        //int newnum = colmateDom.iterator().next();
                        //currGrid[k][col] = getChar(newnum);
                        System.out.println("ADDING COLPOS: " + String.valueOf(k) + " " + String.valueOf(col));
                        lst.add(getIntFromPosPair(k, col));
                        //updateDomains(row, k, newnum, currGrid, currDomain);
                    }
                }
            }

            int box = getBox(row, col);
            for (int[] boxPos : getBoxPositions(box)) {
                int i = boxPos[0];
                int j = boxPos[1];

                if (i == row || j == col) continue; // we dealt with rowmates and colmates earlier
                if (currGrid[i][j] != empty) continue;
                Set<Integer> boxmateDom = currDomain.get(i).get(j);
                int oldBoxmateSize = boxmateDom.size();
                boxmateDom.remove(num);
                int newBoxmateSize = boxmateDom.size();
                if (newBoxmateSize == 1 && oldBoxmateSize > 1) {
                    //int newnum = boxmateDom.iterator().next();
                    //currGrid[i][j] = getChar(newnum);
                    System.out.println("ADDING BOXPOS: " + String.valueOf(i) + " " + String.valueOf(j));
                    lst.add(getIntFromPosPair(i, j));
                }
            }
        }
        return true;
    }

    private boolean ac3(char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        LinkedList<Integer> lst = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (currGrid[i][j] != empty) {
                    currDomain.get(i).set(j, new HashSet<Integer>());
                    currDomain.get(i).get(j).add(getNum(currGrid[i][j]));
                    lst.addLast(getIntFromPosPair(i, j));
                }
            }
        }
        return propagate(lst, currGrid, currDomain);
    }

    private boolean mac(int r, int c, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        LinkedList<Integer> lst = new LinkedList<Integer>();
        lst.addLast(getIntFromPosPair(r, c));
        //System.out.println("STARTMAC " + String.valueOf(r) + " " + String.valueOf(c));
        return propagate(lst, currGrid, currDomain);
    }

    private boolean init() {
        boolean ans = true;
        boolean res = checkGrid(grid);
        initDomain();
        if (!res) {
            System.out.println("BAD");
        }
        if (!ac3(grid, domain)) {
            return false;
        }
        return res;
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

//        System.out.println("BOX: " + String.valueOf(box));
        int row = r;
        int col = c;
        int box = getBox(r, c);

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

    private int getFreq(int num, int row, int col, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        int rowFreq = 0;
        int colFreq = 0;
        int boxFreq = 0;
        for (int k = 0; k < size; k++) {
            if (currDomain.get(row).get(k).contains(num)) {
                rowFreq++;
            }
            if (currDomain.get(k).get(col).contains(num)) {
                colFreq++;
            }
        }

        // box
        int rb = root * (row/root);
        int cb = root * (col/root);
        for (int k = 0; k < root; k++) {
            for (int l = 0; l < root; l++) {
                if (currDomain.get(rb + k).get(cb + l).contains(num)) {
                    boxFreq++;
                }
            }
        }
        return Math.max(rowFreq, Math.max(colFreq, boxFreq));
    }

    private ArrayList<Integer> getValOrder(int r, int c, Set<Integer> dom, ArrayList<ArrayList<Set<Integer>>> currDomain) {
        ArrayList<int[]> freqs = new ArrayList<int[]>();
        for (int num : dom) {
            int[] pair = new int[2];
            pair[0] = num;
            pair[1] = getFreq(num, r, c, currDomain);
            freqs.add(pair);
        }

        Comparator<int[]> compareByFreq = new Comparator<int[]>() {
            public int compare(int[] ar1, int[] ar2) {
                if (ar1[1] < ar2[1]) {
                    return -1;
                } else if (ar1[1] == ar2[1]) {
                    return 0;
                } else { // ar1[1] > ar2[1]
                    return 1;
                }
            }
        };
        Collections.sort(freqs, compareByFreq);

        ArrayList<Integer> vals = new ArrayList<Integer>();
        for (int k = 0; k < freqs.size(); k++) {
            vals.add(freqs.get(k)[0]);
        }
        return vals;
    }

    private char[][] dfs(int r, int c, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, int recdepth) {
//        if (recdepth > maxRecdepth) {
//            maxRecdepth = recdepth;
//        }
        reccalls+=1;
        if (reccalls % 100000 == 0) {
            System.out.println("RECCALLS : " + String.valueOf(reccalls));
            System.out.println("DEPTH " + String.valueOf(recdepth));
            show(currGrid);
        }

        Set<Integer> dom = currDomain.get(r).get(c);

        ArrayList<Integer> vals = getValOrder(r, c, dom, currDomain);

        for (int k = 0; k < vals.size(); k++) {
            int num = vals.get(k);
            char[][] newGrid = getNewGrid(currGrid);
            char cc = getChar(num);
            newGrid[r][c] = cc;

            ArrayList<ArrayList<Set<Integer>>> newDomain = getNewDomain(currDomain);
            newDomain.get(r).set(c, new HashSet<Integer>());
            newDomain.get(r).get(c).add(num);
            //updateDomains(r, c, num, newGrid, newDomain);

//            System.out.println("RCCC " + String.valueOf(r) + " " + String.valueOf(c) + " " + cc);
//            System.out.println(getChar(newDomain.get(r).get(c).iterator().next()));
            if (!mac(r, c, newGrid, newDomain)) {
                return null;
            }

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