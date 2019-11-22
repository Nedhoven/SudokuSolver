
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

public class Optimized implements SolverInterface {
    
    private char[][] grid;
    private final Integer size;
    private final Integer root;
    private final char empty = '0';
    private final Set<Integer> arr;
    private final Map<Integer, Set<Integer>> row;
    private final Map<Integer, Set<Integer>> col;
    private final Map<Integer, Set<Integer>> box;
    private final Map<Integer, Set<Integer>> emptyRow;
    private final Map<Integer, Set<Integer>> emptyCol;

//    private final Map<Integer, Map<Integer, Integer>> rowFreq;
//    private final Map<Integer, Map<Integer, Integer>> colFreq;
//    private final Map<Integer, Map<Integer, Integer>> boxFreq;

    private ArrayList<ArrayList<Set<Integer>>> domain;

    private Integer reccalls = 0;
    private Integer maxRecdepth = 0;
    private int lastRecdepth = 0;

    private void fillArray() {
        for (int x = 0; x < size; x++) {
            arr.add(x);
        }
    }
    
    private void allocate(Map<Integer, Set<Integer>> map) {
        for (int index = 0; index < size; index++) {
            Set<Integer> set = new HashSet<Integer>(arr);
            map.put(index, set);
        }
    }
    
    private void create(Map<Integer, Set<Integer>> map) {
        for (int index = 0; index < size; index++) {
            map.put(index, new HashSet<Integer>());
        }
    }

