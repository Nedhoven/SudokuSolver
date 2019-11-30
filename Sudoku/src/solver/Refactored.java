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
import java.util.PriorityQueue;
import java.util.Stack;
import java.lang.Thread;

import java.math.BigInteger;

public class Refactored implements SolverInterface {

    private char[][] grid;
    private final Integer size;
    private final Integer root;
    private final char empty = '0';
    private ArrayList<ArrayList<ArrayList<Integer>>> domain;

    private Stack<ArrayList<Integer>> calls;
    private ArrayList<ArrayList<DomainSizeEntry>> domainSize;
    private Stack<Entry> history;

    private BigInteger reccalls = BigInteger.valueOf((long)0);
    private Integer maxRecdepth = 0;
    private int lastRecdepth = 0;

    private PriorityQueue<DomainSizeEntry> sizeQueue;
    private Comparator<DomainSizeEntry> sizeComparator;

    private int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }

    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }

    private int[] setDomain(int i, int j, ArrayList<Integer> nums) {
        int[] change = new int[4];
        change[0] = i;
        change[1] = j;
        change[2] = domainSize.get(i).get(j).size;
        //domain.get(i).get(j).size();
        ArrayList<Integer> dom = domain.get(i).get(j);
        for (int num : nums) {
            dom.remove(dom.indexOf(num));
            dom.add(0, num);
        }
        int numsSize = nums.size();
        domainSize.get(i).get(j).size = numsSize;
        change[3] = numsSize;

        DomainSizeEntry e =  domainSize.get(i).get(j);
        sizeQueue.remove(e);
        sizeQueue.add(e);
        return change;
    }

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
//        System.out.println("INDEX");
//        System.out.println(r);
//        System.out.println(c);
//        System.out.println(domSize);
//        System.out.println(dom.size());
//        show(grid);
        dom.remove(dom.indexOf(num));
        dom.add(domSize - 1, num);
        domainSize.get(r).get(c).size = domSize - 1;
        int[] domainChange = new int[4];
        domainChange[0] = r;
        domainChange[1] = c;
        domainChange[2] = domSize;
        domainChange[3] = domSize - 1;
        if (domSize <= 1) {
//            System.out.println("PRUNED TO ZERO");
////
////            System.out.println("SIZE");
//            System.out.println(domSize);
        }
        DomainSizeEntry e  = domainSize.get(r).get(c);
        sizeQueue.remove(e);
        sizeQueue.add(e);
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
//            domainChanges = new HashSet<int[]>();
        }
        domainChanges.add(change);

        Entry entry = new Entry();
        entry.r = r;
        entry.c = c;
        entry.valI = currValI;
        entry.vals = vals;
        entry.domainChanges = domainChanges;
        history.push(entry);
        //System.out.println(entry.r + " " + entry.c + " " + getChar(entry.vals.get(entry.valI)));
        return !pruneDeadend;
    }

    private Entry popGrid() {
        Entry currEntry = history.pop();
        int r = currEntry.r;
        int c = currEntry.c;
//        if (r == 3 && c == 8) {
//            System.out.println("POPPINGGRID");
//            System.out.println(r);
//            System.out.println(c);
//        }
        //System.out.println("POP " + r + " " + c + " " + grid[r][c]);
        grid[r][c] = empty;
        DomainSizeEntry se = domainSize.get(r).get(c);
        se.isEmpty = true;
        sizeQueue.remove(se);
        sizeQueue.add(se);
        Set<int[]> changes = currEntry.domainChanges;
        for (int[] change : changes) {
//            domainSize.get(change[0]).get(change[1]).size = change[2];
            DomainSizeEntry e = domainSize.get(change[0]).get(change[1]);
            e.size = change[2];
            sizeQueue.remove(e);
            sizeQueue.add(e);
        }
        if (history.size() == 0) {
            return null;
        }
        return history.peek();

/*
        Entry prevEntry = history.peek();
        domainSize.get(i).set(j, oldEntry[2]);
*/
    }

    private boolean initDomain() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    ArrayList<Integer> nums = new ArrayList<Integer>();
                    nums.add(getNum(grid[i][j]));
                    setDomain(i, j, nums);
                    DomainSizeEntry e = domainSize.get(i).get(j);
                    e.isEmpty = false;
                    sizeQueue.remove(e);
                    sizeQueue.add(e);

