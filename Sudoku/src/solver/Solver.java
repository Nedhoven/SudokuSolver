package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;
import java.lang.Thread;

public class Solver {

    private char[][] grid;
    private final Integer size; // size = N for an NxN grid
    private final Integer root; // sqrt(size)
    private final char empty = '0'; // character representing an empty cell
    private static final int DEFAULT = 0;
    private static final int SECOND = 1;

    private final int varmode = SECOND;

    // A stack to keep track of the current path in the search; each entry tracks:
    //   the current cell,
    //   the changes made to the domain via forward checking after populating the cell
    //   the index (in the array of domain values for the cell) of the current value
    //   a reference to the list of values
    private Stack<Entry> history;

    private int iterations = 0; // track number of iterations of search for diagnostic purposes
    private Integer maxDepth = 0; // track depth

    Domains domains;

    // For Norvig's second strategy
    Places places = null;

    // Convert a grid character into an integer value
    private int getNum(char c) {
        return Util.getNum(c);
    }

    // Convert an integer value into a grid character
    private char getChar(int num) {
        return Util.getChar(num);
    }




    ////////// HISTORY MANAGEMENT METHODS /////////
    private boolean pushGrid(int r, int c, char cc, int currValI, ArrayList<Integer> vals) {
        boolean pruneDeadend = false;
        int num = getNum(cc);
        grid[r][c] = cc;

        domains.fillIn(r, c);

        ArrayList<Integer> nums = new ArrayList<Integer>();
        nums.add(num);
        int[] change = domains.setDomain(r, c, nums);
        PruneResult pruneRes = domains.pruneDomains(r, c, num, grid);
        Set<int[]> domainChanges = pruneRes.changes;
        if (pruneRes.hasZero) {
            pruneDeadend = true;
        }
        domainChanges.add(change);

        Entry entry = new Entry();
        entry.r = r;
        entry.c = c;
        entry.valI = currValI;
        entry.vals = vals;
        entry.domainChanges = domainChanges;
        history.push(entry);

        if (varmode == SECOND) {
            places.fillIn(r, c, getBox(r, c), num);
        }
        return !pruneDeadend;
    }

    private Entry popGrid() {
        Entry currEntry = history.pop();
        int r = currEntry.r;
        int c = currEntry.c;
        if (varmode == SECOND) {
            places.unfill(r, c, getBox(r, c), getNum(grid[r][c]));
        }
        grid[r][c] = empty;
        domains.unfill(r, c);

        Set<int[]> changes = currEntry.domainChanges;
        domains.updateFromChanges(changes);
        if (history.size() == 0) {
            return null;
        }
        return history.peek();

    }