    private Set<Integer> getDomain(int r, int c, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        Set<Integer> dom = new HashSet<Integer>();
        Set<Integer> rowSet = row.get(r);
        Set<Integer> colSet = col.get(c);
        int b = root * (r / root) + (c / root);

        Set<Integer> boxSet = box.get(b);

        for (int i = 0; i < size; i++) {
            if (rowSet.contains(i) && colSet.contains(i) && boxSet.contains(i) && currDomain.get(r).get(c).contains(i)) {
                dom.add(i);
            }
        }

        return dom;
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

//    public void pathcons() {
//        LinkedList<ArrayList<Integer>> lst = new LinkedList<Integer>();
//
//        for (int k = 0; k < size*size; k++) {
//            for (int l = k + 1; l < size*size; l++) {
//                int i1 = k/size;
//                int j1 = k%size;
//                int i2 = l/size;
//                int j2 = l%size;
//
//                int b1 = root*(i1/root) + (j1/root);
//                int b2 = root*(i2/root) + (j2/root);
//
//                if (i1 == j1 || i2 == j2 || b1 == b2) {
//                    // arc consistency covers these
//                    continue;
//                }
//
//                if (grid[i1][j1] != empty && grid[i2][j2] != empty) {
//                    ArrayList<Integer> ar = new ArrayList<Integer>():
//                    ar.add(k);
//                    ar.add(l);
//                    lst.addLast(ar);
//                }
//            }
//        }
//        while (lst.size() > 0) {
//            ArrayList<Integer> ar = lst.removeFirst();
//            int k = ar.get(0);
//            int l = ar.get(1);
//            int i1 = k/size;
//            int j1 = k%size;
//            int i2 = l/size;
//            int j2 = l%size;
//            for (int p = 0; p < size*size; p++) {
//                if (p == k || p == l) continue;
//                int r = p/size;
//                int s = p%size;
//                int b = root*(r/root) + (s/root);
//                if (grid[r][s] != empty) {
//                    continue;
//                }
//
//                // in row i1, col j2
//                if (r == i1 && s == j2) {
//                    domain.get(r).get(s).remove()
//                }
//                else if (r == i1 && b == b2) {
//
//                }
//                else if (s == j1 && r == i2) {
//
//                }
//                else if (s == j1 && b == b2) {
//
//                }
//                else if (b == b1 && r == i2) {
//
//                }
//                else if (b == b1 && s == j2 ) {
//
//                }
//
//            }
//        }
//
//    }

    public boolean propagate(LinkedList<Integer> lst, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox){
        while (lst.size() > 0) {
            Integer posint = lst.removeFirst();
            int[] pos = new int[2];
            pos[0] = posint / size;
            pos[1] = posint % size;
            int num = getNum(currGrid[pos[0]][pos[1]]);

            // update cell domains
            // row/col
            for (int k = 0; k < size; k++) {
                //int oldRowmateSize = currDomain.get(pos[0]).get(k).size();
                //int oldColmateSize = currDomain.get(k).get(pos[1]).size();
                int oldRowmateSize = getDomain(pos[0], k, currDomain, currRow, currCol, currBox).size();
                int oldColmateSize = getDomain(k, pos[1], currDomain, currRow, currCol, currBox).size();
                //System.out.println("REmoving: " + String.valueOf(pos[0]) + " " + String.valueOf(pos[1]) + " " + String.valueOf(k) + " " + getChar(num));
                currDomain.get(pos[0]).get(k).remove(num);
                currDomain.get(k).get(pos[1]).remove(num);

                if (k != pos[1] && currGrid[pos[0]][k] == empty && getDomain(pos[0], k, currDomain, currRow, currCol, currBox).size() == 1 && oldRowmateSize > 1) {
//                    if (k != pos[1] && currGrid[pos[0]][k] == empty && currDomain.get(pos[0]).get(k).size() == 1 && oldRowmateSize > 1) {
//                    int newnum = currDomain.get(pos[0]).get(k).iterator().next();
                    int newnum = currDomain.get(pos[0]).get(k).iterator().next();
                    currGrid[pos[0]][k] = getChar(newnum);
                    if (!currRow.get(pos[0]).contains(newnum) || !currCol.get(k).contains(newnum) || !currBox.get(root*(pos[0]/root) + (k/root)).contains(newnum)) {
                        System.out.println("set row " + String.valueOf(pos[0])  + " " + String.valueOf(k) + " " + getChar(num) + " " + getChar(newnum));
                        show(currGrid);
                        System.out.println("ROW PROBLEM!");
                        return false;
                    }
                    add(pos[0], k, newnum, true, currRow, currCol, currBox);
                    lst.addLast(pos[0]*size+k);
                }
//                if (k != pos[0] && currGrid[k][pos[1]] == empty && currDomain.get(k).get(pos[1]).size() == 1 && oldColmateSize > 1) {
                if (k != pos[0] && currGrid[k][pos[1]] == empty && getDomain(k, pos[1], currDomain, currRow, currCol, currBox).size() == 1 && oldColmateSize > 1) {
                    //int newnum = currDomain.get(k).get(pos[1]).iterator().next();
                    int newnum = getDomain(k, pos[1], currDomain, currRow, currCol, currBox).iterator().next();
                    currGrid[k][pos[1]] = getChar(newnum);
                    if (!currRow.get(k).contains(newnum) || !currCol.get(pos[1]).contains(newnum) || !currBox.get(root*(k/root) + (pos[1]/root)).contains(newnum)) {
                        System.out.println("set col " + String.valueOf(k)  + " " + String.valueOf(pos[1]) + " " + getChar(num) + " " + getChar(newnum));
                        show(currGrid);
                        System.out.println("COL PROBLEM!");
                        return false;
                    }
                    add(k, pos[1], newnum, true, currRow, currCol, currBox);
                    lst.addLast(k*size+pos[1]);
                }
            }

            // box
            int rb = root * (pos[0]/root);
            int cb = root * (pos[1]/root);
            for (int k = 0; k < root; k++) {
                for (int l = 0; l < root; l++) {
                    if (rb + k == pos[0] && cb + l == pos[1]) continue;
                    //int oldBoxmateSize = currDomain.get(rb + k).get(cb + l).size();
                    int oldBoxmateSize = getDomain(rb + k, cb + l, currDomain, currRow, currCol, currBox).size();
                    //System.out.println("REmoving: " + String.valueOf(rb + k) + " " + String.valueOf(cb + l) + " " + getChar(num));
                    currDomain.get(rb + k).get(cb + l).remove(num);
//                    if (currGrid[rb + k][cb + l] == empty && currDomain.get(rb + k).get(cb + l).size() == 1 && oldBoxmateSize > 1) {
                    if (currGrid[rb + k][cb + l] == empty && getDomain(rb + k, cb + l, currDomain, currRow, currCol, currBox).size() == 1 && oldBoxmateSize > 1) {
                        //int newnum = currDomain.get(rb + k).get(cb + l).iterator().next();
                        int newnum = getDomain(rb + k, cb + l, currDomain, currRow, currCol, currBox).iterator().next();
                        currGrid[rb + k][cb + l] = getChar(newnum);
                        if (!currRow.get(rb+k).contains(newnum) || !currCol.get(cb+l).contains(newnum) || !currBox.get(root*((rb+k)/root) + ((cb+l)/root)).contains(newnum)) {
                            System.out.println("set box " + String.valueOf(rb+k)  + " " + String.valueOf(cb+l) + " " + getChar(num) + " " + getChar(newnum) + " " + String.valueOf(pos[0]) + " " + String.valueOf(pos[1]));
                            show(currGrid);
                            System.out.println("BOX PROBLEM!");
                            return false;
                        }
                        add(rb+k, cb+l, newnum, true, currRow, currCol, currBox);
                        lst.addLast((rb+k)*size+(cb+l));
                    }
                }
            }

        }
        return true;
    }

    public boolean mac(int r, int c, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        LinkedList<Integer> lst = new LinkedList<Integer>();
        lst.addLast(r*size+c);
        return propagate(lst, currGrid, currDomain, currRow, currCol, currBox);
    }

    public boolean ac3(char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        LinkedList<Integer> lst = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (currGrid[i][j] != empty) {
                    lst.addLast(i*size+j);
                }
            }
        }
        boolean ret = propagate(lst, currGrid, currDomain, currRow, currCol, currBox);
        System.out.println("AFTER AC3");
        show(currGrid);
        return ret;
    }

    public Optimized(int n) {
        size = n;
        root = (int) Math.sqrt(n);
        arr = new HashSet<Integer>();
        fillArray();
        row = new HashMap<Integer, Set<Integer>>();
        allocate(row);
        col = new HashMap<Integer, Set<Integer>>();
        allocate(col);
        box = new HashMap<Integer, Set<Integer>>();
        allocate(box);
        emptyRow = new HashMap<Integer, Set<Integer>>();
        create(emptyRow);
        emptyCol = new HashMap<Integer, Set<Integer>>();
        create(emptyCol);

        fillDomain();
    }
    public static void show(char[][] board) {
        for (char[] arr : board) {
            System.out.println(Arrays.toString(arr));
        }
    }