//                    if (pruneDomains(i, j, getNum(grid[i][j])) == null) {
//                        return false;
//                    }
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
        //System.out.println("HMM " + getChar(domain.get(3).get(0).iterator().next()));
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
//                System.out.println(domain.get(i).get(j).size());
//                domainSize.get(i).set(j, size);
            }
        }
    }

    public Refactored(int n) {
        size = n;
        root = (int) Math.sqrt(n);

        sizeComparator = new Comparator<DomainSizeEntry>() {
            public int compare(DomainSizeEntry e, DomainSizeEntry f) {
                if (e.isEmpty && !f.isEmpty) {
                    return -1;
                }
                else if (!e.isEmpty && f.isEmpty) {
                    return 1;
                }
                return e.size - f.size;
//                if (e.size() < f.size()) {
//                    return -1;
//                }
//                else if (e.size() == f.size()) {
//                    return 0;
//                }
//                else {
//                    return 1;
//                }
            }
        };
        sizeQueue = new PriorityQueue<DomainSizeEntry>(size*size, sizeComparator);
        fillDomain();
        calls = new Stack<ArrayList<Integer>>();
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
            //System.out.println("HMM " + getChar(currDomain.get(3).get(0).iterator().next()));

            Integer posint = lst.removeFirst();
            int[] pos = getPosPairFromInt(posint);
            int row = pos[0];
            int col = pos[1];

            //System.out.println("ROWCOL: " + String.valueOf(row) + " " + String.valueOf(col) + " " + posint.toString());

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
                }

                if (getChar(domNum) == '0') {
                    System.out.println("WHOOPS");
                }
                //System.out.println("SIZE " + currDomain.get(row).get(col).size() + " " + getChar(domNum));
            }
            int num = getNum(grid[pos[0]][pos[1]]);

