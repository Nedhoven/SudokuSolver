
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
        Example ex = new Example(25);
        return ex.getGrid(filename);
    }
    
    public static char[][] calculate(char[][] board, boolean useOptimized) {
        int n = board.length;
        SolverInterface s;
        if (useOptimized) {
            s = new Refactored(25);
        }
        else {
            s = new Solver(n);
        }
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
    
    public static void runTest() {
//        Optimized s = new Optimized(9);
//        char[][] board = getBoard();
//        double startTime =
//        boolean ans = s.solveSudoku(board);
//        System.out.println(Arrays.toString(s.getChar()));
    }
    
}
