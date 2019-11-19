
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

    private Set<Integer> getDomain(int r, int c) {
        Set<Integer> dom = new HashSet<Integer>();
        Set<Integer> rowSet = row.get(r);
        Set<Integer> colSet = col.get(c);
        int b = root * (r / root) + (c / root);

        Set<Integer> boxSet = box.get(b);

        for (int i = 0; i < size; i++) {
            if (rowSet.contains(i) && colSet.contains(i) && boxSet.contains(i) && domain.get(r).get(c).contains(i)) {
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

//    public void ac3() {
//        LinkedList<Integer> lst = new LinkedList<Integer>();
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if (grid[i][j] != empty) {
//                    lst.addLast(i*size+j);
//                }
//            }
//        }
//
//        while (lst.size() > 0) {
//            Integer posint = lst.removeFirst();
//            int[] pos = new int[2];
//            pos[0] = posint / size;
//            pos[1] = posint % size;
//            int num = getNum(grid[pos[0]][pos[1]]);
//
//            // update cell domains
//            // row/col
//            for (int k = 0; k < size; k++) {
//                domain.get(pos[0]).get(k).remove(num);
//                domain.get(k).get(pos[1]).remove(num);
//
//                if (domain.get(pos[0]).get(k).size() == 1) {
//                    lst.addLast(pos[0]*size+k);
//                }
//                if (domain.get(k).get(pos[1]).size() == 1) {
//                    lst.addLast(k*size+pos[1]);
//                }
//            }
//
//            // box
//            int rb = root * (pos[0]/root);
//            int cb = root * (pos[1]/root);
//            for (int k = 0; k < root; k++) {
//                for (int l = 0; l < root; l++) {
//                    domain.get(rb + k).get(cb + l).remove(num);
//                    if (domain.get(rb + k).get(cb + l).size() == 1) {
//                        lst.addLast((rb+k)*size+(cb+l));
//                    }
//                }
//            }
//
//        }
//    }
//
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
        //ac3();

        return ans;
    }
    
    private int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }
    
    private char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }

    private int[] getIndex() {
        int argminI = -1;
        int argminJ = -1;
        int minDomainSize = size + 1;
        int[] res = new int[2];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) continue;
                //int domainSize = domain.get(i).get(j).size();
                int domainSize = getDomain(i, j).size();
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
//    private int[] getIndex() {
//        int[] res = new int[2];
//        int minRow = size + 1;
//        int minCol = size + 1;
//        int r = -1;
//        int c = -1;
//        for (int i = 0; i < size; i++) {
//            if (!row.get(i).isEmpty() && row.get(i).size() < minRow) {
//                minRow = row.get(i).size();
//                r = i;
//            }
//            if (!col.get(i).isEmpty() && col.get(i).size() < minCol) {
//                minCol = col.get(i).size();
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
//                if (!col.get(j).isEmpty() && col.get(j).size() < minCol) {
//                    minCol = col.get(j).size();
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
//                if (!row.get(i).isEmpty() && row.get(i).size() < minRow) {
//                    minRow = row.get(i).size();
//                    r = i;
//                }
//            }
//            res[0] = r;
//        }
//        return res;
//    }
    
    private void add(int r, int c, int num, boolean flag) {
        int b = root * (r / root) + (c / root);
        if (flag) {
            Set<Integer> set = row.get(r);
            set.remove(num);
            //System.out.println("new set at row " + r + ": " + set);
            row.put(r, set);
            set = emptyRow.get(r);
            set.remove(c);
            //System.out.println("new empty set at row " + r + ": " + set);
            emptyRow.put(r, set);
            set = col.get(c);
            set.remove(num);
            //System.out.println("new set at col " + c + ": " + set);
            col.put(c, set);
            set = emptyCol.get(c);
            set.remove(r);
            //System.out.println("new empty set at col " + c + ": " + set);
            emptyCol.put(c, set);
            set = box.get(b);
            set.remove(num);
            //System.out.println("new set at box " + b + ": " + set);
            box.put(b, set);
        }
        else {
            Set<Integer> set = row.get(r);
            set.add(num);
            //System.out.println("new set at row " + r + ": " + set);
            row.put(r, set);
            set = emptyRow.get(r);
            set.add(c);
            //System.out.println("new empty set at row " + r + ": " + set);
            emptyRow.put(r, set);
            set = col.get(c);
            set.add(num);
            //System.out.println("new set at col " + c + ": " + set);
            col.put(c, set);
            set = emptyCol.get(c);
            set.add(r);
            //System.out.println("new empty set at col " + c + ": " + set);
            emptyCol.put(c, set);
            set = box.get(b);
            set.add(num);
            //System.out.println("new set at box " + b + ": " + set);
            box.put(b, set);
        }
    }
    
    private Set<Integer> union(int r, int c) {
        int b = root * (r / root) + (c / root);
        Set<Integer> res = new HashSet<Integer>();
        //for (int num : row.get(r)) {
        //    if (col.get(c).contains(num) && box.get(b).contains(num)) {
        //        res.add(num);
        //    }
        //}
        if (row.get(r).size() < col.get(c).size()) {
            if (row.get(r).size() < box.get(b).size()) {
                for (int num : row.get(r)) {
                    if (col.get(c).contains(num) && box.get(b).contains(num)) {
                        res.add(num);
                    }
                }
            }
            else {
                for (int num : box.get(b)) {
                    if (row.get(r).contains(num) && col.get(c).contains(num)) {
                        res.add(num);
                    }
                }
            }
        }
        else {
            if (col.get(c).size() < box.get(b).size()) {
                for (int num : col.get(c)) {
                    if (row.get(r).contains(num) && box.get(b).contains(num)) {
                        res.add(num);
                    }
                }
            }
            else {
                for (int num : box.get(b)) {
                    if (row.get(r).contains(num) && col.get(c).contains(num)) {
                        res.add(num);
                    }
                }
            }
        }
        return res;
    }

    private int getFreq(int num, int row, int col) {
        int rowFreq = 0;
        int colFreq = 0;
        int boxFreq = 0;
        for (int k = 0; k < size; k++) {
            if (getDomain(row, k).contains(num)) {
                rowFreq++;
            }
            if (getDomain(k, col).contains(num)) {
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
                if (getDomain(rb + k, cb + l).contains(num)) {
                    boxFreq++;
                }
            }
        }
        return Math.max(rowFreq, Math.max(colFreq, boxFreq));
    }

    private int[] getForcedVal() {
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
                    if (grid[i][j] != empty) {
                        continue;
                    }
                    Set<Integer> dom = getDomain(i, j);
                    if (dom.contains(num)) {
                        places.add(j);
                    }
                }
                if (places.size() == 1) {
                    System.out.println("FOUND");
                    char cc = getChar(num);
                    int j = places.iterator().next();
                    res[0] = i;
                    res[1] = j;
                    res[2] = num;
                    System.out.println(String.valueOf(getDomain(i, j).contains(num)));
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
                    if (grid[i][j] != empty) {
                        continue;
                    }
                    Set<Integer> dom = getDomain(i, j);
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
                        if (grid[root * rb + k][root * cb + l] != empty) {
                            continue;
                        }
                        Set<Integer> dom = getDomain(root*rb + k, root*cb + l);
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

    private boolean dfs(int r, int c, int recdepth, int forcedNum) {
        if (recdepth > maxRecdepth) {
            maxRecdepth = recdepth;
        }
        reccalls+=1;
        if (reccalls > 10000000)
            return false;
        if (r == -1 || c == -1) {
            //System.out.println("done in dfs!");
            return true;
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
            Set<Integer> set = union(r, c);
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
        }
//        if (freqs.size() == 0 || freqs.get(0)[1] >= 13) {
//            return false;
//        }
        //System.out.println(String.valueOf(freqs.size()));

        for (int i = 0; i < freqs.size(); i++) {
            int num = freqs.get(i)[0];
            char cc = getChar(num);
            //System.out.println("assign " + cc);
            grid[r][c] = cc;
            add(r, c, num, true);
            if (recdepth % 20 == 0 && recdepth != lastRecdepth) {
                lastRecdepth = recdepth;
                System.out.println("GOING IN: " + Integer.valueOf(recdepth).toString());
                show(grid);
            }

            int[] forced = getForcedVal();
            if (forced[0] != -1) {
                System.out.println(Arrays.toString(forced));
                if (recdepth > 100) {
                    return false;
                }
                if (dfs(forced[0], forced[1], recdepth + 1, forced[2])) {
                    return true;
                }
            }
            else {
                int[] pos = getIndex();
                if (dfs(pos[0], pos[1], recdepth + 1, -1)) {
                    return true;
                }
            }
            //System.out.println("change for (" + r + "," + c + ")");
            add(r, c, num, false);
            grid[r][c] = empty;
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
        return false;
    }
    
    public boolean solveSudoku(char[][] board) {
        grid = board;
        System.out.println("Initializing");
        double startTime = System.nanoTime();
        if (!init()) {
            return false;
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
        int[] pos = getIndex();
        boolean ret = dfs(pos[0], pos[1], 0, -1);
        System.out.println("recursive calls: " + reccalls.toString());
        System.out.println("recursive depth: " + maxRecdepth.toString());

        return ret;
    }
    
}
