
package solver;

import java.util.Arrays;

public class Main {
    
    public static void main(String[] args) {
        boolean runFlag = true;
        //runTest();
        boolean useOptimized;
        if (args.length > 0) {
            useOptimized = true;
        }
        else {
            useOptimized = false;
        }
        if (runFlag) {
            String filename = null;
            if (args.length >= 2) {
                filename = args[1];
            }
            char[][] board = getBoard(filename);
            double startTime = System.nanoTime();
            char[][] ans = calculate(board, useOptimized);
            double endTime = System.nanoTime();
            double time = (endTime - startTime) / 1000;
            System.out.println("running time: " + time + " us!");
            System.out.println();
            System.out.println("answer:");
            show(ans);
        }
    }
    
    public static char[][] getBoard(String filename) {
        Example ex = new Example(16);
        return ex.getGrid(filename);
    }
    
    public static char[][] calculate(char[][] board, boolean useOptimized) {
        int n = board.length;
        Solver s = new Solver(16);
        show(board);
        char[][] ans = s.solveSudoku(board);
        if (ans == null) {
            System.err.println("SUDOKU UNSOLVEABLE!");
            System.exit(1);
        }
        return ans;
    }
    
    public static void show(char[][] board) {
        for (char[] arr : board) {
            System.out.println(Arrays.toString(arr));
        }
    }
}
