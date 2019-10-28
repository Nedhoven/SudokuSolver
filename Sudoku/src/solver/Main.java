
package solver;

import java.util.Arrays;

public class Main {
    
    public static void main(String[] args) {
        boolean runFlag = true;
        //runTest();
        if (runFlag) {
            char[][] board = getBoard();
            double startTime = System.nanoTime();
            calculate(board);
            double endTime = System.nanoTime();
            double time = (endTime - startTime) / 1000;
            System.out.println("running time: " + time + " us!");
            System.out.println();
            System.out.println("answer:");
            show(board);
        }
    }
    
    public static char[][] getBoard() {
        Example ex = new Example(9);
        return ex.getGrid();
    }
    
    public static char[][] calculate(char[][] board) {
        int n = board.length;
        Solver s = new Solver(n);
        boolean ans = s.solveSudoku(board);
        if (!ans) {
            System.err.println("SUDOKU UNSOLVEABLE!");
            System.exit(1);
        }
        return board;
    }
    
    public static void show(char[][] board) {
        for (char[] arr : board) {
            System.out.println(Arrays.toString(arr));
        }
    }
    
    public static void runTest() {
        Optimized s = new Optimized(9);
        System.out.println(Arrays.toString(s.getChar()));
    }
    
}
