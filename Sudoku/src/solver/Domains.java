package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;


public class Domains {
    private int size;
    private int root;
    private static final int DEFAULT = 0;
    private static final int SECOND = 1;
    private int varmode;
    private final char empty = '0';
    private Places places;


    // 2D ArrayList whose entry at each cell is the domain of the cell, itself an ArrayList of Integers
    private ArrayList<ArrayList<ArrayList<Integer>>> domain;
    // 2D ArrayList whose entry at each cell is an object storing the size of the domain and some other information
    private ArrayList<ArrayList<DomainSizeEntry>> domainSize;
    // Used for ordering domain sizes in the queue
    private Comparator<DomainSizeEntry> domainSizeComparator;
    // Used for identifying the MRV
    public PriorityQueue<DomainSizeEntry> sizeQueue;

    private int getBox(int r, int c) {
        return Util.getBox(r, c, root);
    }

    private ArrayList<int[]> getBoxPositions(int box) {
        return Util.getBoxPositions(box, root);
    }


    public void fillIn(int r, int c) {
        DomainSizeEntry e = domainSize.get(r).get(c);
        e.isEmpty = false;
        sizeQueue.remove(e);
        sizeQueue.add(e);
    }

    public void unfill(int r, int c) {
        DomainSizeEntry se = domainSize.get(r).get(c);
        se.isEmpty = true;
        sizeQueue.remove(se);
        sizeQueue.add(se);
    }

    public Domains(int size, int root, int varmode, Places places) {
        this.size = size;
        this.root = root;
        this.varmode = varmode;
        this.places = places;
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
    }

    public boolean initDomain(char[][] grid) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != empty) {
                    ArrayList<Integer> nums = new ArrayList<Integer>();
                    int num = Util.getNum(grid[i][j]);
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
                    if (pruneDomains(i, j, Util.getNum(grid[i][j]), grid).hasZero) {
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
    public int[] setDomain(int i, int j, ArrayList<Integer> nums) {
        int[] change = new int[4];
        change[0] = i;
        change[1] = j;
        DomainSizeEntry domSizeE = domainSize.get(i).get(j);
        change[2] = domSizeE.size;
        ArrayList<Integer> dom = domain.get(i).get(j);

        ArrayList<Integer> numsToRemove = new ArrayList<Integer>();
        ArrayList<Integer> numsToAdd = new ArrayList<Integer>();
        if (varmode == SECOND) {
            for (int num : nums) {
                if (dom.indexOf(num) >= domSizeE.size) {
                    numsToAdd.add(num);
                }
            }

            for (int k = 0; k < domSizeE.size; k++) {
                int domNum = dom.get(k);
                if (nums.indexOf(domNum) == -1) {
                    numsToRemove.add(domNum);
                }
            }
        }
        for (int num : nums) {
            dom.remove(dom.indexOf(num));
            dom.add(0, num);
        }
        int numsSize = nums.size();
        domSizeE.size = numsSize;

        if (varmode == SECOND) {
            places.updateFromChange(i, j, getBox(i, j), numsToAdd, numsToRemove);
        }

        change[3] = domSizeE.size;

        DomainSizeEntry e =  domainSize.get(i).get(j);
        sizeQueue.remove(e);
        sizeQueue.add(e);
        return change;
    }


    public int[] removeFromDomain(int r, int c, int num) {
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

    public ArrayList<Integer> getDomainVals(int r, int c) {
        ArrayList<Integer> vals = new ArrayList<Integer>();
        int domSize = domainSize.get(r).get(c).size;
        for (int i = 0; i < domSize; i++) {
            vals.add(domain.get(r).get(c).get(i));
        }
        return vals;
    }

    public PruneResult pruneDomains(int r, int c, int num, char[][] grid) {
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

    public void updateFromChanges(Set<int[]> changes) {
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
    }

    public int getSize(int row, int col) {
        return domainSize.get(row).get(col).size;
    }

    public ArrayList<Integer> getDomain(int row, int col) {
        return domain.get(row).get(col);
    }

    public DomainSizeEntry peek() {
        return sizeQueue.peek();
    }

    public boolean isInDomain(int r, int c, int num) {
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

}