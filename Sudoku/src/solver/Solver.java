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

    private final int varmode = DEFAULT;

    // 2D ArrayList whose entry at each cell is the domain of the cell, itself an ArrayList of Integers
    private ArrayList<ArrayList<ArrayList<Integer>>> domain;
    // 2D ArrayList whose entry at each cell is an object storing the size of the domain and some other information
    private ArrayList<ArrayList<DomainSizeEntry>> domainSize;
    // A stack to keep track of the current path in the search; each entry tracks:
    //   the current cell,
    //   the changes made to the domain via forward checking after populating the cell
    //   the index (in the array of domain values for the cell) of the current value
    //   a reference to the list of values
    private Stack<Entry> history;

    private int iterations = 0; // track number of iterations of search for diagnostic purposes
    private Integer maxDepth = 0; // track depth

    // Used for identifying the MRV
    private PriorityQueue<DomainSizeEntry> sizeQueue;
    // Used for ordering domain sizes in the queue
    private Comparator<DomainSizeEntry> domainSizeComparator;
    // Used for ordering
    private Comparator<PlacesSizeEntry> placesSizeComparator;

    // For Norvig's second strategy
    Places places = null;

    // Convert a grid character into an integer value
    private int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }

    // Convert an integer value into a grid character
    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }
    private int[] setDomain(int i, int j, ArrayList<Integer> nums) {
        int[] change = new int[4];
        change[0] = i;
        change[1] = j;
        DomainSizeEntry domSizeE = domainSize.get(i).get(j);
//        System.out.println("DSE " + domSizeE.size);
        change[2] = domSizeE.size;
        //domain.get(i).get(j).size();
        ArrayList<Integer> dom = domain.get(i).get(j);

        Collections.sort(nums);

        int box = getBox(i, j);
        Iterator<Integer> numsIt = nums.iterator();
        int currNum = numsIt.next();
        for (int k = 0; k < size; k++) {
            int dind = dom.indexOf(k);

            if (k == currNum) {
                // in nums, not in domain
                if (dind >= domSizeE.size) {
                    dom.remove(dind);
                    dom.add(0, k);
                    domSizeE.size++;
                    if (varmode == SECOND) {
                        places.add(i, j, box, k);
                    }
                }
                if (numsIt.hasNext()) {
                    currNum = numsIt.next();
                }
                else {
                    currNum = -1;
                }
            }
            else {
                // not in nums, but is in domain
                if (dind < domSizeE.size) {
                    dom.remove(dind);
                    dom.add(domSizeE.size - 1, k);
                    domSizeE.size--;
                    if (varmode == SECOND) {
                        places.remove(i, j, box, k);
                    }
                }
            }
        }

        //int numsSize = nums.size();
        //domainSize.get(i).get(j).size = numsSize;
        change[3] = domSizeE.size;

        DomainSizeEntry e =  domainSize.get(i).get(j);
        sizeQueue.remove(e);
        sizeQueue.add(e);
        return change;
    }
