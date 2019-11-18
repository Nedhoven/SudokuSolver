
package solver;


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
    
    public Solver() {
        size = 25;
        root = 5;
    }
    
    public Solver(int n) {
        size = n;
        root = (int) Math.sqrt(n);
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
                }
            }
        }
    }
    
    private boolean dfs(int row, int col, int recdepth) {
        if (recdepth > maxRecdepth) {
            maxRecdepth = recdepth;
        }
        reccalls+=1;
        if (reccalls > 100000) {
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
            return dfs(row, col + 1, recdepth + 1);
        }
        //System.out.println("row = " + row);
        for (int num = 0; num < size; num++) {
            if (check(row, col, num)) {
                add(row, col, num, true);
                grid[row][col] = getChar(num);
                numRow[row]++;
                if (dfs(row, col + 1, recdepth + 1)) {
                    return true;
                }
                //System.out.println("backtrack on row = " + row);
                numRow[row]--;
                grid[row][col] = empty;
                add(row, col, num, false);
            }
        }
        return false;
    }
    
    public boolean solveSudoku(char[][] board) {
        grid = board;
        flag = false;
        numRow = new int[size];
        row = new boolean[size][size];
        col = new boolean[size][size];
        box = new boolean[size][size];
        init();
        if (flag) {
            return false;
        }
        int r = getRow();
        boolean ret = dfs(r, 0, 0);
        System.out.println("Recursive calls: " + reccalls.toString());
        System.out.println("Recursive depth: " + maxRecdepth.toString());
        return ret;
    }
    
}