//    private void printBoard() {
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                System.out.print(grid[i][j]);
//            }
//            System.out.print('\n');
//        }
//        System.out.println();
//    }
//
    private boolean init() {
        boolean ans = true;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == empty) {
                    Set<Integer> set = emptyRow.get(i);
                    set.add(j);
                    emptyRow.put(i, set);
                    set = emptyCol.get(j);
                    set.add(i);
                    emptyCol.put(j, set);
                }
                else {
                    // update row/col/box domains
                    int b = root * (i / root) + (j / root);
                    int num = getNum(grid[i][j]);
                    Set<Integer> set = row.get(i);
                    if (!set.remove(num)) {
                        ans = false;
                    }
                    row.put(i, set);
                    set = col.get(j);
                    if (!set.remove(num)) {
                        ans = false;
                    }
                    col.put(j, set);
                    set = box.get(b);
                    if (!set.remove(num)) {
                        ans = false;
                    }
                    box.put(b, set);

//                    // update cell domains
//                    // row/col
//                    for (int k = 0; k < size; k++) {
//                        domain.get(i).get(k).remove(num);
//                        domain.get(k).get(j).remove(num);
//                    }
//
//                    // box
//                    int rb = root * (i/root);
//                    int cb = root * (j/root);
//                    for (int k = 0; k < root; k++) {
//                        for (int l = 0; l < root; l++) {
//                            domain.get(rb + k).get(cb + l).remove(num);
//                        }
//                    }

                }
            }
        }
        if (!ac3(grid, domain, row, col, box)) {
            return false;
        }

        return ans;
    }
    
    private int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }
    
    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }

    private int[] getIndex(char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        int argminI = -1;
        int argminJ = -1;
        int minDomainSize = size + 1;
        int[] res = new int[2];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (currGrid[i][j] != empty) continue;
                //int domainSize = domain.get(i).get(j).size();
                int domainSize = getDomain(i, j, currDomain, currRow, currCol, currBox).size();
                if (domainSize < size) {
                    argminI = i;
                    argminJ = j;
                    minDomainSize = domainSize;
                }
            }
        }
        res[0] = argminI;
        res[1] = argminJ;
        return res;
    }