//
//    private int[] setDomain(int i, int j, ArrayList<Integer> nums) {
//        int[] change = new int[4];
//        change[0] = i;
//        change[1] = j;
//        change[2] = domainSize.get(i).get(j).size;
//        ArrayList<Integer> dom = domain.get(i).get(j);
//        for (int num : nums) {
//            dom.remove(dom.indexOf(num));
//            dom.add(0, num);
//        }
//        int numsSize = nums.size();
//        domainSize.get(i).get(j).size = numsSize;
//        change[3] = numsSize;
//
//        DomainSizeEntry e =  domainSize.get(i).get(j);
//        sizeQueue.remove(e);
//        sizeQueue.add(e);
//
//        places.updateFromDomainChange(i, j, change[2], change[3]);
//        return change;
//    }

    private boolean isInDomain(int r, int c, int num) {
        ArrayList<Integer> dom = domain.get(r).get(c);
        int domSize = domainSize.get(r).get(c).size;
        int index = dom.indexOf(num);
        if (index >= domSize) {
            return false;
        }
        else {
            return true;
        }
    }

    private int[] removeFromDomain(int r, int c, int num) {
        ArrayList<Integer> dom = domain.get(r).get(c);
        int domSize = domainSize.get(r).get(c).size;
        if (dom.indexOf(num) >= domSize) {
            return null;
        }
        dom.remove(dom.indexOf(num));
        dom.add(domSize - 1, num);
        domainSize.get(r).get(c).size = domSize - 1;
        int[] domainChange = new int[4];
        domainChange[0] = r;
        domainChange[1] = c;
        domainChange[2] = domSize;
        domainChange[3] = domSize - 1;
        if (domSize <= 1) {
        }
        DomainSizeEntry e  = domainSize.get(r).get(c);
        sizeQueue.remove(e);
        sizeQueue.add(e);

        if (varmode == SECOND) {
            places.remove(r, c, getBox(r, c), num);
        }
        return domainChange;
    }

    private boolean pushGrid(int r, int c, char cc, int currValI, ArrayList<Integer> vals) {
        boolean pruneDeadend = false;
        int num = getNum(cc);
        grid[r][c] = cc;

        DomainSizeEntry e = domainSize.get(r).get(c);
        e.isEmpty = false;
        sizeQueue.remove(e);
        sizeQueue.add(e);

        ArrayList<Integer> nums = new ArrayList<Integer>();
        nums.add(num);
        int[] change = setDomain(r, c, nums);
        PruneResult pruneRes = pruneDomains(r, c, num);
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
        DomainSizeEntry se = domainSize.get(r).get(c);
        se.isEmpty = true;
        sizeQueue.remove(se);
        sizeQueue.add(se);
        Set<int[]> changes = currEntry.domainChanges;
        for (int[] change : changes) {
            DomainSizeEntry e = domainSize.get(change[0]).get(change[1]);
            e.size = change[2];
            sizeQueue.remove(e);
            sizeQueue.add(e);

            ArrayList<Integer> dom = domain.get(change[0]).get(change[1]);
            for (int k = change[3]; k < change[2]; k++) {
                int num = dom.get(k);
                if (varmode == SECOND) {
                    places.add(change[0], change[1], getBox(change[0], change[1]), num);
                }
            }
        }
        if (history.size() == 0) {
            return null;
        }
        return history.peek();

    }

    private boolean initDomain() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    ArrayList<Integer> nums = new ArrayList<Integer>();
                    int num = getNum(grid[i][j]);
                    nums.add(num);
                    setDomain(i, j, nums);
                    DomainSizeEntry e = domainSize.get(i).get(j);
                    e.isEmpty = false;
                    sizeQueue.remove(e);
                    sizeQueue.add(e);

                    if (varmode == SECOND) {
                        places.fillIn(i, j, getBox(i, j), num);
                    }
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    if (pruneDomains(i, j, getNum(grid[i][j])).hasZero) {
                        System.out.println("BADUHOH");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void fillDomain() {
        domain = new ArrayList<ArrayList<ArrayList<Integer>>>();
        domainSize = new ArrayList<ArrayList<DomainSizeEntry>>();
        for (int i = 0; i < size; i++) {
            domain.add(new ArrayList<ArrayList<Integer>>());
            domainSize.add(new ArrayList<DomainSizeEntry>());
            for (int j = 0; j < size; j++) {
                domain.get(i).add(new ArrayList<Integer>());
                for (int num = 0; num < size; num++) {
                    domain.get(i).get(j).add(num);
                }
                DomainSizeEntry e = new DomainSizeEntry(i, j, size, true);
                sizeQueue.add(e);
                domainSize.get(i).add(e);

            }
        }
    }

    public Solver(int n) {
        size = n;
        root = (int) Math.sqrt(n);

        domainSizeComparator = new Comparator<DomainSizeEntry>() {
            public int compare(DomainSizeEntry e, DomainSizeEntry f) {
                if (e.isEmpty && !f.isEmpty) {
                    return -1;
                }
                else if (!e.isEmpty && f.isEmpty) {
                    return 1;
                }
                return e.size - f.size;

            }
        };
        sizeQueue = new PriorityQueue<DomainSizeEntry>(size*size, domainSizeComparator);
        fillDomain();
        history = new Stack<Entry>();
        if (varmode == SECOND) {
            places = new Places(size);
        }
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


            if (domainSize.get(row).get(col).size > 1) {
                System.out.println("UH OH");
            }

            if (domainSize.get(row).get(col).size == 0) {
                show(grid);
                System.out.println(" BAD " + String.valueOf(row) + " " + String.valueOf(col));
                return false;
            }
            for (int domNum : getDomainVals(row, col)) {
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
                    int oldRowmateSize = domainSize.get(row).get(k).size;
                    removeFromDomain(row, k, num);
                    int newRowmateSize = domainSize.get(row).get(k).size;

                    if (newRowmateSize == 1 && oldRowmateSize > 1) {

                        lst.add(getIntFromPosPair(row, k));
                    }
                }

                if (k != row && grid[k][col] == empty) {
                    int oldColmateSize = domainSize.get(k).get(col).size;

                    removeFromDomain(k, col, num);
                    int newColmateSize = domainSize.get(k).get(col).size;

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
                int oldBoxmateSize = domainSize.get(i).get(j).size;

                removeFromDomain(i, j, num);
                int newBoxmateSize = domainSize.get(i).get(j).size;

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

                if (domainSize.get(i).get(j).size == 1) {
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
        if (!initDomain()) {
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
        DomainSizeEntry e = sizeQueue.peek();
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
                return places.getPosFromPlaces(pe, grid, size, domain, domainSize);
            }
        }
        return minpos;
    }


    private int getBox(int r, int c) {
        return root*(r/root) + (c/root);
    }

    private ArrayList<int[]> getBoxPositions(int box) {
        ArrayList<int[]> positions = new ArrayList<int[]>();
        for (int i = 0; i < root; i++) {
            for (int j = 0; j < root; j++) {
                int bi = box / root;
                int bj = box % root;
                int[] pos = new int[2];
                pos[0] = root*bi + i;
                pos[1] = root*bj + j;
                positions.add(pos);
            }
        }
        return positions;
    }

    private PruneResult pruneDomains(int r, int c, int num) {
        boolean hasZero = false;

        int row = r;
        int col = c;
        int box = getBox(r, c);

        Set<int[]> domainChanges = new HashSet<int[]>();

        for (int j = 0; j < size; j++) {
            if (j == c) continue;
            if (grid[r][j] != empty) continue;
            int[] domainChange = removeFromDomain(row, j, num);
            if (domainChange != null) {
                if (domainChange[3] == 0) {
                    hasZero = true;
                }
                domainChanges.add(domainChange);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i == r) continue;
            if (grid[i][c] != empty) continue;
            int[] domainChange = removeFromDomain(i, col, num);
            if (domainChange != null) {
                if (domainChange[3] == 0) {
                    hasZero = true;
                }
                domainChanges.add(domainChange);
            }
        }
        for (int[] pos : getBoxPositions(box)) {
            if (pos[0] == r && pos[1] == c) continue;
            if (grid[pos[0]][pos[1]] != empty) continue;
            int[] domainChange = removeFromDomain(pos[0], pos[1], num);
            if (domainChange != null) {
                if (domainChange[3] == 0) {
                    hasZero = true;
                }
                domainChanges.add(domainChange);
            }
        }
        return new PruneResult(domainChanges, hasZero);
    }

    private boolean isFull(char[][] currGrid) {
        return !sizeQueue.peek().isEmpty;
    }

    private ArrayList<Integer> getDomainVals(int r, int c) {
        ArrayList<Integer> vals = new ArrayList<Integer>();
        int domSize = domainSize.get(r).get(c).size;
        for (int i = 0; i < domSize; i++) {
            vals.add(domain.get(r).get(c).get(i));
        }
        return vals;
    }

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
                dom = domain.get(r).get(c);
            }
            if (domainSize.get(r).get(c).size == 0) {
                System.err.println("The algorithm selected a row and column with zero-size domain.");
            }

            // For logging purposes
            updateDepthForLogging(depth);

            // Backtracking condition: if we have run out of values for the current cell
            if (currValI == domainSize.get(r).get(c).size) {
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
}