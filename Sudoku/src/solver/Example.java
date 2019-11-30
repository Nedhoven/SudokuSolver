
package solver;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Example {
    
    private final int size;
    
    public Example(int n) {
        size = n;
    }

    private char adjustChar(char c) {
        if (c == '9') {
            return 'A';
        }
        else {
            return (char)((int)c+1);
        }
//        else if ((int)'0' <= (int)c <= (int)'8')
//        if (num == (9) {
//            return (char) (num + '1');
//        }
//        else if (num == 9) {
//            return 'A';
//        }
//        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }


    public char[][] getGrid(String filename) {
        switch (size) {
//            case 9: {
//                char[][] board = {
//                        {'1', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'2', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'3', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'0', '0', '5', '0', '0', '0', '0', '0', '0'},
//                        {'6', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'7', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'8', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'9', '0', '0', '0', '0', '0', '0', '0', '0'},
//                        {'0', '0', '0', '0', '0', '0', '0', '0', '0'}
//                };
//                return board;
//            }
            case 9:
            {
//                char[][] board = {
//                    {'5', '3', '0', '0', '7', '0', '0', '0', '0'},
//                    {'6', '0', '0', '1', '9', '5', '0', '0', '0'},
//                    {'0', '9', '8', '0', '0', '0', '0', '6', '0'},
//                    {'8', '0', '0', '0', '6', '0', '0', '0', '3'},
//                    {'4', '0', '0', '8', '0', '3', '0', '0', '1'},
//                    {'7', '0', '0', '0', '2', '0', '0', '0', '6'},
//                    {'0', '6', '0', '0', '0', '0', '2', '8', '0'},
//                    {'0', '0', '0', '4', '1', '9', '0', '0', '5'},
//                    {'0', '0', '0', '0', '8', '0', '0', '7', '9'}
//                };
                char[][] board = {
                        {'0', '3', '0', '0', '0', '0', '0', '0', '0'},
                        {'6', '0', '0', '1', '0', '5', '0', '0', '0'},
                        {'0', '0', '8', '0', '0', '0', '0', '6', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '3', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '2', '0', '0'},
                        {'0', '0', '0', '4', '1', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '7', '9'}
                };
                return board;
            }
            case 17:
            {
                char[][] board = new char[16][16];
                try {
                    File f = new File("/mnt/c/Users/danie/Downloads/hexa-20.txt");
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String st;
                    int i = 0;
                    while ((st = br.readLine()) != null) {
                        if (st.length() == 0) continue;
                        st = st.replaceAll("\\s+", " ");
                        String[] parts = st.split(" ");
                        int j = 0;
                        for (String part : parts) {
                            if (part.length() == 0) continue;
//                            System.out.println(part);
                            //if (part.replaceAll("\\s+", " ") == "-1") {
                            if (part.charAt(0) == '-') {
                                 board[i][j] = '0';
                            }
                            else {
                                board[i][j] = adjustChar(part.charAt(0));
                            }
                            j++;
                        }
                        i++;
                    }
//                    for (int r = 0; r < 16; r++) {
//                        System.out.println();
//                        for (int j = 0; j < 16; j++) {
//                            System.out.print(" " + String.valueOf(board[r][j]) + " ");
//                        }
//                    }
                }
                catch (FileNotFoundException e) {
                    System.out.println("FILE NOT FOUND");
                }
                catch (IOException e) {
                    System.out.println("IO EXCEPTION");
                }

//                {
//                    {'0', '2', 'E', '0', '0', '0', 'G', '4', '0', '0', '0', '1', '0', '0', '5', '0'},
//                    {'0', '0', '9', '0', '0', 'A', '0', '1', '0', '0', '0', '0', '0', '4', '0', '0'},
//                    {'0', '0', '0', '0', 'D', '6', '0', '0', '0', 'E', '0', '0', 'F', 'C', '0', 'G'},
//                    {'6', '5', 'A', '0', '8', '2', '0', '0', '0', 'C', '0', '0', '0', '1', '0', '7'},
//                    {'9', '0', '5', '4', '1', '0', '0', '2', '0', '0', '0', '0', 'C', '0', '7', '0'},
//                    {'0', '0', '0', '0', 'B', '0', '0', 'D', '0', '3', '0', '0', '0', '0', '0', '1'},
//                    {'0', '0', '0', '0', 'G', '0', '0', '0', 'D', 'A', 'F', '9', 'E', '0', '4', '0'},
//                    {'A', '0', '0', 'B', '0', '4', '8', 'F', '0', '0', '0', '0', '5', '0', 'D', '0'},
//                    {'0', 'B', '0', '1', '0', '0', '0', '0', 'A', '7', '4', '0', '3', '0', '0', '6'},
//                    {'0', '7', '0', '2', 'E', 'G', '6', 'A', '0', '0', '0', 'B', '0', '0', '0', '0'},
//                    {'G', '0', '0', '0', '0', '0', '1', '0', 'C', '0', '0', 'E', '0', '0', '0', '0'},
//                    {'0', '4', '0', 'A', '0', '0', '0', '0', 'F', '0', '0', '2', 'G', '5', '0', 'B'},
//                    {'B', '0', 'C', '0', '0', '0', 'E', '0', '0', '0', 'D', '7', '0', '9', '6', '2'},
//                    {'8', '0', '7', '9', '0', '0', 'B', '0', '0', '0', 'E', 'A', '0', '0', '0', '0'},
//                    {'0', '0', '4', '0', '0', '0', '0', '0', 'B', '0', '2', '0', '0', '8', '0', '0'},
//                    {'0', '6', '0', '0', 'C', '0', '0', '0', '9', '8', '0', '0', '0', 'E', '1', '0'}
//                };
                return board;
            }
            default:
            {
//                char[][] board = new char[size][size];
//                try {
//                    if (filename == null) {
//                        filename = "board_2525_100_2.txt";
//                    }
////                    File f = new File("/mnt/c/Users/danie/Downloads/board_0.txt");
//                    File f = new File("/mnt/c/Users/danie/cs271/" + filename);
//                    BufferedReader br = new BufferedReader(new FileReader(f));
//                    String st;
//                    int i = 0;
//                    while ((st = br.readLine()) != null) {
//                        if (st.length() == 0) continue;
//                        st = st.replaceAll("\\s+", " ");
//                        String[] parts = st.split(" ");
//                        int j = 0;
//                        for (String part : parts) {
//                            if (part.length() == 0) continue;
////                            System.out.println(part);
//                            //if (part.replaceAll("\\s+", " ") == "-1") {
//                            board[i][j] = part.charAt(0);
////                            if (part.charAt(0) == '-') {
////                                board[i][j] = '0';
////                            }
////                            else {
////                                board[i][j] = adjustChar(part.charAt(0));
////                            }
//                            j++;
//                        }
//                        i++;
//                    }
////                    for (int r = 0; r < 16; r++) {
////                        System.out.println();
////                        for (int j = 0; j < 16; j++) {
////                            System.out.print(" " + String.valueOf(board[r][j]) + " ");
////                        }
////                    }
//                }
//                catch (FileNotFoundException e) {
//                    System.out.println("FILE NOT FOUND");
//                }
//                catch (IOException e) {
//                    System.out.println("IO EXCEPTION");
//                }
//
                char[][] board = {
                    {'0', '0', 'C', '6', '0', '0', '7', '0', 'I', '0', '5', 'O', '0', 'A', '1', '0', '0', '4', '0', '0', '0', '0', '0', '0', '0'},
                    {'2', '0', 'J', '0', 'D', '0', '0', '0', 'A', '0', '0', '0', '0', '0', '0', '0', '0', 'I', '5', '0', '0', '0', '0', '0', '1'},
                    {'0', '0', '0', '0', '0', '0', '0', 'M', '0', '0', '0', '0', '3', '0', '2', '0', '0', 'E', 'C', '0', 'G', '8', 'P', '0', '0'},
                    {'0', 'G', '0', '0', '0', '2', 'N', '0', '0', 'D', 'C', 'M', '0', '0', '0', 'L', 'F', 'J', '3', '0', '0', '0', '0', 'E', '0'},
                    {'N', '0', 'O', '0', '0', '0', '0', '0', 'P', '8', '4', '0', 'G', 'J', 'L', '0', '0', '7', '0', '0', '0', '3', 'C', '0', '9'},
                    {'0', '4', '0', '2', '0', '0', '0', '0', '0', '0', '0', 'A', '0', 'O', 'C', 'H', 'G', '0', '0', '0', '5', '0', '0', '0', '0'},
                    {'0', '0', '9', '0', '0', '6', 'P', '0', '0', '0', '8', '0', '5', '3', '0', '0', '0', '0', '0', '0', 'K', '0', '0', 'I', 'J'},
                    {'F', '0', 'A', 'B', '0', '0', '0', 'I', 'C', 'J', '0', '0', '0', '0', '0', '0', '0', 'N', '0', '0', '7', '0', '0', '4', '0'},
                    {'0', '0', '0', '0', '0', '0', '0', 'E', '0', 'M', '0', '0', 'I', 'G', 'K', '0', '6', 'B', 'D', '0', '0', '0', '0', '0', '0'},
                    {'0', 'M', '0', 'P', '0', '0', '1', 'H', '5', '4', '7', '0', '0', 'E', '0', '8', '3', 'L', '0', '0', 'B', '0', '0', '0', '6'},
                    {'0', 'K', 'D', 'F', '0', '0', '0', '0', '0', '0', '9', '0', '0', '2', '0', 'P', '0', '1', '8', '0', '0', '5', '0', 'L', '0'},
                    {'0', '1', '0', '0', '0', '0', 'G', 'A', '0', '7', '0', '0', '4', 'K', '0', '0', '9', '0', '0', 'E', '0', 'O', '0', 'H', '0'},
                    {'P', '2', '5', '0', '0', '0', '0', '0', 'D', '0', '0', '0', '0', '0', 'M', '0', '0', '0', '0', '0', 'J', '1', '8', '0', '0'},
                    {'0', '0', '7', 'L', '0', '0', 'C', '0', '2', 'H', '0', '0', '0', 'I', '6', 'G', '0', '0', 'F', '0', '0', 'D', '0', 'A', '0'},
                    {'8', 'A', 'I', 'C', 'G', '9', '0', '0', '0', '5', '0', '0', '0', '0', 'J', '0', '0', 'H', '0', 'L', '0', 'F', '0', '0', 'M'},
                    {'0', '8', '0', '0', 'F', '0', '3', '0', '6', '0', 'L', '0', '0', '7', '0', 'I', 'E', '5', '0', '1', '0', '0', '0', '0', '0'},
                    {'0', '0', '0', 'J', '0', '1', '0', 'G', 'B', '0', '0', '0', 'A', 'M', 'P', 'F', '0', '0', '0', '0', '0', '0', 'L', '0', '0'},
                    {'0', '3', '1', '0', 'L', '0', '0', '4', '0', '0', '0', '0', '2', '0', 'D', '0', 'O', 'P', '0', '0', 'E', '0', '0', '6', '0'},
                    {'0', '0', '0', '0', '0', '0', '0', 'F', '0', 'C', 'E', '0', '6', 'H', 'O', '0', '0', '0', '0', '0', '0', '0', 'D', '0', '0'},
                    {'0', '5', 'N', 'G', '4', '0', 'D', 'O', '7', '2', '0', '9', '0', '0', 'F', '3', '0', 'M', '0', '0', '0', '0', '0', '0', '8'},
                    {'0', '0', 'P', 'K', '2', '0', 'J', '0', '0', '0', '0', '1', '0', '0', '0', '0', 'L', '3', '0', '0', 'C', '0', '0', '0', '0'},
                    {'G', 'C', '0', '5', '0', 'B', 'L', '0', 'N', '0', '0', 'F', '0', '0', '0', '0', 'J', '9', '0', '0', '0', '0', '0', 'P', 'A'},
                    {'0', '0', '0', '0', '9', 'K', 'M', '7', '4', '0', '3', '0', 'E', 'P', 'I', '0', 'B', '0', '0', '0', '0', '0', '1', '0', 'F'},
                    {'O', '0', '6', '0', 'M', '8', '0', 'P', 'E', '0', 'A', 'B', '0', '9', '0', 'K', '1', 'G', '0', '7', '0', 'N', '0', '0', 'D'},
                    {'E', 'D', 'L', '1', '0', '0', '5', '0', '0', '0', '6', '0', 'M', '0', 'N', 'A', '0', '0', '0', '2', '0', '0', 'I', '7', 'B'}
                };
                return board;
            }
        }
    }
    
    public char[][] emptyGrid() {
        char[][] res = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[i][j] = '0';
            }
        }
        return res;
    }
    
}