//            System.out.println("CURRENT POS: " + String.valueOf(row) + " " + String.valueOf(col) + " " + currGrid[row][col]);

            for (int k = 0; k < size; k++) {
//                ArrayList<Integer> rowmateDom = currDomain.get(row).get(k);
//                ArrayList<Integer> colmateDom = currDomain.get(k).get(col);

                if (k != col && grid[row][k] == empty) {
                    int oldRowmateSize = domainSize.get(row).get(k).size;
                    //rowmateDom.size();
//                    System.out.println("PRUNING " + getChar(num) + " from " + String.valueOf(row) + " " + String.valueOf(k));
//                    System.out.print("OLD DOMAIN OF " + String.valueOf(row) + String.valueOf(k));
//                    for (int d : rowmateDom) {
//                        System.out.print(" " + getChar(d));
//                    }
//                    System.out.print('\n');
                    removeFromDomain(row, k, num);
//                    rowmateDom.remove(num);
                    int newRowmateSize = domainSize.get(row).get(k).size;
//                    System.out.print("NEW DOMAIN OF " + String.valueOf(row) + String.valueOf(k) +  " " + String.valueOf(oldRowmateSize) + " " + String.valueOf(newRowmateSize));
//                    for (int d : rowmateDom) {
//                        System.out.print(" " + getChar(d));
//                    }
//                    System.out.println('\n');
                    if (newRowmateSize == 1 && oldRowmateSize > 1) {
                        //int newnum = rowmateDom.iterator().next();
                        //currGrid[row][k] = getChar(newnum);
//                        System.out.print("ADDING ROWPOS: " + String.valueOf(row) + " " + String.valueOf(k));
                        lst.add(getIntFromPosPair(row, k));
                        //updateDomains(row, k, newnum, currGrid, currDomain);
                    }
                }

                if (k != row && grid[k][col] == empty) {
                    int oldColmateSize = domainSize.get(k).get(col).size;
//                    System.out.println("PRUNING " + getChar(num) + " from " + String.valueOf(k) + " " + String.valueOf(col));
//                    System.out.print("OLD DOMAIN OF " + String.valueOf(k) + String.valueOf(col));
//                    for (int d : colmateDom) {
//                        System.out.print(" " + getChar(d));
//                    }
//                    System.out.print('\n');
                    removeFromDomain(k, col, num);
                    int newColmateSize = domainSize.get(k).get(col).size;
//                    System.out.print("NEW DOMAIN OF " + String.valueOf(k) + String.valueOf(col) +  " " + String.valueOf(oldColmateSize) + " " + String.valueOf(newColmateSize));
//                    for (int d : colmateDom) {
//                        System.out.print(" " + getChar(d));
//                    }
//                    System.out.print('\n');
                    if (newColmateSize == 1 && oldColmateSize > 1) {
                        //int newnum = colmateDom.iterator().next();
                        //currGrid[k][col] = getChar(newnum);
//                        System.out.println("ADDING COLPOS: " + String.valueOf(k) + " " + String.valueOf(col));
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
                if (grid[i][j] != empty) continue;
                //ArrayList<Integer> boxmateDom = currDomain.get(i).get(j);
                int oldBoxmateSize = domainSize.get(i).get(j).size;
                        //boxmateDom.size();
//                System.out.println("PRUNING " + getChar(num) + " from " + String.valueOf(i) + " " + String.valueOf(j));
//                System.out.print("OLD DOMAIN OF " + String.valueOf(i) + String.valueOf(j));
//                for (int d : boxmateDom) {
//                    System.out.print(" " + getChar(d));
//                }
//                System.out.println('\n');
                removeFromDomain(i, j, num);
//                boxmateDom.remove(num);
                int newBoxmateSize = domainSize.get(i).get(j).size;
                //boxmateDom.size();

//                System.out.print("NEW DOMAIN OF " + String.valueOf(i) + String.valueOf(j) +  " " + String.valueOf(oldBoxmateSize) + " " + String.valueOf(newBoxmateSize));
//                for (int d : boxmateDom) {
//                    System.out.print(" " + getChar(d));
//                }
//                System.out.println('\n');
                if (newBoxmateSize == 1 && oldBoxmateSize > 1) {
                    //int newnum = boxmateDom.iterator().next();
                    //currGrid[i][j] = getChar(newnum);
//                    System.out.println("ADDING BOXPOS: " + String.valueOf(i) + " " + String.valueOf(j));
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
//                if (currGrid[i][j] != empty) {
//                    currDomain.get(i).set(j, new HashSet<Integer>());
//                    currDomain.get(i).get(j).add(getNum(currGrid[i][j]));
//                }
                if (domainSize.get(i).get(j).size == 1) {
                    lst.addLast(getIntFromPosPair(i, j));
                    System.out.println("ADDING " + String.valueOf(i) + " " + String.valueOf(j));
                }
            }
        }
        boolean ret = propagate(lst);
        System.out.println("AFTER AC3");
        show(grid);
        return ret;
    }

//    private boolean mac(int r, int c) {
//        //show(currGrid);
//
//        LinkedList<Integer> lst = new LinkedList<Integer>();
//        lst.addLast(getIntFromPosPair(r, c));
//        //System.out.println("STARTMAC " + String.valueOf(r) + " " + String.valueOf(c) + " " + currGrid[r][c] + " " + currDomain.get(r).get(c).size() + " " + getChar(currDomain.get(r).get(c).iterator().next()));
//        boolean ret = propagate(lst);
//        //show(currGrid);
//        //System.out.println();
//        return ret;
//    }

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
//
//    private char[][] getNewGrid(char[][] currGrid) {
//        char[][] newGrid = new char[size][size];
//        for (int k = 0; k < size; k++) {
//            for (int l = 0; l < size; l++) {
//                newGrid[k][l] = currGrid[k][l];
//            }
//        }
//        return newGrid;
//    }
//
//    private ArrayList<ArrayList<ArrayList<Integer>>> getNewDomain(ArrayList<ArrayList<ArrayList<Integer>>> currDomain) {
//        ArrayList<ArrayList<ArrayList<Integer>>> newDomain = new ArrayList<ArrayList<ArrayList<Integer>>>();
//        for (int k = 0; k < size; k++) {
//            ArrayList<ArrayList<Integer>> kdom = new ArrayList<ArrayList<Integer>>();
//            for (int l = 0; l < size; l++) {
//                kdom.add(new HashSet<Integer>(currDomain.get(k).get(l)));
//            }
//            newDomain.add(kdom);
//        }
//        return newDomain;
//    }

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
//        if (currGrid[pos[0]][pos[1]] != empty) {
//            System.out.println("VBAD " );
//        }
//        return pos;
//    }

//    private int[] getNextPos(int r, int c) {
//        int minDomainSize = size + 1;
//        int[] minpos = new int[2];
//        minpos[0] = -1;
//        minpos[1] = -1;
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if (grid[i][j] != empty) continue;
//                int ijSize = domainSize.get(i).get(j);
//                if (ijSize < minDomainSize) {
//                    minDomainSize = ijSize;
//                    minpos[0] = i;
//                    minpos[1] = j;
//                }
//            }
//        }
//        return minpos;
//    }

    private int[] getNextPos(int r, int c) {
        DomainSizeEntry e = sizeQueue.peek();
        int[] minpos = new int[2];
        minpos[0] = e.r;
        minpos[1] = e.c;
//        System.out.println("NEXTPOS SIZE: " + String.valueOf(minpos[0]) + " " + String.valueOf(minpos[1]) + " " + String.valueOf(e.size) + " " +  String.valueOf(e.isEmpty) + " " + grid[minpos[0]][minpos[1]] + " " + domainSize.get(minpos[0]).get(minpos[1]).size);
        return minpos;
//        int minDomainSize = size + 1;
//        int[] minpos = new int[2];
//        minpos[0] = -1;
//        minpos[1] = -1;
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if (grid[i][j] != empty) continue;
//                int ijSize = domainSize.get(i).get(j);
//                if (ijSize < minDomainSize) {
//                    minDomainSize = ijSize;
//                    minpos[0] = i;
//                    minpos[1] = j;
//                }
//            }
//        }
//        return minpos;
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

    private PruneResult pruneDomains(int r, int c, int num) {
        //show(currGrid);
//        System.out.println("POS: " + String.valueOf(r) + ", " + String.valueOf(c));

//        System.out.println("BOX: " + String.valueOf(box));
        boolean hasZero = false;

        int row = r;
        int col = c;
        int box = getBox(r, c);

        Set<int[]> domainChanges = new HashSet<int[]>();

        for (int j = 0; j < size; j++) {
//            System.out.println("ROWPOS: " + String.valueOf(row) + ", " + String.valueOf(j));
            if (j == c) continue;
            if (grid[r][j] != empty) continue;
            int[] domainChange = removeFromDomain(row, j, num);
            if (domainChange != null) {
//                System.out.println("RES SIZE");
//                System.out.println(domainChange[3]);
                if (domainChange[3] == 0) {
                    hasZero = true;
                }
                domainChanges.add(domainChange);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i == r) continue;
            if (grid[i][c] != empty) continue;
            //removeFromDomain(i, col, num);
            int[] domainChange = removeFromDomain(i, col, num);
            if (domainChange != null) {
//                System.out.println("RES SIZE");
//                System.out.println(domainChange[3]);
                if (domainChange[3] == 0) {
                    hasZero = true;
                }
                domainChanges.add(domainChange);
            }
        }
        for (int[] pos : getBoxPositions(box)) {
//            System.out.println("BOXPOS: " + String.valueOf(pos[0]) + ", " + String.valueOf(pos[1]));
            if (pos[0] == r && pos[1] == c) continue;
            if (grid[pos[0]][pos[1]] != empty) continue;
            //removeFromDomain(pos[0], pos[1], num);
            int[] domainChange = removeFromDomain(pos[0], pos[1], num);
            if (domainChange != null) {
//                System.out.println("RES SIZE");
//                System.out.println(domainChange[3]);
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
//        boolean isFull = true;
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if (currGrid[i][j] == empty) {
//                    isFull = false;
//                }
//            }
//        }
//        return isFull;
    }

    private int getFreq(int num, int row, int col) {
        int rowFreq = 0;
        int colFreq = 0;
        int boxFreq = 0;
        for (int k = 0; k < size; k++) {
            if (isInDomain(row, k, num)) {
                rowFreq++;
            }
            if (isInDomain(k, col, num)) {
                colFreq++;
            }
        }

        // box
        int rb = root * (row/root);
        int cb = root * (col/root);
        for (int k = 0; k < root; k++) {
            for (int l = 0; l < root; l++) {
                if (isInDomain(rb + k, cb + l, num)) {
                    boxFreq++;
                }
            }
        }
        return Math.max(rowFreq, Math.max(colFreq, boxFreq));
    }

    private ArrayList<Integer> getDomainVals(int r, int c) {
        ArrayList<Integer> vals = new ArrayList<Integer>();
        int domSize = domainSize.get(r).get(c).size;
        for (int i = 0; i < domSize; i++) {
            vals.add(domain.get(r).get(c).get(i));
        }
        return vals;
    }

    private ArrayList<Integer> getValOrder(int r, int c, ArrayList<Integer> dom) {
        ArrayList<int[]> freqs = new ArrayList<int[]>();
        int domSize = domainSize.get(r).get(c).size;
        for (int i = 0; i < domSize; i++) {
            int num = dom.get(i);
            int[] pair = new int[2];
            pair[0] = num;
            pair[1] = getFreq(num, r, c);
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

    private char[][] dfs() {
//        if (recdepth > maxRecdepth) {
//            maxRecdepth = recdepth;
//        }

//        if (reccalls > 15) {
//            return null;
//        }
        int depth = 1;
        int currValI = 0;
        //int[] pos = getNextPos(0, -1);
        //int r = pos[0];
        //int c = pos[1];
        int[] pos;
        int r = 0;
        int c = -1;
        ArrayList<Integer> dom;
        ArrayList<Integer> vals;
        Entry prevEntry = null;
        pos = getNextPos(r, c);
        r = pos[0];
        c = pos[1];
        dom = domain.get(r).get(c);
//        vals = dom;
        //vals = new ArrayList<Integer>(dom.subList(0, domainSize.get(r).get(c).size));
        vals = getValOrder(r, c, dom);
        boolean backtracking = false;
        do {
            if (domainSize.get(r).get(c).size == 0) {
                System.out.println("BADBADBAD");
            }
//            System.out.println("VALS SIZE FOR  " + r + " " + c);
//            System.out.println(vals.size());
//            System.out.println(grid[r][c]);
//            System.out.println(currValI);
            reccalls = reccalls.add(BigInteger.valueOf((long)1));
            if (reccalls.mod(BigInteger.valueOf((long)1000000)).equals(BigInteger.valueOf((long)0))) {
                System.out.println("RECCALLS : " + String.valueOf(reccalls));
                System.out.println("DEPTH " + String.valueOf(depth));
                show(grid);
                System.out.println();
            }
            if (currValI == domainSize.get(r).get(c).size) {
//                System.out.println(r);
//                System.out.println(c);
//                System.out.println(reccalls);
//                System.out.println(vals.size());
//                System.out.println(history);
//                System.out.println(depth);
                // backtrack!
                depth--;
                if (depth == 0) {
                    return null;
                }
//                System.out.println("POPPING");
//                if (backtracking) {
////                    System.out.println("BACKPOP");
//                    prevEntry = popGrid();
//                }
                backtracking = true;
                r = prevEntry.r;
                c = prevEntry.c;
                currValI = prevEntry.valI + 1;
                vals = prevEntry.vals;
                prevEntry = popGrid();
                continue;
            }
            backtracking = false;
            int num = vals.get(currValI);
            char cc = getChar(num);
            depth++;
//            if (r == 3 && c == 8) {
//                System.out.println("PUSHING");
//                System.out.println(grid[r][c]);
//            }
            if (!pushGrid(r, c, cc, currValI, vals)) {
                // backtrack!
//                System.out.println("BACK");
                prevEntry = popGrid();
                currValI++;
                depth--;
//                if (r == 3 && c == 8) {
//                    System.out.println("POPPED");
//                    System.out.println(grid[r][c]);
//                }
                continue;
            }
            if (isFull(grid)) {
                return grid;
            }
            // pushing worked, so continue
            pos = getNextPos(r, c);
            r = pos[0];
            c = pos[1];
            dom = domain.get(r).get(c);
            //vals = new ArrayList<Integer>(dom.subList(0, domainSize.get(r).get(c).size));
            //vals = dom;
            vals = getValOrder(r, c, dom);
            currValI = 0;
//            System.out.println("PUSHING WORKED");
//            System.out.println(depth);
//            System.out.println(history);
            //pushing worked, so keep searching

//                if (!mac(r, c, newGrid, newDomain)) {
//                    continue;
//                }

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
            System.out.println(hist.r + " " + hist.c + " " + getChar(hist.vals.get(hist.valI)));
        }

        char[][] resGrid = dfs();
        return resGrid;
    }
}