    public Solver(int n) {
        size = n;
        root = (int) Math.sqrt(n);

        if (varmode == SECOND) {
            places = new Places(size);
        }
        domains = new Domains(size, root, varmode, places);
        history = new Stack<Entry>();
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


    private boolean propagate(LinkedList<Integer> lst) {
        while (lst.size() > 0) {

            Integer posint = lst.removeFirst();
            int[] pos = getPosPairFromInt(posint);
            int row = pos[0];
            int col = pos[1];


            if (domains.getSize(row, col) > 1) {
                System.out.println("UH OH");
            }

            if (domains.getSize(row, col) == 0) {
                show(grid);
                System.out.println(" BAD " + String.valueOf(row) + " " + String.valueOf(col));
                return false;
            }
            for (int domNum : domains.getDomainVals(row, col)) {
                if (grid[pos[0]][pos[1]] == empty) {
                    grid[pos[0]][pos[1]] = getChar(domNum);
                    if (varmode == SECOND) {
                        places.fillIn(pos[0], pos[1], getBox(pos[0], pos[1]), domNum);
                    }
                }

                if (getChar(domNum) == '0') {
                    System.out.println("WHOOPS");
                }
            }
            int num = getNum(grid[pos[0]][pos[1]]);


            for (int k = 0; k < size; k++) {

                if (k != col && grid[row][k] == empty) {
                    int oldRowmateSize = domains.getSize(row, k);
                    domains.removeFromDomain(row, k, num);
                    int newRowmateSize = domains.getSize(row, k);

                    if (newRowmateSize == 1 && oldRowmateSize > 1) {

                        lst.add(getIntFromPosPair(row, k));
                    }
                }

                if (k != row && grid[k][col] == empty) {
                    int oldColmateSize = domains.getSize(k, col);

                    domains.removeFromDomain(k, col, num);
                    int newColmateSize = domains.getSize(k, col);

                    if (newColmateSize == 1 && oldColmateSize > 1) {

                        lst.add(getIntFromPosPair(k, col));
                    }
                }
            }

            int box = getBox(row, col);
            for (int[] boxPos : getBoxPositions(box)) {
                int i = boxPos[0];
                int j = boxPos[1];

                if (i == row || j == col) continue; // we dealt with rowmates and colmates earlier
                if (grid[i][j] != empty) continue;
                int oldBoxmateSize = domains.getSize(i, j);;

                domains.removeFromDomain(i, j, num);
                int newBoxmateSize = domains.getSize(i, j);

                if (newBoxmateSize == 1 && oldBoxmateSize > 1) {

                    lst.add(getIntFromPosPair(i, j));
                }
            }
        }
        return true;
    }

    private boolean ac3() {
        LinkedList<Integer> lst = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if (domains.getSize(i, j) == 1) {
                    lst.addLast(getIntFromPosPair(i, j));
                }
            }
        }
        boolean ret = propagate(lst);
        return ret;
    }



    private boolean init() {
        boolean ans = true;
        boolean res = checkGrid(grid);
        if (!domains.initDomain(grid)) {
            return false;
        }
        if (!res) {
            System.out.println("BAD");
        }
        if (!ac3()) {
            return false;
        }
        return res;
    }

    private int[] getNextPos() {
        DomainSizeEntry e = domains.peek();
        int[] minpos = new int[2];
        minpos[0] = e.r;
        minpos[1] = e.c;
        if (varmode == SECOND && e.size > 1) {
            PlacesSizeEntry pe = places.peek();
            if (pe.isFilledIn) {
                System.out.println("OOPS V BAD");
            }
            if (pe.size == 1) {
                //System.out.println("USING SIZE HEURISTIC!");
                return places.getPosFromPlaces(pe, grid, size, domains);
            }
        }
        return minpos;
    }


    private int getBox(int r, int c) {
        return Util.getBox(r, c, root);
    }

    private ArrayList<int[]> getBoxPositions(int box) {
        return Util.getBoxPositions(box, root);
    }



    ///////// LOGGING CODE ///////////
    private void logIteration(int iterations, int depth, char[][] grid) {
        System.out.println("ITERATIONS : " + String.valueOf(iterations));
        System.out.println("DEPTH " + String.valueOf(depth));
        show(grid);
        System.out.println();
    }

    private void updateDepthForLogging(int depth) {
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        iterations++;
        if (iterations % 1000000 == 0) {
            logIteration(iterations, depth, grid);
        }
    }



    ///////// MAIN SEARCH METHODS /////////
    private char[][] dfs() {
        int depth = 1; // for diagnostic purposes, keep track of depth
        int currValI = 0; // index into the arraylist of values for the current cell
        int r = -1; // row of the current cell
        int c = -1; // col of the current cell
        ArrayList<Integer> dom = null;
        Entry prevEntry = null;
        boolean backtracking = false;  // whether the search is currently backtracking
        do {
            if (!backtracking) {
                // Get the next cell to try
                int[] pos = getNextPos();
                r = pos[0];
                c = pos[1];
                dom = domains.getDomain(r, c);
            }
            if (domains.getSize(r, c) == 0) {
                System.err.println("The algorithm selected a row and column with zero-size domain.");
            }

            // For logging purposes
            updateDepthForLogging(depth);

            // Backtracking condition: if we have run out of values for the current cell
            if (currValI == domains.getSize(r, c)) {
                // backtrack!
                backtracking = true;
                depth--;
                if (depth == 0) {
                    // We ran out of values for the first cell, so it's unsolvable.
                    return null;
                }

                // `prevEntry` was already populated after the last value failed
                // so restore r, c, etc. from `prevEntry`.
                // (Since at the next iteration, we'll check this same condition against this r and c)
                r = prevEntry.r;
                c = prevEntry.c;
                // but we need to try the next value for the previous entry
                currValI = prevEntry.valI + 1;
                dom = prevEntry.vals;

                // update the `prevEntry` reference so it now points to the entry
                // before the previous entry (in case we have to backtrack again)
                prevEntry = popGrid();
                continue;
            }

            depth++;
            // For the current cell, get the next domain value to try
            int num = dom.get(currValI);
            char cc = getChar(num);
            // Try filling in the cell with the value. If it fails...
            if (!pushGrid(r, c, cc, currValI, dom)) {
                // backtrack!
                backtracking = true;
                // clear out the cell value and restore state.
                prevEntry = popGrid();
                currValI++;
                depth--;

                continue;
            }
            // Otherwise, pushing worked. If the grid is full...
            if (isFull(grid)) {
                //We win!
                return grid;
            }
            // Reset the current value index...
            currValI = 0;
            // And move forward
            backtracking = false;

        } while (true);
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

        if (isFull(grid)) {
            return grid;
        }
        for (int i = 0; i < history.size(); i++) {
            Entry hist = history.get(i);
        }

        char[][] resGrid = dfs();
        System.out.println("Iterations: " + iterations);
        System.out.println("DEPTH: " + maxDepth);
        return resGrid;
    }


    private boolean isFull(char[][] currGrid) {
        return !domains.peek().isEmpty;
    }

}