//    private int[] getIndex(char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
//        int[] res = new int[2];
//        int minRow = size + 1;
//        int minCol = size + 1;
//        int r = -1;
//        int c = -1;
//        for (int i = 0; i < size; i++) {
//            if (!currRow.get(i).isEmpty() && currRow.get(i).size() < minRow) {
//                minRow = currRow.get(i).size();
//                r = i;
//            }
//            if (!currCol.get(i).isEmpty() && currCol.get(i).size() < minCol) {
//                minCol = currCol.get(i).size();
//                c = i;
//            }
//        }
//        //System.out.println("found: " + r + " with " + minRow + " and " + c + " with " + minCol);
//        if (r == -1 && c == -1) {
//            //System.out.println("done looking!");
//            //System.out.println("row: " + row);
//            //System.out.println("col: " + col);
//            res[0] = r;
//            res[1] = c;
//            return res;
//        }
//        if (minRow < minCol) {
//            res[0] = r;
//            //System.out.println("set x as " + r);
//            minCol = size + 1;
//            c = -1;
//            //System.out.println("empty cells: " + emptyRow.get(r));
//            for (int j : emptyRow.get(r)) {
//                if (!currCol.get(j).isEmpty() && currCol.get(j).size() < minCol) {
//                    minCol = currCol.get(j).size();
//                    c = j;
//                }
//            }
//            res[1] = c;
//        }
//        else {
//            res[1] = c;
//            //System.out.println("set y as " + c);
//            minRow = size + 1;
//            r = -1;
//            //System.out.println("empty cells: " + emptyCol.get(c));
//            for (int i : emptyCol.get(c)) {
//                if (!currRow.get(i).isEmpty() && currRow.get(i).size() < minRow) {
//                    minRow = currRow.get(i).size();
//                    r = i;
//                }
//            }
//            res[0] = r;
//        }
//        return res;
//    }
    
    private void add(int r, int c, int num, boolean flag, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        int b = root * (r / root) + (c / root);
        if (flag) {
            Set<Integer> set = currRow.get(r);
            set.remove(num);
            //System.out.println("new set at row " + r + ": " + set);
            currRow.put(r, set);
            set = emptyRow.get(r);
            set.remove(c);
            //System.out.println("new empty set at row " + r + ": " + set);
            emptyRow.put(r, set);
            set = currCol.get(c);
            set.remove(num);
            //System.out.println("new set at col " + c + ": " + set);
            currCol.put(c, set);
            set = emptyCol.get(c);
            set.remove(r);
            //System.out.println("new empty set at col " + c + ": " + set);
            emptyCol.put(c, set);
            set = currBox.get(b);
            set.remove(num);
            //System.out.println("new set at box " + b + ": " + set);
            currBox.put(b, set);
        }
        else {
            Set<Integer> set = currRow.get(r);
            set.add(num);
            //System.out.println("new set at row " + r + ": " + set);
            currRow.put(r, set);
            set = emptyRow.get(r);
            set.add(c);
            //System.out.println("new empty set at row " + r + ": " + set);
            emptyRow.put(r, set);
            set = currCol.get(c);
            set.add(num);
            //System.out.println("new set at col " + c + ": " + set);
            currCol.put(c, set);
            set = emptyCol.get(c);
            set.add(r);
            //System.out.println("new empty set at col " + c + ": " + set);
            emptyCol.put(c, set);
            set = currBox.get(b);
            set.add(num);
            //System.out.println("new set at box " + b + ": " + set);
            currBox.put(b, set);
        }
    }
    
    private Set<Integer> union(int r, int c, ArrayList<ArrayList<Set<Integer>>> currDomain,  Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        int b = root * (r / root) + (c / root);
        Set<Integer> res = new HashSet<Integer>();
        //for (int num : row.get(r)) {
        //    if (col.get(c).contains(num) && box.get(b).contains(num)) {
        //        res.add(num);
        //    }
        //}
        if (currRow.get(r).size() < currCol.get(c).size()) {
            if (currRow.get(r).size() < currBox.get(b).size()) {
                for (int num : currRow.get(r)) {
                    if (currCol.get(c).contains(num) && currBox.get(b).contains(num)) {
                        res.add(num);
                    }
                }
            }
            else {
                for (int num : currBox.get(b)) {
                    if (currRow.get(r).contains(num) && currCol.get(c).contains(num)) {
                        res.add(num);
                    }
                }
            }
        }
        else {
            if (currCol.get(c).size() < currBox.get(b).size()) {
                for (int num : currCol.get(c)) {
                    if (currRow.get(r).contains(num) && currBox.get(b).contains(num)) {
                        res.add(num);
                    }
                }
            }
            else {
                for (int num : currBox.get(b)) {
                    if (currRow.get(r).contains(num) && currCol.get(c).contains(num)) {
                        res.add(num);
                    }
                }
            }
        }
        return res;
    }

    private int getFreq(int num, int row, int col, ArrayList<ArrayList<Set<Integer>>> currDomain,  Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        int rowFreq = 0;
        int colFreq = 0;
        int boxFreq = 0;
        for (int k = 0; k < size; k++) {
            if (getDomain(row, k, currDomain, currRow, currCol, currBox).contains(num)) {
                rowFreq++;
            }
            if (getDomain(k, col, currDomain, currRow, currCol, currBox).contains(num)) {
                colFreq++;
            }
//
//            if (domain.get(row).get(k).contains(num)) {
//                rowFreq++;
//            }
//            if (domain.get(k).get(col).contains(num)) {
//                colFreq++;
//            }
        }

        // box
        int rb = root * (row/root);
        int cb = root * (col/root);
        for (int k = 0; k < root; k++) {
            for (int l = 0; l < root; l++) {
//                if (domain.get(rb + k).get(cb + l).contains(num)) {
//                    boxFreq++;
//                }
                if (getDomain(rb + k, cb + l, currDomain, currRow, currCol, currBox).contains(num)) {
                    boxFreq++;
                }
            }
        }
        return Math.max(rowFreq, Math.max(colFreq, boxFreq));
    }

    private int[] getForcedVal(char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain, Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        int[] res = new int[3];
        res[0] = -1;
        res[1] = -1;
        res[2] = -1;
        for (int i = 0; i < size; i++) {
            Set<Integer> rowSet = row.get(i);
            for (int num = 0; num < size; num++) {
                if (rowSet.contains(num)) {
                    continue;
                }
                Set<Integer> places = new HashSet<Integer>();
                for (int j = 0; j < size; j++) {
                    if (currGrid[i][j] != empty) {
                        continue;
                    }
                    Set<Integer> dom = getDomain(i, j, currDomain, currRow, currCol, currBox);
                    if (dom.contains(num)) {
                        places.add(j);
                    }
                }
                if (places.size() == 1) {
                    //System.out.println("FOUND");
                    char cc = getChar(num);
                    int j = places.iterator().next();
                    res[0] = i;
                    res[1] = j;
                    res[2] = num;
                    //System.out.println(String.valueOf(getDomain(i, j).contains(num)));
                    return res;
                }
            }
        }

        for (int j = 0; j < size; j++) {
            Set<Integer> colSet = col.get(j);
            for (int num = 0; num < size; num++) {
                if (colSet.contains(num)) {
                    continue;
                }
                Set<Integer> places = new HashSet<Integer>();
                for (int i = 0; i < size; i++) {
                    if (currGrid[i][j] != empty) {
                        continue;
                    }
                    Set<Integer> dom = getDomain(i, j, currDomain, currRow, currCol, currBox);
                    if (dom.contains(num)) {
                        places.add(i);
                    }
                }
                if (places.size() == 1) {
                    char cc = getChar(num);
                    int r = places.iterator().next();
                    res[0] = r;
                    res[1] = j;
                    res[2] = num;
                    return res;
                    //grid[i][c] = cc;
                    //add(i, c, num, true);
                }
            }
        }

        for (int b = 0; b < size; b++) {
            Set<Integer> boxSet = box.get(b);
            for (int num = 0; num < size; num++) {
                if (boxSet.contains(num)) {
                    continue;
                }
                Set<Integer> places = new HashSet<Integer>();
                int rb = b / root;
                int cb = b % root;
                for (int k = 0; k < root; k++) {
                    for (int l = 0; l < root; l++) {
                        if (currGrid[root * rb + k][root * cb + l] != empty) {
                            continue;
                        }
                        Set<Integer> dom = getDomain(root*rb + k, root*cb + l, currDomain, currRow, currCol, currBox);
                        if (dom.contains(num)) {
                            places.add(k*root+l);
                        }
                    }
                }
                if (places.size() == 1) {
                    char cc = getChar(num);
                    int bi = places.iterator().next();
                    int rk = bi / root;
                    int cl = bi % root;
                    res[0] = root * rb + rk;
                    res[1] = root * cb + cl;
                    res[2] = num;
                    return res;
                }
            }
        }


        return res;
//        for (int j = 0; j < size; j++) {
//            Set<Integer> colSet = col.get(j);
//            for (int num = 0; num < size; num++) {
//                if (colSet.contains(num)) {
//                    continue;
//                }
//                Set<Integer> places = new HashSet<Integer>();
//                for (int i = 0; i < size; i++) {
//                    if (grid[i][j] == empty) {
//                        continue;
//                    }
//                    Set<Integer> dom = getDomain(i, j);
//                    if (dom.contains(num)) {
//                        places.add(j);
//                    }
//                }
//                if (places.size() == 1) {
//                    char cc = getChar(num);
//                    int r = places.iterator().next();
//                    grid[r][j] = cc;
//                    add(r, j, num, true);
//                }
//            }
//        }
    }

    private char[][] dfs(int r, int c, int recdepth, int forcedNum, char[][] currGrid, ArrayList<ArrayList<Set<Integer>>> currDomain,  Map<Integer, Set<Integer>> currRow, Map<Integer, Set<Integer>> currCol, Map<Integer, Set<Integer>> currBox) {
        //System.out.println("TRYING: " + String.valueOf(r) + ", " + String.valueOf(c));
        //System.out.println(String.valueOf(r) + " " + String.valueOf(c));
        //show(currGrid);
        //System.out.println();
        if (recdepth > maxRecdepth) {
            maxRecdepth = recdepth;
        }
        reccalls+=1;
//        if (reccalls > 10000000)
//            return null;
        if (r == -1 || c == -1) {
            //System.out.println("done in dfs!");
            return currGrid;
        }
        int b = root * (r / root) + (c / root);
        ArrayList<int[]> freqs = new ArrayList<int[]>();
        if (forcedNum != -1) {
            int[] pair = new int[2];
            pair[0] = forcedNum;
            pair[1] = -1;
            freqs.add(pair);
        }
        else {
            // System.out.println("at (" + r + "," + c + ") - box #" + b);
            Set<Integer> set = union(r, c, currDomain, currRow, currCol, currBox);
            /*System.out.println("available numbers: " + set);
            if (row.get(r).size() != emptyRow.get(r).size()) {
                System.out.println("-------------------------");
                System.out.println("Problem!");
                System.out.println("-------------------------");
            }
            if (col.get(c).size() != emptyCol.get(c).size()) {
                System.out.println("-------------------------");
                System.out.println("Problem!");
                System.out.println("-------------------------");
            }*/

            for (int num : set) {
                int[] pair = new int[2];
                pair[0] = num;
                pair[1] = getFreq(num, r, c, currDomain, currRow, currCol, currBox);
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
        }
//        if (freqs.size() == 0 || freqs.get(0)[1] >= 13) {
//            return false;
//        }
        //System.out.println(String.valueOf(freqs.size()));
        //show(currGrid);
//        System.out.println();
//        show(newGrid);
//        System.out.println("****");

//        if (freqs.size() == 0) {
//            System.out.println("SOL: " + String.valueOf(r) + " " + String.valueOf(c));
//            show(currGrid);
//        }
        for (int i = 0; i < freqs.size(); i++) {
            int num = freqs.get(i)[0];
            char cc = getChar(num);
            //System.out.println("assign " + cc);
//            char[][] newGrid = currGrid;
//            newGrid[r][c] = cc;
//            ArrayList<ArrayList<Set<Integer>>> newDomain = currDomain;
//            Map<Integer, Set<Integer>> newRow = currRow;
//            Map<Integer, Set<Integer>> newCol = currCol;
//            Map<Integer, Set<Integer>> newBox = currBox;
            char[][] newGrid = new char[size][size];
            for (int k = 0; k < size; k++) {
                for (int l = 0; l < size; l++) {
                    newGrid[k][l] = currGrid[k][l];
                }
            }
            newGrid[r][c] = cc;
            ArrayList<ArrayList<Set<Integer>>> newDomain = new ArrayList<ArrayList<Set<Integer>>>();
            for (int k = 0; k < size; k++) {
                ArrayList<Set<Integer>> kdom = new ArrayList<Set<Integer>>();
                for (int l = 0; l < size; l++) {
                    kdom.add(new HashSet<Integer>(currDomain.get(k).get(l)));
                }
                newDomain.add(kdom);
            }

            Map<Integer, Set<Integer>> newRow = new HashMap<Integer, Set<Integer>>();
            Map<Integer, Set<Integer>> newCol = new HashMap<Integer, Set<Integer>>();
            Map<Integer, Set<Integer>> newBox = new HashMap<Integer, Set<Integer>>();
            for (int k = 0; k < size; k++) {
                newRow.put(k, new HashSet<Integer>(currRow.get(k)));
                newCol.put(k, new HashSet<Integer>(currCol.get(k)));
                newBox.put(k, new HashSet<Integer>(currBox.get(k)));
            }

//            if (!ac3(newGrid, newDomain, newRow, newCol, newBox)) {
//                return null;
//            }


            if (forcedNum != -1) {
                System.out.println("ADDING " + String.valueOf(r) + " " + String.valueOf(c) + " " + String.valueOf(num));
            }
            add(r, c, num, true, newRow, newCol, newBox);
            if (!mac(r, c, newGrid, newDomain, newRow, newCol, newBox)) {
                return null;
            }
            if (recdepth % 40 == 0 && recdepth != lastRecdepth) {
                lastRecdepth = recdepth;
                System.out.println("GOING IN: " + Integer.valueOf(recdepth).toString());
                //show(grid);
            }
            if (reccalls % 100000 == 0) {
                System.out.println("RECCALLS " + String.valueOf(reccalls));
                System.out.println("DEPTH " + String.valueOf(recdepth));
//                int numFilled = 0;
//                for (int p = 0; p < size; p++) {
//                    for (int q = 0; q < size; q++) {
//                        if (currGrid[p][q] != empty) {
//                            numFilled++;
//                        }
//                    }
//                }
//                System.out.println("NUMFILLED " + String.valueOf(numFilled));
                show(currGrid);
//                //show(grid);
            }

            //int[] forced = getForcedVal(newGrid, newDomain, newRow, newCol, newBox);
            int[] forced = new int[3];
            forced[0] = -1;
            forced[1] = -1;
            forced[2] = -1;
            if (forced[0] != -1) {
                System.out.println("FORCING: " + Arrays.toString(forced));
//                if (recdepth > 100) {
//                    return false;
//                }
                char[][] resGrid = dfs(forced[0], forced[1], recdepth + 1, forced[2], newGrid, newDomain, newRow, newCol, newBox);
                if (resGrid != null) {
                    return resGrid;
                }
            }
            else {
                //int[] pos = getIndex(newGrid, newDomain, newRow, newCol, newBox);
                //char[][] resGrid = dfs(pos[0], pos[1], recdepth + 1, -1, newGrid, newDomain, newRow, newCol, newBox);
                int nextR = r;
                int nextC = c;
                do {
                    nextC++;

                    if (nextC == size) {
                        nextC = 0;
                        nextR++;
                    }
                    if (nextR == size) {
                        return null;
                    }
                } while (currGrid[nextR][nextC] != empty);
//                    System.out.println(String.valueOf(r) + " " + String.valueOf(c) + " " + String.valueOf(nextR) + " " + String.valueOf(nextC));
//                try{
//                    Thread.sleep(500);
//                }
//                catch(Exception e){
//
//                }
                char[][] resGrid = dfs(nextR, nextC, recdepth + 1, -1, newGrid, newDomain, newRow, newCol, newBox);
                if (resGrid != null) {
                    return resGrid;
                }
            }
            //System.out.println("change for (" + r + "," + c + ")");
            add(r, c, num, false, newRow, newCol, newBox);
            newGrid[r][c] = empty;
            /*if (row.get(r).size() != emptyRow.get(r).size()) {
                System.out.println("-------------------------");
                System.out.println("after assigning");
                System.out.println("Problem!");
                System.out.println("-------------------------");
            }
            if (col.get(c).size() != emptyCol.get(c).size()) {
                System.out.println("-------------------------");
                System.out.println("after assigning");
                System.out.println("Problem!");
                System.out.println("-------------------------");
            }*/
        }
        if (recdepth % 40 == 0) {
            //lastRecdepth = recdepth;
            //System.out.println("GOING OUT: " + Integer.valueOf(recdepth).toString());
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

        //System.out.println("row: " + row);
        //System.out.println("col: " + col);
        //System.out.println("box: " + box);
        //System.out.println("empty cells at each row: " + emptyRow);
        //System.out.println("empty cells at each col: " + emptyCol);
        //int[] pos = getIndex(grid, domain, row, col, box);
        //char[][] resGrid = dfs(pos[0], pos[1], 0, -1, grid, domain, row, col, box);
        char[][] resGrid = dfs(0, 0, 0, -1, grid, domain, row, col, box);
        System.out.println("recursive calls: " + reccalls.toString());
        System.out.println("recursive depth: " + maxRecdepth.toString());

        return resGrid;
    }
    
}
