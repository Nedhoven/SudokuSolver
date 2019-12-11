
package solver;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static int N;
    public static void main(String[] args) throws IOException {
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
            if (args.length < 3) {
                System.err.println("Expected three arguments: input file, output file, and N (e.g. 9, 16, or 25)");
            }
            N = Integer.valueOf(args[2]);
            IO io = new IO(args[0], args[1], N);
            //char[][] board = getBoard(filename);
            char[][] board = io.readChar("");
            double startTime = System.nanoTime();
            char[][] ans = calculate(board, useOptimized);
            double endTime = System.nanoTime();
            double time = (endTime - startTime) / 1000;
            System.out.println("running time: " + time + " us!");
            System.out.println();
            System.out.println("answer:");
            show(ans);
            io.writeSerial(ans, "");
        }
    }
    
    public static char[][] getBoard(String filename) {
        Example ex = new Example(N);
        return ex.getGrid(filename);
    }
    
    public static char[][] calculate(char[][] board, boolean useOptimized) {
        int n = board.length;
        Solver s = new Solver(N);
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
